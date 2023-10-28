package com.ruoyi.device.service.impl;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.base.domain.RegLib;
import com.ruoyi.base.mapper.RegLibMapper;
import com.ruoyi.common.constant.CtrlNo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.modbus.ModbusUtils;
import com.ruoyi.common.utils.modbus.ModbusUtilsBak;
import com.ruoyi.connDev.modbus.ModbusSocketManage;
import com.ruoyi.device.domain.DevList;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.mapper.DevListMapper;
import com.ruoyi.device.mapper.ParamValueMapper;
import com.ruoyi.device.service.IParamValueService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.statistic.domain.DevChangeLog;
import com.ruoyi.statistic.mapper.DevChangeLogMapper;

import cn.hutool.core.thread.ThreadUtil;

/**
 * 设备参数Service业务层处理
 * 
 * @author cbw
 * @date 2022-10-14
 */
@Service
public class ParamValueServiceImpl implements IParamValueService {
    @Autowired
    private ParamValueMapper paramValueMapper;

    @Autowired
    private RegLibMapper regLibMapper;

    @Autowired
    private DevListMapper devListMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DevChangeLogMapper logMapper;

    // 上锁
    private ReentrantLock lock = new ReentrantLock();

    // 定义常量
    private static final int MK_NUM_ADDR = 982;
    private static final int MK_NUM_REG_START = 983;
    private static final int MM_REG_START = 514;
    private static final int MM_TOTAL_NUM = 62;
    private static final int MM_GD_ADDR = 574;
    private static final int MM_WGD_ADDR = 575;

    /**
     * 查询设备参数
     * 
     * @param id 设备参数主键
     * @return 设备参数
     */
    @Override
    public ParamValue selectParamValueById(Long id) {
        return paramValueMapper.selectParamValueById(id);
    }

    /**
     * 查询设备参数列表
     * 
     * @param paramValue 设备参数
     * @return 设备参数
     */
    @Override
    public List<ParamValue> selectParamValueList(ParamValue paramValue) {
        return paramValueMapper.selectParamValueList(paramValue);
    }

    /**
     * 新增设备参数
     * 
     * @param paramValue 设备参数
     * @return 结果
     */
    @Override
    public int insertParamValue(ParamValue paramValue) {
        return paramValueMapper.insertParamValue(paramValue);
    }

