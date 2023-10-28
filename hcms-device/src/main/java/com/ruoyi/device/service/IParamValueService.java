package com.ruoyi.device.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.device.domain.ParamValue;

/**
 * 设备参数Service接口
 * 
 * @author cbw
 * @date 2022-10-14
 */
public interface IParamValueService {
    /**
     * 查询设备参数
     * 
     * @param id 设备参数主键
     * @return 设备参数
     */
    public ParamValue selectParamValueById(Long id);

    /**
     * 查询设备参数列表
     * 
     * @param paramValue 设备参数
     * @return 设备参数集合
     */
    public List<ParamValue> selectParamValueList(ParamValue paramValue);

    /**
     * 新增设备参数
     * 
     * @param paramValue 设备参数
     * @return 结果
     */
    public int insertParamValue(ParamValue paramValue);

    /**
     * 修改设备参数
     * 
     * @param paramValue 设备参数
     * @return 结果
     */
    public int updateParamValue(ParamValue paramValue);

    /**
     * 批量删除设备参数
     * 
     * @param ids 需要删除的设备参数主键集合
     * @return 结果
     */
    public int deleteParamValueByIds(Long[] ids);

    /**
     * 删除设备参数信息
     * 
     * @param id 设备参数主键
     * @return 结果
     */
    public int deleteParamValueById(Long id);

    /**
     * 删除设备参数信息
     * 
     * @return 结果
     */
    public int deleteParamValueByDevName(String devName);

    /**
     * 删除设备参数信息
     * 
     * @return 结果
     */
    public int deleteParamValueByIp(String ip);

    /**
     * 根据ip采集数据
     */
    public int collData(ParamValue paramValue);

    /**
     * 采集模块状态数据
     */
    public AjaxResult collModuleStatus(ParamValue paramValue);

    // 查询指定斗参数
    public AjaxResult getDouParam(String devName, Integer param);

    // 修改指定斗参数
    // public AjaxResult setDouParam(HashMap<String, String> obj);

    // 查询模块状态参数
    public AjaxResult getMkParam(String devName, Integer param);

    // 查询马达模式参数
    public AjaxResult getMotorParam(String devName, Integer mainModel, Integer subModel);

    // 查询输入输出参数
    public AjaxResult getInOutParam(String devName, Integer param, Integer inOut);

    // 修改输入输出参数
    public int setInOutParam(ParamValue paramValue);

    // 查询设备参数--适用于设备参数管理
    public List<ParamValue> listForParam(ParamValue paramValue);

    // 查询频率增益参数
    public AjaxResult setPlzyParam(ParamValue pv);

}
