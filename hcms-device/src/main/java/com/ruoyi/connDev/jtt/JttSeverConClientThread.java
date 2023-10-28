package com.ruoyi.connDev.jtt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.JTcpFunc;
import com.ruoyi.common.utils.sign.Md5Utils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.domain.upgrade;
import com.ruoyi.device.service.IDevMsgService;
import com.ruoyi.device.service.IupgradeService;

public class JttSeverConClientThread extends Thread {
    // private String host = "localhost";// 默认连接到本机
    // private int port = 5020;// 默认连接到端口5020
    private String random = ""; // 服务端发来的随机字符串
    private String myRandom = UUID.randomUUID().toString().substring(0, 6); // 随机字符串
    public static String key = "sk#`~`#CBM5";
    private File tranFile;
    private ReentrantLock lock = new ReentrantLock();
    private Socket socket = null;
    private String ip = null;// 与服务端通信的设备ip
    private String uuid = null; // 与服务器通信的设备uuid

    HeartBeatThread hbt = null;

    public JttSeverConClientThread(Socket socket, String ip) {
        this.socket = socket;
        this.ip = ip;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getRandom() {
        return random;
    }

    public String getMyRandom() {
        return myRandom;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public void setMyRandom(String myRandom) {
        this.myRandom = myRandom;
    }

    public String getKey() {
        return key;
    }

    public void setFile(File file) {
        this.tranFile = file;
    }

    public String getUuid(){
        return this.uuid;
      }
    
    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    @Override
    public void run() {
        DevMsg devMsg = new DevMsg();
        try {
            System.out.println("jtt-sc: 新的设备接入");
            OutputStream os = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            int n = 2;
            while (n > 0) {
                n--;
                // 接收来自服务器的信息
                byte[] buffer = new byte[1024];
                in.read(buffer);
                String accpet = new String(buffer, StandardCharsets.UTF_8);
                System.out.println("服务器：" + accpet);
                System.out.println(accpet.length());
                System.out.println(accpet.indexOf("\0\0"));
                // 1.如果是随机字符串
                if (accpet.indexOf("\0\0") != -1 && accpet.indexOf("\0\0") == accpet.indexOf('\0')) {
                    random = accpet.substring(0, accpet.indexOf("\0"));
                    System.out.println("接收到随机字符串" + random);
                    // 2.发送json请求
                    if (!random.equals("")) {
                        System.out.println("开始写请求");
                        // 获取设备信息
                        writeJsonReq(os, JTcpFunc.MAC_INFO, new JSONObject());
                    }
                }
                // 3.接收json请求
                if (accpet.indexOf("\0\0") != -1 && accpet.indexOf("\0\0") != accpet.indexOf('\0')) { // 如果是json字符串-检测是不是以\0-字符串-\0\0结尾
                    System.out.println("接收到json请求");
                    // 截取
                    String joStr = accpet.substring(0, accpet.indexOf("\0"));
                    String md5 = accpet.substring(accpet.indexOf("\0") + 1, accpet.lastIndexOf("\0\0"));
                    // 转换json，提取关键参数并校验
                    JSONObject json = JSONObject.parseObject(joStr);
                    // 校验random
                    String random_ = json.getString("random");
                    System.out.println("接收到random:" + random_);
                    if (!random_.equals(myRandom)) {
                        writeJsonRespErr(os, -1, "random错误");
                        return;
                    }
                    // 更新random
                    random = json.getString("myRandom");
                    System.out.println("更新random为：" + random);
                    // 校验md5
                    String md5_ = Md5Utils.hash(joStr + key);
                    if (!md5.trim().equals(md5_)) {
                        writeJsonRespErr(os, -1, "MD5签名错误");
                        return;
                    }
                    // 校验json
                    if (json.isEmpty()) {
                        writeJsonRespErr(os, -1, "JSON格式错误");
                        return;
                    }
                    // 获取设备名称并存储起来
                    String z_content = json.getString("z_content");
                    JSONObject jObj = JSONObject.parseObject(z_content);
                    String devName = jObj.getString("name");
                    // 假如devName是空字符串，则使用ip作为名称
                    if (devName == null || devName.equals("")) {
                        devName = ip;
                    }
                    JttSocketManage.getShadow().put(ip, devName);
                    JttSocketManage.getShadow().put(devName, ip);
                    JttSocketManage.addServerConClientThread(devName, JttSocketManage.getServerConClientThread(ip));
                    String devModel = jObj.getString("type");
                    String uuid = jObj.getString("uuid"); // TODO 用作后续比对
                    String cpusn = jObj.getString("cpusn");
                    System.out.println(devName);
                    devMsg.setDevName(devName);
                    devMsg.setDevCpusn(cpusn);
                    // 根据名称查询是否有这台机器, 假如有，删除原先的记录再插入
                    IDevMsgService devMsgService = SpringUtils.getBean(IDevMsgService.class);
                    List<DevMsg> dms = devMsgService.selectDevMsgList(devMsg);
                    if (dms.size() > 0) {
                        devMsgService.deleteDevMsgById(dms.get(0).getId());
                    }
                    devMsg.setDevModel(devModel);
                    devMsg.setUuid(uuid);
                    devMsg.setIp(socket.getInetAddress().getHostAddress());
                    devMsg.setStatus(1);
                    devMsg.setDtCreate(new Date());
                    devMsg.setDtUpdate(new Date());
                    devMsgService.insertDevMsg(devMsg);
                    // 增加一条软件更新记录
                    IupgradeService upgradeService = SpringUtils.getBean(IupgradeService.class);
                    // 先删除（删除要等到后面再做，因为现在没有版本信息）
                    // upgradeService.deleteupgradeByDevName(devName);
                    // 后插入
                    upgrade t_upgrade = new upgrade();
                    t_upgrade.setDevName(devName);
                    List<upgrade> upgrades = upgradeService.selectupgradeList(t_upgrade);
                    if (upgrades.size() <= 0) {
                        t_upgrade.setName("software_1");
                        t_upgrade.setDtUpdate(new Date());
                        upgradeService.insertupgrade(t_upgrade);
                    }

                }
            }

            // 发送心跳检测
            JSONObject jo = new JSONObject();
            jo.put("fun", JTcpFunc.HEART_BEAT);
            jo.put("random", random);
            jo.put("myRandom", myRandom);
            jo.put("z_content", new JSONObject());
            hbt = new HeartBeatThread(socket, jo, this, devMsg);
            hbt.start();

        } catch (Exception e) {
            e.printStackTrace();
            // 关闭socket
            try {
                System.out.println("jtt-sc: socket是否已关闭：" + socket.isClosed());
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // 删除线程
            // this.interrupt();
        }
    }

    /**
     * 发起文件请求通信
     */
    public boolean startSendFile(long filesize) {
        System.out.println("socket is" + socket.isConnected());
        try {
            // 发送文件之前设置超时时间，防止文件传输过程中超时
            // 根据文件大小设置超时时间, 留下一定的缓冲时间
            // 计算公式：文件大小/服务器带宽=传输时间
            int timeout = (int) filesize / 1024 / 1024 / 1 * 30 + 10;
            socket.setSoTimeout(1000 * timeout);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            boolean loop = true;
            boolean first = true;
            // 停止心跳检测
            hbt.stopHeartBeat();
            while (loop) {
                System.out.println("服务端和客户端" + ip + "保持通信");

                // 2.发送json请求
                if (!random.equals("") && first) {
                    System.out.println("开始写请求");
                    writeJsonReq(out, JTcpFunc.NET_UP, new JSONObject());
                    first = false;
                }
                // 接收来自服务器的信息
                byte[] buffer = new byte[1024];
                in.read(buffer);
                String accpet = new String(buffer, StandardCharsets.UTF_8);
                System.out.println("服务器：" + accpet);
                System.out.println(accpet.length());
                System.out.println(accpet.indexOf("\0\0"));

                // 3.接收json请求
                if (accpet.indexOf("\0\0") != -1 && accpet.indexOf("\0\0") != accpet.indexOf('\0')) { // 如果是json字符串-检测是不是以\0-字符串-\0\0结尾
                    System.out.println("接收到json请求");
                    // 截取
                    String joStr = accpet.substring(0, accpet.indexOf("\0"));
                    String md5 = accpet.substring(accpet.indexOf("\0") + 1, accpet.lastIndexOf("\0\0"));
                    // 转换json，提取关键参数并校验
                    JSONObject jo = JSONObject.parseObject(joStr);
                    // 校验random
                    String random_ = jo.getString("random");
                    System.out.println("接收到random:" + random_);
                    if (!random_.equals(myRandom)) {
                        writeJsonRespErr(out, -1, "random错误");
                        return false;
                    }
                    // 更新random
                    this.random = jo.getString("myRandom");
                    System.out.println("更新random为：" + random);
                    // 校验md5
                    String md5_ = Md5Utils.hash(joStr + key);
                    if (!md5.trim().equals(md5_)) {
                        writeJsonRespErr(out, -1, "MD5签名错误");
                        return false;
                    }
                    // 校验json
                    if (jo.isEmpty()) {
                        writeJsonRespErr(out, -1, "JSON格式错误");
                        return false;
                    }
                    // 检测fun
                    String fun = jo.getString("fun");
                    System.out.println(fun);
                    if (!fun.equals(JTcpFunc.NET_UP)) {
                        writeJsonRespErr(out, -1, "fun错误");
                        return false;
                    }
                    // 4.如果error = 0，发送文件
                    int error = jo.getIntValue("error");
                    String message = jo.getString("message");
                    if (message.equals("接收成功")) {
                        // 发送文件之后重置超时时间
                        socket.setSoTimeout(1000 * 10);
                        // 重置心跳检测
                        hbt.resumeHeartBeat();
                        // 重置random
                        // random = "";
                        return true;
                    }
                    System.out.println(message);
                    if (error == 0) {
                        lock.lock();
                        System.out.println("开始发送文件");
                        sendFile(out, tranFile);
                        lock.unlock();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送json请求
     * 
     * @return
     * @throws IOException
     */
    public void writeJsonReq(OutputStream out, String funCode, JSONObject jo) throws IOException {
        // 构建json请求
        jo.put("fun", funCode);
        jo.put("random", random);
        jo.put("myRandom", myRandom);
        jo.put("z_content", new JSONObject());
        String joStr = jo.toString();
        String md5 = Md5Utils.hash(joStr + key);
        String reqStr = joStr + "\0" + md5 + "\0\0";
        // System.out.println("发送json请求" + reqStr);
        out.write(reqStr.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    /**
     * 发送文件
     * 
     * @param args
     */
    private void sendFile(OutputStream out, File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        String fileName = file.getName();
        // 文件名字节数
        int fnSize = fileName.getBytes(StandardCharsets.UTF_8).length;
        ByteBuffer buffer = ByteBuffer.wrap(int2Bytes(fnSize));

        System.out.println(fnSize);
        out.write(buffer.array());
        // 文件名
        byte[] fileName_ = fileName.getBytes(StandardCharsets.UTF_8);
        out.write(fileName_);
        // 文件内容字节数
        int filesize = (int) (file.length());
        System.out.println(filesize);
        out.write(int2Bytes(filesize));
        // 文件内容
        byte[] fileContent = new byte[fis.available()];
        System.out.println("=====开始传输文件=====");
        fis.read(fileContent);
        byte[] bytes = new byte[10240];
        int len = 0;
        long progress = 0;
        System.out.println(len);
        FileInputStream fis_ = new FileInputStream(file);
        while ((len = fis_.read(bytes, 0, bytes.length)) != -1) {
            out.write(bytes, 0, len);
            // out.flush();
            progress += len;
            System.out.println("| " + (100 * progress / file.length()) + "% |");
        }
        // out.write(fileContent);
        System.out.println("======文件传输成功=====");
        // 检验和
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(fileName_);
        bos.write(fileContent);
        // System.out.println("使用更新后的random："+random);
        bos.write(random.getBytes(StandardCharsets.UTF_8));
        bos.write(key.getBytes(StandardCharsets.UTF_8));
        byte[] ck = Md5Utils.md5b(bos.toByteArray());
        // System.out.println(ck.toString());
        // System.out.println(ck.length);
        // 检验和字节数
        int ckSize = 16;
        out.write(int2Bytes(ckSize));
        out.write(ck);

        fis_.close();
        fis.close();
    }

    /**
     * writeJsonRespErr
     * 
     * @param args
     * @throws IOException
     */
    public void writeJsonRespErr(OutputStream out, int code, String msg) throws IOException {
        // System.out.println(msg);
        JSONObject jo = new JSONObject();
        jo.put("fun", "net_up");
        jo.put("error", code);
        jo.put("message", msg);
        jo.put("random", random);
        jo.put("myRandom", myRandom);
        jo.put("z_content", new JSONObject());
        out.write(jo.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 整型转二进制数组：大端
     */
    private byte[] int2Bytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) (i >> 16);
        bytes[2] = (byte) (i >> 8);
        bytes[3] = (byte) i;
        return bytes;
    }
}
