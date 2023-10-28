package com.ruoyi.device.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.device.domain.ParamValue;

/**
 * 设备参数Mapper接口
 * 
 * @author cbw
 * @date 2022-10-14
 */
public interface ParamValueMapper {
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
         * 删除设备参数
         * 
         * @param id 设备参数主键
         * @return 结果
         */
        public int deleteParamValueById(Long id);

        /**
         * 删除设备参数
         * 
         * @param devName 设备名称
         * @return 结果
         */
        public int deleteParamValueByDevName(String devName);

        /**
         * 删除设备参数
         * 
         * @param Ip ip地址
         * @return 结果
         */
        public int deleteParamValueByIp(String ip);

        /**
         * 批量删除设备参数
         * 
         * @param ids 需要删除的数据主键集合
         * @return 结果
         */
        public int deleteParamValueByIds(Long[] ids);

        /**
         * 根据regAddr和paramName获取sysRegLib
         */
        public RegLib getRegLibByRP(ParamValue paramValue);

        /**
         * 根据regAddr获取RegLib
         */
        public RegLib getRegLibByRegAddr(int regAddr);

        // // 查询指定斗参数
        // public AjaxResult getDouParam(String devName, int param);

        // // 修改指定斗参数
        // public AjaxResult setDouParam(HashMap<String, String> obj);

        // // 查询模块状态参数
        // public AjaxResult getMkParam(String devName, int param);

        // // 查询马达模式参数
        // public AjaxResult getMotorParam(String devName, int mainModel, int subModel);

        // // 查询输入输出参数
        // public AjaxResult getInOutParam(String devName, int param);

        // // 修改输入输出参数
        // public AjaxResult setInOutParam(HashMap<String, String> obj);

        // 根据设备名称和regAddr获取参数值
        public ParamValue getParamValueByDevNameAndRegAddr(@Param("devName") String devName,
                        @Param("regAddr") String regAddr);

        // 根据设备名称和paramKey获取参数值
        public ParamValue getParamValueByDevNameAndParamKey(@Param("devName") String devName,
                        @Param("paramKey") String paramKey);

        // 根据设备名称和regAddr修改参数值
        public int updateParamValueByDevNameAndRegAddr(@Param("devName") String devName,
                        @Param("regAddr") String regAddr,
                        @Param("paramValue") String paramValue);

        // 根据地址区间获取参数值
        public List<ParamValue> getParamValueByRegAddrRange(@Param("devName") String devName,
                        @Param("startAddr") String regAddrStart, @Param("endAddr") String regAddrEnd);

        // 根据设备名称获取paramKey为sys_dot_numm的参数值
        public ParamValue getDotNumParamValueByDevName(@Param("devName") String devName);

        // 删除原先采集的模块状态参数
        public int deleteMkParamValueByDevName(@Param("devName") String devName);

}
