package com.ruoyi;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.JTcpFunc;
import com.ruoyi.common.utils.sign.Md5Utils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.device.domain.ParamValue;


public class CollServer {

    private int port = 8848;// 服务器监听窗口
    private String random = ""; //客户端发来的随机字符串
    // private String myRandom = UUID.randomUUID().toString().substring(0, 6); // 随机字符串
    private String myRandom = "";
    private String key = "sk#`~`#CBM5";
    private ServerSocket serverSocket;  // 定义服务器套接字
    // 创建动态线程池，适合小并发量，容易出现OutOfMemoryError
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private ReentrantLock lock = new ReentrantLock();

    public CollServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器启动监听在" + port + "端口...");
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        // 获得输出流缓冲区的地址
        OutputStream socketOut = socket.getOutputStream();
        // 网络流写出需要使用flush，这里在printWriter构造方法直接设置为自动flush
        return new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        // 获得输入流缓冲区的地址
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    private JSONObject readJson(String msg) {
        // 截取
        String joStr = msg.substring(0, msg.indexOf("\0"));
        System.out.println("接收到json字符串："+joStr);
        String md5 = msg.substring(msg.indexOf("\0") + 1, msg.lastIndexOf("\0\0"));
        // 转换json，提取关键参数并校验
        JSONObject jo = JSONObject.parseObject(joStr);
        return jo;
    }

    /**
       * writeJsonRespErr
       * @param args
     * @throws IOException
       */
      private void writeJsonRespErr(PrintWriter pw,int code, String msg) throws IOException{
        System.out.println(msg);
        JSONObject jo = new JSONObject();
        jo.put("fun", "net_up");
        jo.put("error", code);
        jo.put("message", msg);
        jo.put("random", random);
        jo.put("myRandom", myRandom);
        jo.put("z_content", new JSONObject());
        pw.println(jo.toString());
      }

      
    /**
       * writeJsonResp
       * @param args
     * @throws IOException
       */
      private void writeJsonResp(PrintWriter pw, String fun, int code, String msg, JSONObject content) throws IOException{
        System.out.println(msg);
        JSONObject jo = new JSONObject();
        jo.put("fun", fun);
        jo.put("error", code);
        jo.put("message", msg);
        jo.put("random", random);
        jo.put("myRandom", myRandom);
        jo.put("z_content", content);
        pw.println(jo.toString());
      }

    // 多客户版本，可以同时与多用户建立通信连接
    public void Service() throws IOException {
        while (true) {
            Socket socket = null;
            socket = serverSocket.accept();
            // 将服务器和客户端的通信交给线程池处理
            Handler handler = new Handler(socket);
            executorService.execute(handler);
        }
    }

