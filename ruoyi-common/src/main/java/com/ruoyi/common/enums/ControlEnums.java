package com.ruoyi.common.enums;

// ControlEnum.java
public enum ControlEnums {
    CTL_NONE(0), // CTRL_RESET
    CTL_RUN(1), // 运行 commandH=0,停止;commandH=2,运行;commandH=4,清除报警
    CTL_MANUAL_ONCE(2), // 指定的一列斗运行一次 commandH斗编号
    CTL_MANUAL_CONTINUE(3), // 指定的一列斗连续循环运行
    CTL_MANUAL_ZZJ(4), // 指定主振机运行一次
    CTL_MANUAL_XZJ(5), // 指定的线振机运行一次
    CTL_MANUAL_HCD(6), // 指定的进料斗运行一次
    CTL_MANUAL_CZD(7), // 指定的称重斗运行一次
    CTL_MANUAL_JYD(8), // 指定的记忆斗运行一次
    CTL_MANUAL_JLD(9), // 指定集料斗运行一次
    CTL_DOU_PROHIBIT(10), // 禁止指定的斗
    CTL_DOU_TEST(11), // 称重斗进行采样测试
    CTL_DOU_CALIZERO(12), // 传感器置零
    CTL_DOU_CALIFULL(13), // 传感器满度
    CTL_SAVE_SYSTEM(14), // 保存系统参数
    CTL_SAVE_PROGRAM(15), // 保存程序参数
    CTL_MANUAL_ZERO(16), // 斗置零
    CTL_MANUAL_EMPTY(17), // 斗清空
    CTL_MANUAL_CLEAN(18), // 斗清洗
    CTL_MANUAL_STOP(19), // 停止当前动作
    CTL_PRMLIST(20), // 读程序列表,CmdPara起始程序号 新增加
    CTL_PRM_WRITE(21), // 写程序参数commandH=’A'-'D'，加载当前的程序号;commandH=0,CmdPara=1-94指定程序号 新增加
    CTL_PRM_READ(22), // 读程序参数commandH=’A'-'D'，加载当前的程序号;commandH=0,CmdPara=1-99指定程序号
    CTL_PRM_SELECT(23), // 选择程序号
    CTL_PRM_DELETE(24), // 删除指定的程序号
    CTL_PRM_COPY(25), // 复制到指定的程序号
    CTL_MOTOR_SEARCH(26), // 查询马达模式
    CTL_MOTOR_SELECT(27), // 选择马达模式
    CTL_MOTOR_COPY(28), // 复制马达模式
    CTL_RECOARD_SEARCH(29), // 查询记录
    CTL_RECOARD_CLEAN(30), // 清除记录
    CTL_MOTOR_TEST(31), // 马达模式测试
    CTL_MSV(32), // 更新斗状态和斗重量
    CTL_SEZ(33), // 清零
    CTL_QLTJ(34), // 缺料暂停
    CTL_PRMSAVE(35), // 参数保存
    CTL_PRM_READ1(36), // 参数
    CTL_TEST_IOOUT(37), // 端口模拟
    CTL_TEST_DOUKAIMEN(38), // 斗开门测试,commandH=斗号,命令参数=角度
    CTL_TEST_ZHENDONG(39), // 振动测试
    CTL_READ_MKSTATUS(40), // 查询模块状态
    CTL_INTO_PAGE(41), // 进入页面
    CTL_IN_TWEIGHT(42); // 外部输入目标重量

    private int value;

    ControlEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
