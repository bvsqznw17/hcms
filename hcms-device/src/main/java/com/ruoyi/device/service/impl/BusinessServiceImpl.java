package com.ruoyi.device.service.impl;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.base.mapper.RegLibMapper;
import com.ruoyi.common.constant.MbTranType;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.modbus.ModbusUtils;
import com.ruoyi.connDev.auth.OperateStatusMap;
import com.ruoyi.connDev.modbus.ModbusSocketManage;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.mapper.DevMsgMapper;
import com.ruoyi.device.mapper.ParamValueMapper;
import com.ruoyi.device.service.IBusinessService;

@Service
public class BusinessServiceImpl implements IBusinessService {

    private static final String KEY_PREFIX = "mb:";

    // @Autowired
    // private BusinessMapper businessMapper;
    @Autowired
    RedisCache redisCache;

    @Autowired
    private DevMsgMapper devMsgMapper;

    @Autowired
    private ParamValueMapper paramValueMapper;

    @Autowired
    private RegLibMapper regLibMapper;

    @Override
    public AjaxResult checkDevStatus(String devName, String userId) {
        try {

            // 1.获取设备的在线状态判断设备是否在线
            DevMsg devMsg = devMsgMapper.selectDevMsgByName(devName);
            if (devMsg == null || devMsg.getStatus() != 1) {
                return AjaxResult.success("已离线");
            }

            // 如果设备超过5分钟没有被操作，则对设备解锁
            if (new Date().getTime() - devMsg.getDtUpdate().getTime() > 5 * 60 * 1000) {
                OperateStatusMap.removeOperateStatusMap(devName);
            }

            // 2.获取设备的状态判断设备是否可以被操作
            Socket sct = ModbusSocketManage.getServerConClientThread(devMsg.getDevName()).getSocket();
            String value = ModbusUtils.ReadHoldingRegister(sct, 821, 1, "U16");
            int valueInt = Integer.parseInt(value == null ? "0" : value);
            boolean canOperate = (valueInt & 0x0001) == 0;

            // 3.如果设备可以被操作，则检查设备是否已经被操作，通过operateStatusMap判断设备是否已经被操作
            if (canOperate) {
                if (OperateStatusMap.getOperateStatusMap().containsKey(devName)) {
                    String operateUserId = OperateStatusMap.getOperateStatusMap().get(devName);
                    if (operateUserId.equals(userId)) {
                        return AjaxResult.success("200");
                    } else {
                        return AjaxResult.success("被其他用户操作");
                    }
                } else {
                    OperateStatusMap.addOperateStatusMap(devName, userId);
                }
            } else {
                return AjaxResult.success("运行中");
            }

            // 如果设备可以被操作，则返回设备的信息
            return AjaxResult.success("200");

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.success("不可操作");
        }
    }

    @Override
    public int changeDevStatus(String devName, String operateStatus) {
        // 1.根据devName获取设备信息
        DevMsg devMsg = devMsgMapper.selectDevMsgByName(devName);
        if (devMsg == null) {
            return 0;
        }
        // 2.修改设备的OperateStatus
        int status = 0;
        if (operateStatus != null) {
            status = Integer.parseInt(operateStatus);
        }
        devMsg.setOperateStatus(status);
        return devMsgMapper.updateDevMsg(devMsg);
    }

