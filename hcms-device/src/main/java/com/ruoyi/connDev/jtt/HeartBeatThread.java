package com.ruoyi.connDev.jtt;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.JTcpFunc;
import com.ruoyi.common.utils.sign.Md5Utils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.service.IDevMsgService;

public class HeartBeatThread extends Thread {

    private static String key = "sk#`~`#CBM5";
    // private static final int CHECK_MILLS = 1000 * 60 * 5;
    private static final int CHECK_MILLS = 50 * 1000;

    private boolean isStop = false;
    private JSONObject jo = new JSONObject();
    private JttSeverConClientThread jst = null;
    private Socket socket = null;
    private DevMsg devMsg = null;

    public HeartBeatThread(Socket socket, JSONObject jo, JttSeverConClientThread jst, DevMsg devMsg) {
        this.socket = socket;
        this.jo = jo;
        this.jst = jst;
        this.devMsg = devMsg;
    }

    @Override
    public void run() {
        boolean loop = true;
        while (loop) {
            // this.isStop = false;
            try {
                // 发送心跳保持连接
                while (!isStop) {
                    // 构建json请求
                    OutputStream out = socket.getOutputStream();
                    InputStream in = socket.getInputStream();
                    jst.writeJsonReq(out, JTcpFunc.HEART_BEAT, jo);

                    // 读取返回请求
                    byte[] buffer = new byte[1024];
                    in.read(buffer);
                    String accpet = new String(buffer, StandardCharsets.UTF_8);
                    // System.out.println("接收到心跳响应" + accpet);
                    // System.out.println(accpet.length());
                    // 3.接收json请求
                    if (accpet.indexOf("\0\0") != -1 && accpet.indexOf("\0\0") != accpet.indexOf('\0')) { // 如果是json字符串-检测是不是以\0-字符串-\0\0结尾
                        // 截取
                        String joStr = accpet.substring(0, accpet.indexOf("\0"));
                        String md5 = accpet.substring(accpet.indexOf("\0") + 1, accpet.lastIndexOf("\0\0"));
                        // 转换json，提取关键参数并校验
                        JSONObject json = JSONObject.parseObject(joStr);
                        // 校验random
                        String random_ = json.getString("random");
                        // System.out.println("接收到random:" + random_);
                        if (!random_.equals(jst.getMyRandom())) {
                            jst.writeJsonRespErr(out, -1, "random错误");
                            return;
                        }
                        // 更新random
                        String random = json.getString("myRandom");
                        if (!random.equals("") && random != null) {
                            // System.out.println("更新random为：" + random);
                            jst.setRandom(random);
                        }
                        // 校验md5
                        String md5_ = Md5Utils.hash(joStr + key);
                        if (!md5.trim().equals(md5_)) {
                            jst.writeJsonRespErr(out, -1, "MD5签名错误");
                            return;
                        }
                        // 校验json
                        if (json.isEmpty()) {
                            jst.writeJsonRespErr(out, -1, "JSON格式错误");
                            return;
                        }
                        // 更新设备状态
                        devMsg.setStatus(1);
                        devMsg.setDtCreate(new Date());
                        devMsg.setDtUpdate(new Date());
                        SpringUtils.getBean(IDevMsgService.class).updateDevMsg(devMsg);
                        break;
                    }
                }
                Thread.sleep(CHECK_MILLS);
            } catch (Exception e) {
                // 同时更新设备状态
                devMsg.setStatus(0);
                SpringUtils.getBean(IDevMsgService.class).updateDevMsg(devMsg);
                e.printStackTrace();
                isStop = true;
                loop = false;
                System.out.println("心跳线程异常");
            }
        }
    }

    /**
     * 暂时停止心跳
     */
    public void stopHeartBeat() {
        this.isStop = true;
    }

    /**
     * 恢复心跳
     */
    public void resumeHeartBeat() {
        this.isStop = false;
    }
}
