package com.ruoyi.common.constant;

public class CtrlNo {

    public static final String CTL_NONE = "0"; // public static final String CTL_RESET = "1";
    public static final String CTL_RUN = "1"; // 运行 commandH=0,停止;commandH=2,运行;commandH=4,清除报警
    public static final String CTL_MANUAL_ONCE = "2"; // 指定的一列斗运行一次 commandH斗编号
    public static final String CTL_MANUAL_CONTINUE = "3"; // 指定的一列斗连续循环运行
    public static final String CTL_MANUAL_ZZJ = "4"; // 指定主振机运行一次
    public static final String CTL_MANUAL_XZJ = "5"; // 指定的线振机运行一次
    public static final String CTL_MANUAL_HCD = "6"; // 指定的进料斗运行一次
    public static final String CTL_MANUAL_CZD = "7"; // 指定的称重斗运行一次
    public static final String CTL_MANUAL_JYD = "8"; // 指定的记忆斗运行一次
    public static final String CTL_MANUAL_JLD = "9"; // 指定集料斗运行一次
    public static final String CTL_DOU_PROHIBIT = "10"; // 禁止指定的斗
    public static final String CTL_DOU_TEST = "11"; // 称重斗进行采样测试
    public static final String CTL_DOU_CALIZERO = "12"; // 传感器置零
    public static final String CTL_DOU_CALIFULL = "13"; // 传感器满度
    public static final String CTL_SAVE_SYSTEM = "14"; // 保存系统参数
    public static final String CTL_SAVE_PROGRAM = "15"; // 保存程序参数
    public static final String CTL_MANUAL_ZERO = "16"; // 斗置零
    public static final String CTL_MANUAL_EMPTY = "17"; // 斗清空
    public static final String CTL_MANUAL_CLEAN = "18"; // 斗清洗
    public static final String CTL_MANUAL_STOP = "19"; // 停止当前动作
    public static final String CTL_PRMLIST = "20"; // 读程序列表,CmdPara起始程序号 新增加
    public static final String CTL_PRM_WRITE = "21"; // 写程序参数commandH='A'-'D'，加载当前的程序号;commandH=0,CmdPara=1-94指定程序号
    public static final String CTL_PRM_READ = "22"; // 读程序参数commandH='A'-'D'，加载当前的程序号;commandH=0,CmdPara=1-99指定程序号
    public static final String CTL_PRM_SELECT = "23"; // 选择程序号
    public static final String CTL_PRM_DELETE = "24"; // 删除指定的程序号
    public static final String CTL_PRM_COPY = "25"; // 复制到指定的程序号
    public static final String CTL_MOTOR_SEARCH = "26"; // 查询马达模式
    public static final String CTL_MOTOR_SELECT = "27"; // 选择马达模式
    public static final String CTL_MOTOR_COPY = "28"; // 复制马达模式
    public static final String CTL_RECOARD_SEARCH = "29"; // 查询记录
    public static final String CTL_RECOARD_CLEAN = "30"; // 清除记录
    public static final String CTL_MOTOR_TEST = "31"; // 马达模式测试
    public static final String CTL_MSV = "32"; // 更新斗状态和斗重量
    public static final String CTL_SEZ = "33"; // 清零
    public static final String CTL_QLTJ = "34"; // 缺料暂停
    public static final String CTL_PRMSAVE = "35"; // 参数保存
    public static final String CTL_PRM_READ1 = "36"; // 参数
    public static final String CTL_TEST_IOOUT = "37"; // 端口模拟
    public static final String CTL_TEST_DOUKAIMEN = "38"; // 斗开门测试,commandH=斗号,命令参数=角度
    public static final String CTL_TEST_ZHENDONG = "39"; // 振动测试
    public static final String CTL_READ_MKSTATUS = "40"; // 查询模块状态
    public static final String CTL_INTO_PAGE = "41"; // 进入页面
    public static final String CTL_IN_TWEIGHT = "42"; // 外部输入目标重量
}