    /**
     * 给设备发送指令
     */
    @Override
    public int writeCmd(String devName, String cmd, String cmdParam) {
        try {
            Socket sct = ModbusSocketManage.getServerConClientThread(devName).getSocket();
            ModbusUtils.writeCmdParam(sct, cmd, cmdParam);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 读取设备的参数
     */
    // @Override
    // public AjaxResult readParam(String devName, String paramKey) {
    // try {
    // // 查询 paramValue 对象
    // List<ParamValue> paramValueList = paramValueMapper.selectParamValueList(new
    // ParamValue(devName, paramKey));
    // if (paramValueList.size() == 0) {
    // return AjaxResult.success("200");
    // }
    // ParamValue paramValue = paramValueList.get(0);

    // // 读取设备的参数
    // Socket socket =
    // ModbusSocketManage.getServerConClientThread(devName).getSocket();
    // byte[] bytes = ModbusUtils.ReadHoldingRegister2(socket,
    // paramValue.getRegAddr(), paramValue.getRegNum());

    // // 获取 regLibs
    // List<RegLib> regLibs = regLibMapper.selectRegLibList(null);

    // // 处理读取到的数据
    // List<HashMap<String, Object>> res = processBytes(bytes, paramValue, regLibs);

    // return AjaxResult.success(res);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return AjaxResult.error("读取设备参数失败");
    // }
    // }

    /**
     * 读取设备的参数
     */
    @Override
    public AjaxResult readParam(String devName, String paramKey) {
        HashMap<String, Object> map = new HashMap<>();
        // 从redis中获取设备的参数值
        Object pv = redisCache.getCacheObject(paramKey);
        map.put(paramKey, pv);
        return AjaxResult.success(map);
    }

    /**
     * 读取一组设备的参数
     */
    @Override
    public AjaxResult readParams(String devName, String[] paramKeys) {
        HashMap<String, Object> map = new HashMap<>();
        // 从redis中获取设备的参数值
        for (String paramKey : paramKeys) {
            Object pv = redisCache.getCacheObject(KEY_PREFIX + paramKey);
            map.put(paramKey, pv);
        }
        return AjaxResult.success(map);
    }

    private List<HashMap<String, Object>> processBytes(byte[] bytes, ParamValue paramValue, List<RegLib> regLibs) {
        List<HashMap<String, Object>> res = new ArrayList<>();

        HashMap<Integer, String> regAddrParamKeyMap = getParamKeyByRegAddr(regLibs);

        int i = 0;
        while (i < bytes.length) {
            HashMap<String, Object> map = new HashMap<>();
            String paramKey = regAddrParamKeyMap.get(paramValue.getRegAddr() + i);

            switch (paramValue.getDataType()) {
                case MbTranType.U8:
                    // 每个字节转成一个整型
                    map.put(paramKey, bytes[i]);
                    i++;
                    break;
                case MbTranType.U16:
                    // 每2个字节转为一个short类型
                    map.put(paramKey, (short) ((bytes[i] << 8) | (bytes[i + 1] & 0xff)));
                    i += 2;
                    break;
                case MbTranType.U8A:
                    // 将整个字节数组转为字符串
                    map.put(paramKey, new String(bytes, i, bytes.length - i));
                    i = bytes.length; // 结束循环
                    break;
            }

            res.add(map);
        }

        return res;
    }

    /**
     * 构建一个regAddr对应的paramKey的map
     */
    private HashMap<Integer, String> getParamKeyByRegAddr(List<RegLib> regLibs) {
        HashMap<Integer, String> map = new HashMap<>();
        for (RegLib regLib : regLibs) {
            map.put(regLib.getRegAddr(), regLib.getParamKey());
        }
        return map;
    }

    // 获取设备的运行操作状态
    @Override
    public AjaxResult getRunStatus(String devName) {
        // 通过modbus发送状态查询请求
        return AjaxResult.success(OperateStatusMap.getOperateStatusMap().get(devName));
    }

    // 获取组合面板的数据 @Override
    public AjaxResult getPanelData(String devName) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        // 从Redis获取整数值
        int sysDotNum = Integer.parseInt(getCacheObjectStr("sys_dot_num"));

        // 读取所需要的面板参数并填充到map中
        // 1、4、7
        map.put("程序号", getCacheObject("sys_prm_ids"));
        map.put("目标值", getDotStr(sysDotNum, getCacheObject("prm_SetWeight")));
        map.put("平均斗数", getAverageNumber(readSingleParam(devName, 819, 1, MbTranType.U16)));

        // 2、5、8
        map.put("产品名称", getCacheObject("prm_name"));
        map.put("上限值", getDotStr(sysDotNum, getCacheObject("prm_SetWeight1")));
        map.put("设置速度", getCacheObject("prm_speed"));

        // 3、6、9
        map.put("单位重量", getCacheObject("sys_Unit"));
        map.put("下限值", getDotStr(sysDotNum, getCacheObject("prm_SetWeight2")));
        map.put("实际速度", getCacheObject("cmb_speed"));

        // Remaining
        map.put("weight", getCacheObject("cmb_weight"));
        map.put("sys_unit", getUnit(getCacheObjectStr("sys_Unit")));
        map.put("afc", getAFCStr(getCacheObjectStr("prm_AFC")));

        return AjaxResult.success(map);
    }

    private Object getCacheObject(String key) {
        return redisCache.getCacheObject(KEY_PREFIX + key);
    }

    private String getCacheObjectStr(String key) {
        if (getCacheObject(key) == null) {
            return "";
        } else {
            return getCacheObject(key).toString();
            
        }
    }

    // 从Redis获取整数值，并处理可能的类型异常
    private int getRedisIntValue(String key) {
        Object obj = redisCache.getCacheObject(key);
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return 0; // 或其他默认值
    }

    // 根据dotNum格式化String类型的数值: 比如value是11，dotNum是2，那么就是11.11
    private String getDotStr(Integer dotNum, Object value) {
        String res = value.toString();
        if (dotNum > 0 && value != null) {
            res = String.format("%." + dotNum + "f", Float.parseFloat(res));
        }
        return res;
    }

    // 根据系统单位值返回合适的单位
    private String getUnit(String unit) {
        /* 系统单位 */
        switch (unit) {
            case "0":
                return "g";
            case "1":
                return "kg";
            case "2":
                return "lb";
            case "3":
                return "oz";
            default:
                return "g";
        }
    }

    // 将avgNum转化为所需的平均数格式
    private String getAverageNumber(String avgNumStr) {
        try {
            Integer avgNum = Integer.parseInt(avgNumStr);
            return String.format("%.1f", (avgNum >> 8) / 10.0);
        } catch (NumberFormatException e) {
            return "0"; // 或其他默认值
        }
    }

    // 获取斗状态数据
    @Override
    public AjaxResult getDouStatus(String devName) {
        // 通过modbus发送状态查询请求
        try {
            // 读取设备的参数
            Socket socket = ModbusSocketManage.getServerConClientThread(devName).getSocket();
            byte[] bytes = ModbusUtils.ReadHoldingRegister(socket, 902, 80);

            // 处理读取到的数据：遍历bytes，每两个字节转为一个short
            System.out.println("读取到的斗状态长度：" + bytes.length);
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < bytes.length; i += 2) {
                list.add((bytes[i] << 8) | (bytes[i + 1] & 0xff));
            }

            return AjaxResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("读取设备参数失败");
        }
    }

    // 读取单个参数
    private String readSingleParam(String devName, Integer addr, int regNum, String dataType) {
        try {
            // 读取设备的参数
            Socket socket = ModbusSocketManage.getServerConClientThread(devName).getSocket();
            String res = ModbusUtils.ReadHoldingRegister(socket, addr, regNum,
                    dataType);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    // 获取指定设备的系统小数位数
    private Integer getSysDotNum(String devName) {
        // 查询设备中的sys_dot_num
        ParamValue pv2 = paramValueMapper.getDotNumParamValueByDevName(devName);
        Integer dotNum = 0;
        if (null != pv2) {
            dotNum = pv2.getParamValue() != null ? Integer.parseInt(pv2.getParamValue()) : 0;
        }
        return dotNum;
    }

    // 格式化AFC参数
    public static String getAFCStr(String n) {
        switch (n) {
            case "1":
                return "AFCT";
            case "2":
                return "AFCI";
            case "3":
                return "AFCR";
            default:
                return "";
        }
    }

}
