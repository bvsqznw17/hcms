package com.ruoyi.device.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.device.domain.DevAuth;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.domain.SysSettings;
import com.ruoyi.device.mapper.DevAuthMapper;
import com.ruoyi.device.mapper.DevMsgMapper;
import com.ruoyi.device.mapper.ParamValueMapper;
import com.ruoyi.device.mapper.SysSettingsMapper;
import com.ruoyi.device.service.IDevMsgService;
import com.ruoyi.device.service.IupgradeService;
import com.ruoyi.system.mapper.SysRoleMapper;

/**
 * 设备信息Service业务层处理
 * 
 * @author cbw
 * @date 2022-12-06
 */
@Service
public class DevMsgServiceImpl implements IDevMsgService {
    @Autowired
    private DevMsgMapper devMsgMapper;

    @Autowired
    private ParamValueMapper pvMapper;

    @Autowired
    private IupgradeService upgradeService;

    @Autowired
    private DevAuthMapper devAuthMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysSettingsMapper sysSettingsMapper;

    /**
     * 查询设备信息
     * 
     * @param id 设备信息主键
     * @return 设备信息
     */
    @Override
    public DevMsg selectDevMsgById(Long id) {
        return devMsgMapper.selectDevMsgById(id);
    }

    /**
     * 查询设备信息列表
     * 
     * @param devMsg 设备信息
     * @return 设备信息
     */
    @Override
    public List<DevMsg> selectDevMsgList(DevMsg devMsg) {
        return devMsgMapper.selectDevMsgList(devMsg);
    }

    /**
     * 新增设备信息
     * 
     * @param devMsg 设备信息
     * @return 结果
     */
    @Override
    public int insertDevMsg(DevMsg devMsg) {
        return devMsgMapper.insertDevMsg(devMsg);
    }

    /**
     * 修改设备信息
     * 
     * @param devMsg 设备信息
     * @return 结果
     */
    @Override
    public int updateDevMsg(DevMsg devMsg) {
        return devMsgMapper.updateDevMsg(devMsg);
    }

    /**
     * 批量删除设备信息
     * 
     * @param ids 需要删除的设备信息主键
     * @return 结果
     */
    @Override
    public int deleteDevMsgByIds(Long[] ids) {
        // 删除设备的同时，删除设备升级记录
        for (Long id : ids) {
            // 根据devName删除设备升级记录
            upgradeService.deleteupgradeByDevName(devMsgMapper.selectDevMsgById(id).getDevName());
        }
        return devMsgMapper.deleteDevMsgByIds(ids);
    }

    /**
     * 删除设备信息信息
     * 
     * @param id 设备信息主键
     * @return 结果
     */
    @Override
    public int deleteDevMsgById(Long id) {
        return devMsgMapper.deleteDevMsgById(id);
    }