    /**
     * 修改设备参数
     * 
     * @param paramValue 设备参数
     * @return 结果
     */
    @Override
    public int updateParamValue(ParamValue paramValue) {
        int num = 0;
        try {
            // 同步下发修改指令到指定的机器
            // RegLib lib = paramValueMapper.getRegLibByRP(paramValue);
            System.out.println(paramValue.toString());
            lock.lock();
            Socket sct = ModbusSocketManage.getServerConClientThread(paramValue.getDevName()).getSocket();
            // if (lib != null) {
            // ModbusUtils.WriteMultipleRegisters(sct, lib.getRegAddr(), lib.getRegNum(),
            // lib.getDataType(),
            // paramValue.getParamValue());
            // } else {
            ModbusUtils.WriteMultipleRegisters(sct, paramValue.getRegAddr(), paramValue.getRegNum(),
                    paramValue.getDataType(),
                    paramValue.getParamValue());
            // }
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
            lock.unlock();
            return 0;
        }
        try {
            ParamValue pv = new ParamValue();
            // 如果id不存在，则使用devName和regAddr查询
            if (paramValue.getId() != null) {
                pv = paramValueMapper.selectParamValueById(paramValue.getId());
            } else {
                pv = paramValueMapper.getParamValueByDevNameAndRegAddr(paramValue.getDevName(),
                        paramValue.getRegAddr() + "");
            }
            // 如果查询不到，说明是新增的参数，直接插入
            if (pv != null) {
                num = paramValueMapper.updateParamValue(paramValue);
            } else {
                num = paramValueMapper.insertParamValue(paramValue);
                // 以便于记录日志
                pv = new ParamValue();
                pv.setParamValue("0");
            }
            System.out.println("数据库记录修改成功:" + paramValue.getParamName());
            if (num > 0) {
                logChange(pv, paramValue);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 记录paramValue修改日志
     */
    private void logChange(ParamValue paramValue, ParamValue pv) {
        try {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String operator = loginUser.getUser().getNickName();

            String content = operator + "更改了" + paramValue.getDevName() + "的" + paramValue.getParamName() + ":"
                    + pv.getParamValue() + " => " + paramValue.getParamValue();

            DevChangeLog log = new DevChangeLog();
            log.setOperator(operator);
            log.setDevName(paramValue.getDevName());
            log.setDevModel(paramValue.getDevModel());
            log.setParamname(paramValue.getParamName());
            log.setParamvalue(paramValue.getParamValue());
            log.setDtUpdate(new Date());
            log.setContent(content);
            logMapper.insertDevChangeLog(log);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 批量删除设备参数
     * 
     * @param ids 需要删除的设备参数主键
     * @return 结果
     */
    @Override
    public int deleteParamValueByIds(Long[] ids) {
        return paramValueMapper.deleteParamValueByIds(ids);
    }

    /**
     * 删除设备参数信息
     * 
     * @param id 设备参数主键
     * @return 结果
     */
    @Override
    public int deleteParamValueById(Long id) {
        return paramValueMapper.deleteParamValueById(id);
    }

    @Override
    public int collData(ParamValue paramValue) {
        // 获取ip和端口，如果没有指定端口，默认502
        String ip = paramValue.getIp();
        Integer port = paramValue.getPort() != null ? paramValue.getPort() : 502;
        // 测试连接诶是否可用
        if (!IpUtils.telnet(ip, port, 3000)) {
            addOrUpdDevList(ip, port, false);
            return 0;
        }
        addOrUpdDevList(ip, port, true);
        // 创建连接
        ModbusUtilsBak.initModbusTcpMaster(ip, port);
        // 获取基础参数列表
        List<RegLib> reglibs = regLibMapper.selectRegLibList(null);
        for (RegLib regLib : reglibs) {
            // 跳过保留字段
            if (regLib.getParamName().equals("保留"))
                continue;
            // 执行异步任务
            ThreadUtil.execAsync(() -> {
                try {
                    collDataSingleTask(regLib, ip, port, 4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return 1;
    }

    /**
     * 
     * @param regLib
     * @param ip
     * @param port
     * @param tryTimes
     * @throws Exception
     */
    private void addOrUpdDevList(String ip, Integer port, boolean isConnected) {
        int status = isConnected ? 1 : 0;
        // 检查数据库中是否存在该记录
        // 如果不存在就插入，否则就修改
        DevList dl = new DevList();
        dl.setIp(ip);
        List<DevList> dls = devListMapper.selectDevListList(dl);
        if (dls.size() > 0) {
            // update
            dl = dls.get(0);
            dl.setStatus(status);
            devListMapper.updateDevList(dl);
        } else {
            // insert
            dl.setDtCreate(new Date());
            dl.setDtUpdate(new Date());
            dl.setName("未命名");
            dl.setIp(ip);
            dl.setPort(port);
            dl.setStatus(status);
            devListMapper.insertDevList(dl);
        }
    }

    /**
     * 抽离出来做异步任务的逻辑
     * 
     * @param dataType
     * @return
     */
    private void collDataSingleTask(RegLib regLib, String ip, Integer port, int tryTimes) throws Exception {
        if (tryTimes < 0) {
            return;
        }
        // 数据类型转换
        String dataType = transDataType(regLib.getDataType());
        String value = null;
        try {
            value = ModbusUtilsBak.readHoldingRegisters(regLib.getRegAddr(), regLib.getRegNum(), 1, dataType, 0);
            // TODO 特殊值的处理
            // 将采集的值存储起来
            ParamValue pv = new ParamValue();
            pv.setIp(ip);
            pv.setPort(port);
            pv.setRegAddr(regLib.getRegAddr());
            pv.setParamType(regLib.getParamType());
            pv.setParamSubType(regLib.getParamSubType());
            pv.setDtUpdate(new Date());
            pv.setParamName(regLib.getParamName());
            pv.setParamValue(value);
            paramValueMapper.insertParamValue(pv);
        } catch (Exception e) {
            collDataSingleTask(regLib, ip, port, tryTimes - 1);
            if (tryTimes == 1) {
                throw e;
            }
        }
    }

    /**
     * 对数据类型进行转换
     */
    private String transDataType(String dataType) {
        // 如果字符串中包含'[',将后面一部分都替换为A
        if (dataType.contains("[")) {
            dataType.substring(0, dataType.lastIndexOf("["));
            dataType += "A";
        }
        return dataType;
    }

    @Override
    public int deleteParamValueByDevName(String devName) {
        return paramValueMapper.deleteParamValueByDevName(devName);
    }

    @Override
    public int deleteParamValueByIp(String ip) {
        return paramValueMapper.deleteParamValueByIp(ip);
    }

    @Override
    public AjaxResult collModuleStatus(ParamValue paramValue) {
        try {
            String res = null;
            System.out.println(paramValue.toString());
            Socket sct = ModbusSocketManage.getServerConClientThread(paramValue.getDevName()).getSocket();
            // 删除之前的设备参数值
            paramValueMapper.deleteMkParamValueByDevName(paramValue.getDevName());
            // 调用独立的函数处理不同的命令参数
            processParams(sct, paramValue);
            return AjaxResult.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    private void processParams(Socket sct, ParamValue paramValue) throws Exception {
        String[] params = { "0", "1", "2", "3", "4" };
        int baseParamNum = 0;
        while (baseParamNum < 5) {
            // 下发模块状态读取命令
            // ModbusUtils.writeCmd(sct, CtrlNo.CTL_READ_MKSTATUS);
            // 下发读取模块状态参数的命令
            ModbusUtils.writeCmdParam(sct, CtrlNo.CTL_READ_MKSTATUS, params[baseParamNum]);
            // 如果读取到的命令执行结果为0，说明读取成功
            if (waitCommandResult(sct, 1000)) {
                // 读取982寄存器的值，982寄存器会返回模块的数量
                String moduleNum = ModbusUtils.ReadHoldingRegister(sct, MK_NUM_ADDR, 1, "U16");
                System.out.println("模块数量" + moduleNum);

                // 如果模块数量为空，使用默认的值32
                moduleNum = moduleNum.equals("0") ? "14" : moduleNum;

                // 根据模块数量，读取对应的寄存器个数
                byte[] bytes = ModbusUtils.ReadHoldingRegister(sct, MK_NUM_REG_START,
                        6 * Integer.parseInt(moduleNum));
                System.out.println("读取到的值" + bytes);
                System.out.println("bytes的长度" + bytes.length);
                // 处理读取到的值
                handleMkBytes(bytes, params[baseParamNum], paramValue);
                baseParamNum++;
            }
        }
    }

    /**
     * 处理模块状态的bytes
     */
    private void handleMkBytes(byte[] bytes, String param, ParamValue pv) {
        // 当前一次处理没有数据时的处理
        if (bytes.length < 1) {
            return;
        }

        // 每12字节进行一次处理
        for (int i = 0; i < bytes.length; i += 12) {
            // 当剩余字节数小于12时，结束处理
            if (i + 12 > bytes.length) {
                break;
            }
            // 获取版本信息字节
            byte[] version = Arrays.copyOfRange(bytes, i, i + 8);
            // 获取状态字节并转换为短整型
            short status = (short) ((bytes[i + 8] << 8) | (bytes[i + 9] & 0xff));

            String versionString;
            // 尝试将版本信息字节转成utf8字符串
            try {
                versionString = new String(version, StandardCharsets.UTF_8);
            } catch (Exception e) {
                // 出错时打印异常并进行下一循环
                e.printStackTrace();
                continue;
            }

            StringBuilder str = new StringBuilder();
            // 将版本号和状态拼接起来
            str.append(versionString).append("|").append(status);

            ParamValue paramValue = new ParamValue();
            // 设置设备参数值
            paramValue.setParamValue(str.toString());
            paramValue.setDevName(pv.getDevName());
            paramValue.setDevModel(pv.getDevModel());
            paramValue.setIp(pv.getIp());
            paramValue.setPort(pv.getPort());
            String nameKeyPart = "mkStatus" + param + i / 12;
            // 设置设备参数名(参数键)
            paramValue.setParamName(nameKeyPart);
            paramValue.setParamKey(nameKeyPart);
            paramValue.setParamType(1);
            paramValue.setParamSubType(12);
            paramValue.setRegAddr(968 + Integer.parseInt(param) * 50 + i / 12);
            paramValue.setDtUpdate(new Date());

            try {
                // 尝试插入设备参数到数据库
                paramValueMapper.insertParamValue(paramValue);
            } catch (Exception e) {
                // 插入设备参数出现异常时，打印异常
                e.printStackTrace();
            }
        }
    }

    @Override
    public AjaxResult getDouParam(String devName, Integer param) {
        JSONObject jo = new JSONObject();
        // 基于指定的regAddr累加param作为regAddr
        int[] addrs = { 349, 359, 369, 379, 380 };
        jo.put("setDou", paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addrs[0] + param + ""));
        jo.put("weightUp", paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addrs[1] + param + ""));
        jo.put("weightDown", paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addrs[2] + param + ""));
        jo.put("isMultiDou", paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addrs[3] + ""));
        jo.put("isSuperLight", paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addrs[4] + ""));

        return AjaxResult.success(jo);
    }

    @Override
    public AjaxResult getMkParam(String devName, Integer param) {
        // 根据参数值指定regAddr范围
        // 从MK_NUM_REG_START开始，每个模块占用50个寄存器
        int start = MK_NUM_REG_START + param * 50;
        int end = start + 50;
        return AjaxResult.success(paramValueMapper.getParamValueByRegAddrRange(devName, start + "", end + ""));
    }

    /**
     * 获取马达模式参数
     *
     * @param devName   the name of the device
     * @param mainModel the main model value
     * @param subModel  the sub model value
     * @return the AJAX result containing the motor parameter
     * @throws Exception if an error occurs during the retrieval process
     */
    @Override
    public AjaxResult getMotorParam(String devName, Integer mainModel, Integer subModel) {
        try {

            JSONArray paramValues = new JSONArray();
            int[] mainArr = { 0x00, 0x10, 0x30, 0x40 };
            int[] subArr = { 0x00, 0x01, 0x02, 0x03, 0x04 };
            int cmdParam = mainArr[mainModel] + subArr[subModel];
            System.out.println("mainModel" + mainModel);
            System.out.println("subModel" + subModel);
            System.out.println("命令参数" + cmdParam);
            System.out.println("下发命令" + devName);
            Socket socket = ModbusSocketManage.getServerConClientThread(devName).getSocket();
            ModbusUtils.writeCmdParam(socket, CtrlNo.CTL_MOTOR_SELECT, String.valueOf(cmdParam));
            // ModbusUtils.writeCmdParam(socket, String.valueOf(cmdParam));
            String[] pnameArr = { "角度", "最大速度", "起始速度", "结束速度", "加速度", "减速度" };
            if (waitCommandResult(socket, 3000)) {
                byte[] bytes = ModbusUtils.ReadHoldingRegister(socket, MM_REG_START, MM_TOTAL_NUM);
                System.out.println("长度" + bytes.length);
                // 将字节数组转为数字字符串打印
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    str.append(bytes[i]).append(",");
                }
                System.out.println("数字串：" + str.toString());
                int paramIdx = 0;
                JSONArray paramGroup = new JSONArray();
                for (int i = 0; i < 120; i += 2) {
                    short s = (short) ((bytes[i] << 8) | bytes[i + 1] & 0xff);
                    ParamValue paramValue = new ParamValue();
                    paramValue.setDevModel("CBM5");
                    paramValue.setDevName(devName);
                    paramValue.setParamValue(String.valueOf(s));
                    String paramName = pnameArr[paramIdx] + i / 12;
                    paramValue.setParamName(paramName);
                    paramValue.setParamKey(paramName);
                    paramValue.setParamType(1);
                    paramValue.setParamSubType(9);
                    paramValue.setRegAddr(MM_REG_START + i / 2);
                    paramValue.setRegNum(1);
                    paramValue.setDataType("U16");
                    paramValue.setDtUpdate(new Date());
                    setParamValueInfo(paramValue, paramIdx / 2);
                    paramGroup.add(paramValue);
                    paramIdx++;
                    if (paramIdx % 6 == 0) {
                        paramValues.add(paramGroup);
                        paramGroup = new JSONArray();
                        paramIdx = 0;
                    }
                }
                delMotorExtraParam(bytes, devName, paramValues);
            }
            return AjaxResult.success(paramValues);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 处理马达模式的额外参数
     */
    private void delMotorExtraParam(byte[] bytes, String devName, JSONArray j1) {
        // 处理后面的两个寄存器，两个寄存器共4个字节，每两个字节作为一个short类型的值并存入数据库
        short s1 = (short) ((bytes[120] << 8) | bytes[121] & 0xff);
        short s2 = (short) ((bytes[122] << 8) | bytes[123] & 0xff);
        // 将读取到的值存储到数据库中
        ParamValue pv = new ParamValue();
        pv.setDevModel("CBM5");
        pv.setDevName(devName);
        pv.setParamValue(s1 + "");
        pv.setParamName("光电停止角度");
        pv.setParamKey("motor_gdtzjd");
        pv.setParamType(1);
        pv.setParamSubType(9);
        pv.setRegAddr(MM_GD_ADDR);
        pv.setDtUpdate(new Date());
        pv.setMinV(-3600);
        pv.setMaxV(3600);
        pv.setDecimalNum(1);
        pv.setUnit("°");
        paramValueMapper.insertParamValue(pv);
        j1.add(pv);
        ParamValue pv2 = new ParamValue();
        pv2.setDevModel("CBM5");
        pv2.setDevName(devName);
        pv2.setParamValue(s2 + "");
        pv2.setParamName("无光电停止角度");
        pv2.setParamKey("motor_wgdtzjd");
        pv2.setRegAddr(MM_WGD_ADDR);
        pv2.setParamType(1);
        pv2.setParamSubType(9);
        pv2.setDtUpdate(new Date());
        pv2.setMinV(-3600);
        pv2.setMaxV(3600);
        pv2.setDecimalNum(1);
        pv2.setUnit("°");
        paramValueMapper.insertParamValue(pv2);
        j1.add(pv2);
    }

    private boolean waitCommandResult(Socket socket, long millis) throws Exception {
        int cycleMaxTime = 60;
        while (cycleMaxTime > 0) {
            String value = ModbusUtils.readCmdResult(socket);
            System.out.println("读取命令执行结果" + value);
            // if (value.equals("0")) {
            // if (!value.equals("0")) {
            // return true;
            // }
            // 只要结果不为空就返回
            if (!value.equals("-1")) {
                return true;
            }
            cycleMaxTime--;
            Thread.sleep(millis);
        }
        return false;
    }

    private void setParamValueInfo(ParamValue paramValue, int pidx) {
        switch (pidx) {
            case 0:
                paramValue.setMinV(-3600);
                paramValue.setMaxV(3600);
                paramValue.setDecimalNum(1);
                paramValue.setUnit("度");
                break;
            case 1:
            case 2:
            case 3:
                paramValue.setMinV(0);
                paramValue.setMaxV(600);
                paramValue.setDecimalNum(0);
                paramValue.setUnit("rpm");
                break;
            case 4:
            case 5:
                paramValue.setMinV(0);
                paramValue.setMaxV(9999);
                paramValue.setDecimalNum(0);
                paramValue.setUnit("rpm/s");
                break;
            default:
                break;
        }
    }

    @Override
    public AjaxResult getInOutParam(String devName, Integer param, Integer inOut) {
        // 打印从modbus采集的二进制数据
        try {
            byte[] bytes = ModbusUtils
                    .ReadHoldingRegister(ModbusSocketManage.getServerConClientThread(devName).getSocket(), 106, 8);
            System.out.println("长度" + bytes.length);
            System.out.println(Arrays.toString(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 采集到的值是作为整数存入数据库的，每个整数都可以被拆分为两个整数，对应两个端口的功能码
        JSONObject jo = new JSONObject();
        int[] addrs = { 106, 114, 118, 122 };
        int[] addrs2 = { 162, 163, 164 };
        // 根据inOut的值，选择一个起始addr
        int addr = addrs[inOut];
        // 根据param/2，确定位移量
        int offset = param / 2;
        addr = addr + offset;
        // 根据这个addr，查询paramvalue
        ParamValue pv = paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addr + "");
        // 在java中，int占4个字节，但是我们这里只需要2个字节
        // 所以，我们可以将int转换为short类型，然后再进行位运算
        // 然后根据param%2==1，确定是高8位还是低8位
        if (pv != null) {
            short s = Short.parseShort(pv.getParamValue());
            int i = 0;
            if (param % 2 == 0) {
                // 低8位
                i = s & 0xff;
            } else {
                // 高8位
                i = s >> 8;
            }
            pv.setParamValue(i + "");
        } else {
            pv = new ParamValue();
            pv.setDevName(devName);
            pv.setParamValue("0");
        }
        jo.put("func", pv);
        // 确定状态寄存器的地址-如果inOut为0，那么就是162，如果inOut>2，那么就是164,否则就是163
        if (inOut == 0) {
            inOut = 0;
        } else if (inOut > 2) {
            inOut = 2;
        } else {
            inOut = 1;
        }
        int addr2 = addrs2[inOut];
        ParamValue pv2 = paramValueMapper.getParamValueByDevNameAndRegAddr(devName, addr2 + "");
        if (pv2 == null) {
            pv2 = new ParamValue();
            pv2.setDevName(devName);
            pv2.setParamValue("1");
        }
        jo.put("status", pv2);
        return AjaxResult.success(jo);
    }

    @Override
    public int setInOutParam(ParamValue paramValue) {
        return setInOutFunc(paramValue);
    }

    /**
     * 设置输入输出的func
     */
    private int setInOutFunc(ParamValue paramValue) {
        // 设置输入输出参数的时候，需要先查询到这个参数的值，然后根据这个值来设置
        // 1.查询到这个参数的值
        ParamValue pv = paramValueMapper.getParamValueByDevNameAndRegAddr(paramValue.getDevName(),
                paramValue.getRegAddr() + "");
        // 如果pv为空，则create一个
        if (pv == null) {
            pv = new ParamValue();
            pv.setDevName(paramValue.getDevName());
            pv.setParamName(paramValue.getParamName());
            pv.setParamType(paramValue.getParamType());
            pv.setParamSubType(paramValue.getParamSubType());
            pv.setRegAddr(paramValue.getRegAddr());
            pv.setParamValue("0");
            pv.setDtUpdate(new Date());
        }
        // 2.根据参数值来设置
        // 2.1 将参数值转换为short类型
        short s = Short.parseShort(pv.getParamValue());
        // 2.2 根据paramValue的params中的param%2==0，确定是高8位还是低8位，param是指端口号，总共有8个端口0-7
        short i = Short.parseShort(paramValue.getParamValue());
        int param = Integer.parseInt(paramValue.getParams().get("param").toString());
        if (param % 2 == 0) {
            // 低8位
            s = (short) ((s & 0xff00) | i);
        } else {
            // 高8位
            s = (short) ((s & 0xff) | (i << 8));
        }
        // 2.3 将这个值写入到数据库中
        paramValue.setParamValue(s + "");
        return this.updateParamValue(paramValue);
    }

    /*
     * 查询设备参数--适用于设备参数管理
     */
    @Override
    public List<ParamValue> listForParam(ParamValue paramValue) {
        // 首先查询设备参数
        List<ParamValue> list = paramValueMapper.selectParamValueList(paramValue);
        // 查询设备中的sys_dot_num
        ParamValue pv2 = paramValueMapper.getDotNumParamValueByDevName(paramValue.getDevName());
        Integer dotNum = 0;
        if (null != pv2) {
            dotNum = pv2.getParamValue() != null ? Integer.parseInt(pv2.getParamValue()) : 0;
        }
        // 设置小数点位数
        for (ParamValue pv : list) {
            pv.setDecimalNum(getDecimalNum(pv.getDecimalType(), pv.getDecimalNum(), dotNum));
        }
        // 然后查询reg_lib
        RegLib regLib = new RegLib();
        regLib.setParamType(paramValue.getParamType());
        regLib.setParamSubType(paramValue.getParamSubType());
        regLib.setDevModel(paramValue.getDevModel());
        List<RegLib> regLibs = regLibMapper.selectRegLibList(regLib);
        // 然后将reg_lib中的参数名和设备参数中的参数名进行比较
        // 如果设备参数中没有这个参数名，那么就添加到设备参数中
        for (RegLib rl : regLibs) {
            boolean flag = true;
            for (ParamValue pv : list) {
                if (rl.getParamName().equals(pv.getParamName())) {
                    flag = false;
                    pv.setRegNum(rl.getRegNum());
                    pv.setDataType(rl.getDataType());
                    break;
                }
            }
            if (flag) {
                ParamValue pv = new ParamValue();
                pv.setDevName(paramValue.getDevName());
                pv.setParamKey(rl.getParamKey());
                pv.setParamName(rl.getParamName());
                pv.setParamType(rl.getParamType());
                pv.setParamSubType(rl.getParamSubType());
                pv.setRegAddr(rl.getRegAddr());
                pv.setRegNum(rl.getRegNum());
                pv.setDataType(rl.getDataType());
                pv.setMinV(rl.getMinV());
                pv.setMaxV(rl.getMaxV());
                pv.setDecimalNum(getDecimalNum(rl.getDecimalType(), rl.getDecimalNum(), dotNum));
                pv.setUnit(rl.getUnit());
                // 根据dataType，设置paramValue的值，如果是U8A，设置""，如果是其他，设置0
                if (rl.getDataType().equals("U8A")) {
                    pv.setParamValue("");
                } else {
                    pv.setParamValue("0");
                }
                pv.setDtUpdate(new Date());
                list.add(pv);
            }

        }
        return list;
    }

    /**
     * 根据decimalType计算decimalNum
     */
    private Integer getDecimalNum(Integer decimalType, Integer decimalNum, Integer dotNum) {
        // 根据decimalType来设置decimalNum
        // decimalType有4种情况，0，1，2，3
        // 如果是0，那么decimalNum就是decimalNum
        // 如果是1，那么decimalNum就是跟随reg_lib中的sys_dot_num
        // 如果是2，那么decimalNum就是跟随reg_lib中的sys_dot_num作计算1+sys_dot_num%3
        decimalType = decimalType != null ? decimalType : 0;
        decimalNum = decimalNum != null ? decimalNum : 0;
        if (decimalType == 1) {
            decimalNum = dotNum;
        } else if (decimalType == 2) {
            decimalNum = dotNum % 4;
        }
        return decimalNum;
    }

    @Override
    public AjaxResult setPlzyParam(ParamValue pv) {
        System.out.println(pv.toString());
        // 首先根据regAddr查询相邻的寄存器的值，如果regAddr%2==0，那么就是regAddr+1，否则就是regAddr-1
        int regAddr = pv.getRegAddr();
        int regAddr2 = 0;
        if (regAddr % 2 == 0) {
            regAddr2 = regAddr + 1;
        } else {
            regAddr2 = regAddr - 1;
        }
        // 根据regAddr2查询到这个寄存器的值
        ParamValue pv2 = paramValueMapper.getParamValueByDevNameAndRegAddr(pv.getDevName(), regAddr2 + "");
        // 将两个寄存器的值合并成一个short类型的值
        short s = Short.parseShort(pv.getParamValue());
        short s2 = Short.parseShort(pv2.getParamValue());
        // 如果regAddr%2==0，那么就是s2的低8位，否则就是s2的高8位
        if (regAddr % 2 == 0) {
            s = (short) ((s2 & 0xff00) | s);
        } else {
            s = (short) ((s2 & 0xff) | (s << 8));
        }
        pv.setParamValue(s + "");
        // 如果regAddr < 146, baseAddr = 130, 否则，baseAddr = 146
        int baseAddr = regAddr < 146 ? 130 : 146;
        if (regAddr < 132) {
            regAddr = baseAddr;
        } else {
            regAddr = baseAddr + (regAddr - baseAddr) / 2;
        }
        pv.setRegAddr(regAddr);
        // 根据这个地址将这个值下发到设备中
        updateParamValue(pv);
        return AjaxResult.success();
    }
}
