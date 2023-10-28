// package com.ruoyi.quartz.task;

// import java.util.Date;
// import java.util.List;
// import java.util.Random;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.ruoyi.base.domain.RegLib;
// import com.ruoyi.common.utils.ip.IpUtils;
// import com.ruoyi.common.utils.modbus.ModbusUtilsBak;
// import com.ruoyi.device.domain.DevMsg;
// import com.ruoyi.device.domain.ParamValue;
// import com.ruoyi.device.mapper.DevMsgMapper;
// import com.ruoyi.device.mapper.ParamValueMapper;

// import cn.hutool.core.thread.ThreadUtil;

// /**
//  * modbus心跳定期检测
//  * 
//  * @author cbw
//  */
// @Component("mbaTask")
// public class MbAliveTask {

//     @Autowired
//     private DevMsgMapper devMsgMapper;

//     @Autowired
//     private ParamValueMapper paramValueMapper;

//     @Autowired
//     private com.ruoyi.base.mapper.RegLibMapper RegLibMapper;

//     public void makePvs()
//     {
//         System.out.println("执行无参方法");
//         // 查询设备名称列表
//         List<DevMsg> devMsgs = devMsgMapper.selectDevMsgList(null);
//         // 根据设备名称列表查询
        
//         // 获取基础参数列表
//         List<RegLib> reglibs = RegLibMapper.selectRegLibList(null);
//         Random random = new Random(1000L);
//         for (DevMsg dm : devMsgs){
//             for (RegLib regLib : reglibs) {
//                 // 跳过保留字段
//                 if (regLib.getParamName().equals("保留"))continue;
//                 // 跳过系统参数为0的字段
//                 if (regLib.getParamSubType() == 0)continue;
//                 // 执行插入
//                 ParamValue pv = new ParamValue();
//                 pv.setDevName(dm.getDevName());
//                 pv.setDevModel(dm.getDevModel());
//                 pv.setParamKey(regLib.getParamKey());
//                 pv.setIp(dm.getIp());
//                 pv.setPort(dm.getPort());
//                 pv.setRegAddr(regLib.getRegAddr());
//                 pv.setParamType(regLib.getParamType());
//                 pv.setParamSubType(regLib.getParamSubType());
//                 pv.setDtUpdate(new Date());
//                 pv.setParamName(regLib.getParamName());
//                 pv.setParamValue(random.nextInt(1000) + "");
//                 paramValueMapper.insertParamValue(pv);
//             }
//         }
//     }

//     /**
//      * 多线程扫描目标主机指定Set端口集合的开放情况
//      * 
//      * @param ip
//      *                     待扫描IP或域名,eg:180.97.161.184 www.zifangsky.cn
//      * @param portSet
//      *                     待扫描的端口的Set集合
//      * @param threadNumber
//      *                     线程数
//      * @param timeout
//      *                     连接超时时间
//      */
//     public void scanLargePorts() {
//         // 获取待扫描端口列表
//         DevMsg devMsg = new DevMsg();
//         devMsg.setStatus(1);
//         List<DevMsg> devMsgs = devMsgMapper.selectDevMsgList(devMsg);
//         ExecutorService threadPool = Executors.newCachedThreadPool();
//         for (DevMsg dl : devMsgs) {
//             ScanMethod2 scanMethod2 = new ScanMethod2(dl.getIp(), dl.getPort(), dl);
//             threadPool.execute(scanMethod2);
//         }
//         threadPool.shutdown();
//         while (true) {
//             if (threadPool.isTerminated()) {
//                 System.out.println("扫描结束");
//                 break;
//             }
//             try {
//                 Thread.sleep(1000);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//     }

//     /**
//      * 扫描方式二：针对一个待扫描的端口的Set集合进行扫描
//      * 
//      */
//     private class ScanMethod2 implements Runnable {
//         private String ip; // 目标IP
//         private Integer port; // 目标端口
//         private DevMsg dl; // 修改对象
//         private int timeout; // 线程数，这是第几个线程，超时时间

//         public ScanMethod2(String ip,int port, DevMsg dl) {
//             this.ip = ip;
//             this.port = port;
//             this.dl = dl;
//         }

