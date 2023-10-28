// package com.ruoyi.common.utils.jsonTcp;

// import java.io.IOException;//导入IOException类
// import java.net.ServerSocket;//导入ServerSocket类   
// import java.net.Socket; //导入Socket 类 

// /**
//  * 模拟qq聊天功能： 实现客户端与服务器（一对一）的聊天功能，客户端首先发起聊天，输入的内容在服务器端和客户端显示，
//  * 然后服务器端也可以输入信息，同样信息也在客户端和服务器端显示
//  */

// // 服务器类
// public class JttServer {// ChatServer类
//     private int port = 8189;// 默认服务器端口
//     private String key = "sk#`~`#CBM5";

//     public JttServer() {

//     }

//     // 创建指定端口的服务器
//     public JttServer(int port) {// 构造方法
//         this.port = port;// 将方法参数赋值给类参数
//     }

//     // 提供服务
//     public void service() {// 创建service方法
//         // 端口可以写在配置文件
//         System.out.println("服务器在" + 5020 + "监听..");
//         try {
//             serverSocket = new ServerSocket(port);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         // new Thread(new SendNewsThread()).start();
//         try {
//             while (true) {
//                 // 当和某个客户端建立连接后，会继续监听
//                 Socket socket = serverSocket.accept();
//                 // 读取对象输入流
//                 // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                 // // 得到输出流
//                 // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                 // TODO 读取标识符，这里先用ip代替
//                 String key = socket.getInetAddress().getHostAddress();
//                 // 创建线程与客户端保持通信
//                 JttSeverConClientThread serverConClientThread = new JttSeverConClientThread(socket,
//                         key);
//                 serverConClientThread.start();
//                 // 放入集合
//                 JttSocketManage
//                         .addServerConClientThread(key, serverConClientThread);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         } finally {
//             // 如果退出while关闭serverSocket
//             try {
//                 serverSocket.close();
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }
//     }

//     public static void main(String[] args) {// 主程序方法
//         new JttServer().service();// 调用 service方法
//     }
// }