    /**
     * 查询设备信息列表-拓展
     */
    @Override
    public List<DevMsg> selectDevMsgListEx(DevMsg devMsg, Long userId) {
        // 查询设备信息列表
        List<DevMsg> devMsgs = devMsgMapper.selectDevMsgList(devMsg);

        // 获取系统参数小数位
        int sys_dot_num = getSysParam(devMsg.getDevName()).get("sys_dot_num") == null ? 0
                : Integer.parseInt(
                        getSysParam(devMsg.getDevName()).get("sys_dot_num").toString());

        // 额外参数Array
        // List<String> sa = Arrays.asList("目标重量","设定速度","平均速度","健康状态","料位状态");
        Map<String, String> p_map = new HashMap<>();
        p_map.put("prm_SetWeight", "目标重量");
        p_map.put("prm_speed", "设定速度");
        p_map.put("cmb_speed", "平均速度");
        // 遍历devMsgs
        for (DevMsg dm : devMsgs) {
            // 查询设备参数
            ParamValue paramValue = new ParamValue();
            paramValue.setDevName(dm.getDevName());
            List<ParamValue> pvs = pvMapper.selectParamValueList(paramValue);
            // 遍历pvs，选取几个关键参数加入到devMsg
            Map<String, Object> map = new HashMap<>();
            for (ParamValue pv : pvs) {
                if (p_map.keySet().contains(pv.getParamKey())) {
                    map.put(p_map.get(pv.getParamKey()), formatParamValue(pv.getParamValue(), sys_dot_num));
                }
                // 找到机器类型，把机器类型作为devModel的后缀
                if (null != pv.getParamKey() && pv.getParamKey().equals("sys_MacType")) {
                    // 机器类型的值作为一个16位的short类型，然后分成高低8位，高8位为乘数，低8位为被乘数
                    int macType = pv.getParamValue() == null ? 0 : Integer.parseInt(pv.getParamValue());
                    int high = (macType & 0xff00) >> 8;
                    int low = macType & 0xff;
                    // 把机器类型作为devModel的后缀
                    dm.setDevModel(dm.getDevModel() + "-" + high + "*" + low);
                }
            }
            dm.setParams(map);
        }
        return filterDevMsg(devMsgs, userId);
    }

    // 格式化字符数字并返回数字字符串
    private String formatParamValue(String paramValue, int sysDotNum) {
        if (paramValue != null) {
            double value = Double.parseDouble(paramValue);
            String format = "%." + sysDotNum + "f";
            return String.format(format, value / Math.pow(10, sysDotNum));
        }
        return paramValue;
    }

    /**
     * 查询用户拥有的设备权限，根据devId过滤devMsgs
     */
    private List<DevMsg> filterDevMsg(List<DevMsg> devMsgs, Long userId) {
        // 过滤的逻辑是这样的，查询userId对应的角色，然后根据角色去调用不同表查询devId，最后使用devId过滤机器
        // 1.查询用户拥有的角色
        List<SysRole> roles = sysRoleMapper.selectRolePermissionByUserId(userId);
        // 2.根据角色去调用不同表查询devId
        List<String> devIdList = new ArrayList<>();
        if (containsRoleId(roles, 100L) && roles.size() == 1) {
            // 2.1 如果当前用户拥有代理商角色（roleId为100），查询代理商的设备
            List<DevAuth> devAuths = devAuthMapper.listAgentDevAuth(userId + "");
            devIdList.addAll(devAuths.stream().map(DevAuth::getDevId).collect(Collectors.toList()));
        } else if (containsRoleId(roles, 101L) && roles.size() == 1) {
            // 2.2 如果当前用户拥有终端用户角色（roleId为101），查询终端用户的设备
            List<DevAuth> devAuths = devAuthMapper.listUserDevAuth(userId + "");
            devIdList.addAll(devAuths.stream().map(DevAuth::getDevId).collect(Collectors.toList()));
        } else {
            // 其他情况
            return devMsgs;
        }
        // 3.使用devId过滤
        return devMsgs.stream().filter(dm -> devIdList.contains(dm.getId() + "")).collect(Collectors.toList());
    }

    /**
     * 判断角色列表中的角色是否包含某个角色ID
     */
    private boolean containsRoleId(List<SysRole> roles, Long roleId) {
        for (SysRole role : roles) {
            if (role.getRoleId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定设备的系统参数
     */
    private Map<String, Object> getSysParam(String devName) {
        SysSettings setting = new SysSettings();
        setting.setDevName(devName);
        List<SysSettings> sysSettings = sysSettingsMapper.selectSysSettingsList(setting);
        // 遍历sysSettings并提取各参数的paramKey作为map的key，提取paramValue作为map的value
        Map<String, Object> map = new HashMap<>();
        for (SysSettings s : sysSettings) {
            map.put(s.getParamKey(), s.getParamValue());
        }
        return map;
    }

    @Override
    public void resetDevStatus() {
        devMsgMapper.resetDevStatus();
    }
}
