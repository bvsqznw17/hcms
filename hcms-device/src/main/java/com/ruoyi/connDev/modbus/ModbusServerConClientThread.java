package com.ruoyi.connDev.modbus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.modbus.core.payloads.ReadHoldingRegisterPayLoad;
import com.ruoyi.common.modbus.core.requests.ModBusTcpRequest;
import com.ruoyi.common.modbus.core.requests.ModbusRequest;
import com.ruoyi.common.modbus.utils.MbMsg;
import com.ruoyi.common.utils.modbus.ModbusUtils;
import com.ruoyi.common.utils.modbus.TranStrTools;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.domain.SysSettings;
import com.ruoyi.device.mapper.ParamValueMapper;
import com.ruoyi.device.mapper.SysSettingsMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ModbusServerConClientThread extends Thread {
  private Socket socket = null;
  private String ip = null;
  private String devName = null;// 与服务端通信的设备名称
  private String uuid = null; // 与服务器通信的设备uuid
  private List<RegLib> regLibs = null;

  public ModbusServerConClientThread(Socket socket, String ip, String devName, List<RegLib> regLibs) {
    this.socket = socket;
    this.devName = devName;
    this.ip = ip;
    this.regLibs = regLibs;
  }

  public Socket getSocket() {
    return socket;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  public void run() {
    boolean loop = true;
    try {
      execRedisPartLogic(socket);
      // 获取输入和输出流
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();
      ParamValueMapper paramValueMapper = SpringUtils.getBean(ParamValueMapper.class);

      System.out.println("modbus-sc: 服务端和客户端" + devName + "保持通信");

      // 删除相同IP但不同设备名称的数据
      deleteDifferentDevNameData(paramValueMapper);

      // 接收客户端发来的消息
      while (loop) {
        readAndStoreData(in, out, paramValueMapper);
        // 采集频率增益参数
        collFreqGainData(socket);
        loop = false;
      }
    } catch (Exception e) {
      loop = false;
      e.printStackTrace();
      try {
        System.out.println("modbus-sc: socket是否已关闭：" + socket.isClosed());
        if (!socket.isClosed()) {
          socket.close();
        }
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  // 并行组件
  private void execRedisPartLogic(Socket socket) {

    try {

      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

      // 创建一个定时任务，延迟1秒后开始执行，每2秒执行一次
      scheduler.scheduleAtFixedRate(new RedisReadTask(socket, regLibs), 0, 60, TimeUnit.SECONDS);
      Thread.sleep(5000);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 删除相同IP但不同设备名称的数据
  private void deleteDifferentDevNameData(ParamValueMapper paramValueMapper) {
    ParamValue pv_ip = new ParamValue();
    pv_ip.setIp(ip);
    List<ParamValue> paramValues = paramValueMapper.selectParamValueList(pv_ip);
    for (ParamValue paramValue : paramValues) {
      if (!paramValue.getDevName().equals(devName)) {
        paramValueMapper.deleteParamValueByDevName(paramValue.getDevName());
      }
    }
  }

  // 读取并存储数据
  private void readAndStoreData(InputStream in, OutputStream out, ParamValueMapper paramValueMapper) throws Exception {
    for (RegLib regLib : regLibs) {
      if (shouldSkip(regLib)) {
        continue;
      }

      // 如果是线振振幅，则将regAddr+1
      if (regLib.getParamKey().contains("prm_DZJ_X_ZF")) {
        regLib.setRegAddr(regLib.getRegAddr() + 1);
      }

      // 获取设备数据
      String value = collSingleData(in, out, regLib);

      if (regLib.getParamSubType() == 8) {
        // 如果paramSubType==8，说明是系统参数，存储到hcms_sys_settings表中
        storeSysSettingParams(devName, regLib.getParamKey(), value);
      }

      ParamValue paramValue = paramValueMapper.getParamValueByDevNameAndRegAddr(devName, regLib.getRegAddr() + "");
      if (paramValue != null) {
        // 更新数据
        paramValue.setParamValue(value != "" ? value : "0");
        paramValue.setDtUpdate(new Date());
        paramValueMapper.updateParamValue(paramValue);
        continue;
      }

      // 将采集的值存储起来
      ParamValue pv = createParamValue(regLib, value);
      paramValueMapper.insertParamValue(pv);
    }
  }

  // 判断是否应该跳过某个数据
  private boolean shouldSkip(RegLib regLib) {
    return regLib.getParamName().equals("保留")
        || (regLib.getParamSubType() == 0 && regLib.getParamType() != 2 || regLib.getParamSubType() > 8)
            && regLib.getRegAddr() != 4;
  }

  // 创建ParamValue对象
  private ParamValue createParamValue(RegLib regLib, String value) {
    ParamValue pv = new ParamValue();
    pv.setIp(ip);
    pv.setDevName(devName);
    pv.setDevModel("CBM5"); // 暂时写死
    pv.setParamKey(regLib.getParamKey());
    pv.setRegAddr(regLib.getRegAddr());
    pv.setParamType(regLib.getParamType());
    pv.setParamSubType(regLib.getParamSubType());
    pv.setDtUpdate(new Date());
    pv.setParamName(regLib.getParamName());
    pv.setParamValue(value != "" ? value : "0");
    return pv;
  }

  /**
   * modbus采集
   * 
   * @throws Exception
   */
  private String collSingleData(InputStream in, OutputStream out, RegLib regLib) throws Exception {

    String res = "";

    // 发送
    ModbusRequest req = new ModBusTcpRequest((short) 1,
        new ReadHoldingRegisterPayLoad(regLib.getRegAddr(), regLib.getRegNum()));
    ByteBuf bf = Unpooled.buffer(12);
    MbMsg.encode(req, bf);
    byte[] bytes = bf.array();
    out.write(bytes);
    out.flush();

    // 接收
    bf = Unpooled.buffer(256);
    bf.writeBytes(in, 256);
    res = MbMsg.readDataFromRes(bf, TranStrTools.transDataType(regLib.getDataType()));
    // System.out.println("客户端：" + res);
    return res;
  }

  /**
   * 采集频率增益参数
   */
  private void collFreqGainData(Socket sct) throws Exception {

    ParamValueMapper paramValueMapper = SpringUtils.getBean(ParamValueMapper.class);

    // 从130寄存器开始采集，采集32个寄存器
    byte[] bytes = ModbusUtils.ReadHoldingRegister(sct, 130, 32);

    // 因为每个寄存器有两个字节，这两个字节的顺序是反的，所以需要将这两个字节进行交换
    for (int i = 0; i < bytes.length; i++) {
      if (i % 2 == 1) {
        byte temp = bytes[i];
        bytes[i] = bytes[i - 1];
        bytes[i - 1] = temp;
      }
    }

    // 将每个字节转为一个整数，然后存储到数据库中
    for (int i = 0; i < bytes.length; i++) {

      // 共32个寄存器，每个寄存器2个字节，共64个字节
      // 对于前16个字节，存储为单个振机频率，然后跳过16个字节，再存储16个字节为单个振机增益，之后再跳过16个字节
      if (i % 16 == 0 && i != 0 && i != 32) {
        i += 15;
        continue;
      }

      // 先查询数据库中是否有该条数据，如果有，则更新，否则插入
      int regAddr = i < 16 ? 130 + i : 146 + i - 32;
      ParamValue paramValue = paramValueMapper.getParamValueByDevNameAndRegAddr(devName, regAddr + "");
      if (paramValue != null) {
        // 更新数据
        paramValue.setParamValue(bytes[i] + "");
        paramValue.setDtUpdate(new Date());
        paramValueMapper.updateParamValue(paramValue);
        continue;
      }
      ParamValue pv = new ParamValue();
      pv.setIp(ip);
      // pv.setPort(port);
      pv.setDevName(devName);
      pv.setDevModel("CBM5"); // 暂时写死
      pv.setParamType(1);
      pv.setParamSubType(10);
      pv.setDtUpdate(new Date());

      // 前16个寄存器是单个振机频率，后16个寄存器是单个振机增益
      if (i < 16) {
        pv.setParamName("单个振机频率" + (i + 1));
        pv.setRegAddr(130 + i);
        pv.setParamKey("sys_DZJ_FreqSet[" + (i + 1) + "]");
      } else {
        pv.setParamName("单个振机增益" + (i - 32 + 1));
        pv.setRegAddr(146 + i - 32);
        pv.setParamKey("sys_DZJ_GainSet[" + (i - 32 + 1) + "]");
      }
      pv.setParamValue(String.valueOf(bytes[i]));
      paramValueMapper.insertParamValue(pv);
    }
  }

  // 存储系统设置参数
  private void storeSysSettingParams(String devName, String key, String value) {
    SysSettingsMapper sysSettingsMapper = SpringUtils.getBean(SysSettingsMapper.class);
    SysSettings qSettings = new SysSettings();
    qSettings.setDevName(devName);
    qSettings.setParamKey(key);
    SysSettings sysSettings = sysSettingsMapper.getSysSettingsByDevNameAndParamKey(qSettings);
    if (sysSettings == null) {
      sysSettings = new SysSettings();
      sysSettings.setDevName(devName);
      sysSettings.setParamKey(key);
      sysSettings.setParamValue(value);
      sysSettings.setDtUpdate(new Date());
      sysSettingsMapper.insertSysSettings(sysSettings);
    } else {
      sysSettings.setParamKey(key);
      sysSettings.setParamValue(value);
      sysSettings.setDtUpdate(new Date());
      sysSettingsMapper.updateSysSettings(sysSettings);
    }
  }

}