//         public void run() {
//             // Socket socket;
//             // SocketAddress socketAddress;
//             // socket.connect(socketAddress, timeout);
//             // socket.close();
//             // dl.setStatus(1);
//             ParamValue pv = new ParamValue();
//             pv.setIp(ip);
//             pv.setPort(port);
//             collData(pv);
//         }

//     }

//     public int collData(ParamValue paramValue) {
//         System.out.println("采集数据任务");
//         // 获取ip和端口，如果没有指定端口，默认502
//         String ip = paramValue.getIp();
//         Integer port = paramValue.getPort() != null ? paramValue.getPort(): 502;
//         // port = port != null ? port : 502;
//         // 测试连接诶是否可用
//         if (!IpUtils.telnet(ip, port, 3000)) {
//             addOrUpdDevList(ip, port, false);
//             return 0;
//         }
//         addOrUpdDevList(ip, port, true);
//         // 创建连接
//         ModbusUtilsBak.initModbusTcpMaster(ip, port);
//         // 获取基础参数列表
//         List<RegLib> reglibs = RegLibMapper.selectRegLibList(null);
//         for (RegLib regLib : reglibs) {
//             // 跳过保留字段
//             if (regLib.getParamName().equals("保留"))continue;
//             // 执行异步任务
//             ThreadUtil.execAsync(() -> {
//                 try {
//                     collDataSingleTask(regLib, ip, port, 4);
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             });
//         }
//         return 1;
//     }

//     private void addOrUpdDevList(String ip, Integer port, boolean isConnected){
//         System.out.println("更新任务");
//         int status = isConnected ? 1 : 0;
//         // 检查数据库中是否存在该记录
//         // 如果不存在就插入，否则就修改
//         DevMsg dl = new DevMsg();
//         dl.setIp(ip);
//         List<DevMsg> dls = devMsgMapper.selectDevMsgList(dl);
//         if (dls.size() > 0){
//             // update
//             dl = dls.get(0);
//             dl.setStatus(status);
//             devMsgMapper.updateDevMsg(dl);
//         }else {
//             // insert
//             dl.setDtCreate(new Date());
//             dl.setDtUpdate(new Date());
//             dl.setDevName("未命名");
//             dl.setIp(ip);
//             dl.setPort(port);
//             dl.setStatus(status);
//             devMsgMapper.insertDevMsg(dl);
//         }
//     }

//         /**
//      * 抽离出来做异步任务的逻辑
//      * @param dataType
//      * @return
//      */
//     private void collDataSingleTask(RegLib regLib, String ip, Integer port, int tryTimes) throws Exception{
//         System.out.println("单次采集任务");
//         if (tryTimes < 0){
//             return;
//         }
//         // 数据类型转换
//         String dataType = transDataType(regLib.getDataType());
//         String value = null;
//         try {
//             value = ModbusUtilsBak.readHoldingRegisters(regLib.getRegAddr(), regLib.getRegNum(), 1, dataType, 0);
//             // TODO 特殊值的处理
//             // 将采集的值存储起来
//             ParamValue pv = new ParamValue();
//             pv.setIp(ip);
//             pv.setPort(port);
//             pv.setRegAddr(regLib.getRegAddr());
//             pv.setParamType(regLib.getParamType());
//             pv.setParamSubType(regLib.getParamSubType());
//             pv.setDtUpdate(new Date());
//             pv.setParamName(regLib.getParamName());
//             pv.setParamValue(value);
//             paramValueMapper.insertParamValue(pv);
//             System.out.println("insert a data for pv");
//         } catch (Exception e) {
//             collDataSingleTask(regLib, ip, port, tryTimes - 1);
//             if (tryTimes == 1) {
//                 throw e;
//             }
//         }
//     }


//     /**
//      * 对数据类型进行转换
//      */
//     private String transDataType(String dataType){
//         // 如果字符串中包含'[',将后面一部分都替换为A
//         if (dataType.contains("[")) {
//             dataType.substring(0, dataType.lastIndexOf("["));
//             dataType += "A";
//         }
//         return dataType;
//     }
// }