    class Handler implements Runnable {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            // 本地服务器控制台显示客户端连接的用户信息
            System.out.println("New connection accept:" + socket.getInetAddress());
            SocketAddress sa = socket.getRemoteSocketAddress();
            InetSocketAddress isa = (InetSocketAddress)sa;
            System.out.println(isa.getAddress().getHostAddress());
            System.out.println(socket.getInetAddress().getHostAddress());
            System.out.println(socket.getLocalAddress());
            System.out.println(socket.getLocalAddress().getHostAddress());
            System.out.println(socket.getLocalSocketAddress());
            String devName = null;
            try {
                BufferedReader br = getReader(socket);
                PrintWriter pw = getWriter(socket);

                // pw.println("From 服务器：欢迎使用服务！");
                pw.println("123");

                String msg = null;
                // while ((msg = br.readLine()) != null) {
                //     System.out.println(msg);
                //     // 识别json
                //     if (msg.indexOf("\0\0") != -1 && msg.indexOf("\0\0") != msg.indexOf('\0')) {
                //         System.out.println(readJson(msg));
                //     }
                //     if (msg.trim().equalsIgnoreCase("bye")) {
                //         pw.println("From 服务器：服务器已断开连接，结束服务！");

                //         System.out.println("客户端离开。");
                //         break;
                //     }

                //     pw.println("From 服务器：" + msg);
                //     pw.println("来自服务器,重复消息：" + msg);
                // }

                // 将生成的随机字符串发向客户端
                myRandom = UUID.randomUUID().toString().substring(0, 6); // 随机字符串
                pw.println(myRandom + "\0\0");
                List<ParamValue> pvs = new ArrayList<>();
                while(true){
                    msg = br.readLine();
                    // 识别json
                    if (msg.indexOf("\0\0") != -1 && msg.indexOf("\0\0") != msg.indexOf('\0')) {
                        System.out.println("接收到json请求");
                        // 截取
                        String joStr = msg.substring(0, msg.indexOf("\0"));
                        String md5 = msg.substring(msg.indexOf("\0")+1, msg.lastIndexOf("\0\0"));
                        // 转换json，提取关键参数并校验
                        JSONObject jo = JSONObject.parseObject(joStr);
                        // TODO 校验设备名称

                        // 校验random
                        String random = jo.getString("random");
                        System.out.println("接收到random:"+random);
                        if (!random.equals(myRandom)) {
                            writeJsonRespErr(pw, -1, "random错误");
                            break;
                        }
                        // 校验md5
                        String md5_ = Md5Utils.hash(joStr + key);
                        if (!md5.trim().equals(md5_)) {
                            writeJsonRespErr(pw, -1, "MD5签名错误");
                            break;
                        }
                        // 校验json
                        if (jo.isEmpty()) {
                            writeJsonRespErr(pw, -1, "JSON格式错误");
                            break;
                        }
                        // 检测fun
                        String fun =  jo.getString("fun");
                        int code = jo.getIntValue("code");
                        String[] carr = {JTcpFunc.NET_UP, JTcpFunc.LAN_UP, JTcpFunc.COLL_DEV_DATA};
                        System.out.println(fun);
                        if (!Arrays.asList(carr).contains(fun)) {
                            writeJsonRespErr(pw, -1, "fun错误");
                            break;
                        }
                        // 根据不同的fun执行不同的操作
                        if(fun.equals(JTcpFunc.CONNECT)){
                            // TODO 加入到socket集合中
                        }else if(fun.equals(JTcpFunc.NET_UP)){
                            // 4.如果error = 0，发送文件
                            int error = jo.getIntValue("error");
                            String message = jo.getString("message");
                            if (message.equals("接收成功")) {
                                break;
                            }
                            System.out.println(message);
                            if (error == 0) {
                                lock.lock();
                                System.out.println("开始发送文件");
                                // sendFile(out, tranFile);
                                lock.unlock();
                                // return false;
                            }
                            
                        }else if (fun.equals(JTcpFunc.COLL_DEV_DATA)) {
                            if (code == 0) {
                                // 发送采集请求
                                writeJsonResp(pw, JTcpFunc.COLL_DEV_DATA, 0, "准备接收设备json数据", new JSONObject());
                            }else if (code == 1) {
                                // 将jsonObject转化为对象并加入接收列表
                                ParamValue pv = JSON.parseObject(joStr, ParamValue.class);
                                pvs.add(pv);
                            }else if(code == 2 || code == -1) {
                                // 接收完毕或接收错误，断开连接
                                break;
                            }
                        } else if(fun.equals(JTcpFunc.CHECK_UPDATE)){
                            // 返回软件和语言包名称和公网下载地址
                            JSONObject content = new JSONObject();
                            content.put("soft_name", pvs);
                            content.put("lan_name", pvs);
                            content.put("soft_url", pvs);
                            content.put("lan_url", pvs);
                            writeJsonResp(pw, JTcpFunc.CHECK_UPDATE, 0, "下载地址", content);
                        } else if(fun.equals(JTcpFunc.HEART_BEAT)){
                            // TODO 接收到心跳，返回心跳
                            writeJsonResp(pw, JTcpFunc.HEART_BEAT, 0, "心跳正常", new JSONObject());
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
     * 
     * @param args
     */
    private byte[] int2Bytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) (i >> 16);
        bytes[2] = (byte) (i >> 8);
        bytes[3] = (byte) i;
        return bytes;
    }

    // TODO 从线程map中移除

    public static void main(String[] args) throws IOException {
        new CollServer().Service();
    }

}
