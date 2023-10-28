package com.ruoyi.common.enums;

public enum ModbusCtrlNo {
    CTL_RUN, // 运行 commandH=0,停止;commandH=2,运行;commandH=4,清除报警
    CTL_MANUAL_ONCE, // 指定的一列斗运行一次 commandH斗编号
    CTL_MANUAL_CONTINUE, // 指定的一列斗连续循环运行
    CTL_MANUAL_ZZJ, // 指定主振机运行一次
    CTL_MANUAL_XZJ, // 指定的线振机运行一次
    CTL_MANUAL_HCD, // 指定的进料斗运行一次
    CTL_MANUAL_CZD, // 指定的称重斗运行一次
    CTL_MANUAL_JYD, // 指定的记忆斗运行一次
    CTL_MANUAL_JLD, // 指定集料斗运行一次
    CTL_DOU_PROHIBIT, // 禁止指定的斗
    CTL_DOU_TEST, // 称重斗进行采样测试
    CTL_DOU_CALIZERO, // 传感器置零
    CTL_DOU_CALIFULL, // 传感器满度
    CTL_SAVE_SYSTEM, // 保存系统参数
    CTL_SAVE_PROGRAM, // 保存程序参数
    CTL_MANUAL_ZERO, // 斗置零
    CTL_MANUAL_EMPTY, // 斗清空
    CTL_MANUAL_CLEAN, // 斗清洗
    CTL_MANUAL_STOP, // 停止当前动作
    CTL_PRMLIST, // 读程序列表,CmdPara起始程序号 新增加
    CTL_PRM_WRITE, // 写程序参数commandH=’A'-'D'，加载当前的程序号;commandH=0,CmdPara=1-94指定程序号 新增加
    CTL_PRM_READ, // 读程序参数commandH=’A'-'D'，加载当前的程序号;commandH=0,CmdPara=1-99指定程序号
    CTL_PRM_SELECT, // 选择程序号
    CTL_PRM_DELETE, // 删除指定的程序号
    CTL_PRM_COPY, // 复制到指定的程序号
    CTL_MOTOR_SEARCH, // 查询马达模式
    CTL_MOTOR_SELECT, // 选择马达模式 27
    CTL_MOTOR_COPY, // 复制马达模式
    CTL_RECOARD_SEARCH, // 查询记录
    CTL_RECOARD_CLEAN, // 清除记录 30
    CTL_SEND_COMBIN, // 开始发送组合时数据
    CTL_MSV, // 更新斗状态和斗重量
    CTL_SEZ, // 清零
    CTL_QLTJ, // 缺料暂停
    CTL_PRMSAVE, // 参数保存
    CTL_READ1, // 参数
    CTL_TEST_IOOUT, // 端口模拟
    CTL_TEST_DOUKAIMEN, // 斗开门测试，commandH=斗号，命令参数=角度
    CTL_TEST_ZHENDONG, // 振动测试
    CTL_READ_MKSTATUS, // 查询模块状态 40
    CTL_SEND_FILE // 2-发送DB文件、1-开始发送图像、0-结束发送图像
}
