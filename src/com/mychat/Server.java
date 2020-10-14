package com.mychat;


import com.util.DBHelper;
import com.util.JDBCUtils;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Server {
    //1.v以下为必须在外部声明的变量

    //1.2 socket相关
    ServerSocket serverSocket;//服务器的socket类型为 ServerSocket
    //这里必须要把serverThread线程在外部声明，否则会出现找不到类的报错

    //1.3 线程相关
    ServerThread1 serverThread;//服务器线程：这里的命名增加了1，怀疑系统中有和他同名的类
    ConcurrentHashMap<String, ClientThread> clientThreads;//所有client的Thread，key=clientNickName, value=clientThread
    //ConcurrentHashMap对于多线程可以不一次死锁掉全部线程
    //1.4 用户昵称

    String ToTargetName  = "ALL";//信息发送的目标用户昵称

    //2.构造函数
    public Server() {
        ServerUI serverUI = new ServerUI();
        AddActionListener();
    }

    public static void main(String srgs[]) {
        Server server = new Server();

    }

    //3. 与服务器建立相关
    //3.1 开始运行服务器
    public void StartServer() {
        int ServerPort = Integer.parseInt(ServerUI.getServerPortText().getText().trim());
        try {
            //3.1.1 对相应端口建立socket
            serverSocket = new ServerSocket(ServerPort);

            //3.1.2 为服务器建立新的线程，在线程里面对socket进行监听
            serverThread = new ServerThread1();//如果不重新建立线程会导致服务器界面阻塞，无法点击

            //3.1.3新建存取每个客户端socket线程的map
            clientThreads = new ConcurrentHashMap<String,ClientThread>();

            //3.1.4 服务端在线用户列表显示ALL
            ServerUI.getOnlineClientNickName().addElement("ALL");
        } catch (BindException e) {
            // TODO Auto-generated catch block
            Error("Server：端口异常"+e.getMessage());
        } catch(Exception e) {
            Error("Server：服务器启动失败");
        }
        Success("成功运行服务器");
    }
    //3.2 断开服务器


    //4. UI相关
    //4.1 生成界面窗口

    //4.2添加监听
    private void AddActionListener() {
        ServerUI.getStartServer().addActionListener(new ActionListener() {//启动服务器
            public void actionPerformed(ActionEvent e) {
                StartServer();
            }
        });
        ServerUI.getStopServer().addActionListener(new ActionListener() {//停止服务器
            public void actionPerformed(ActionEvent e) {
                Log("Server：点击停止,暂没有作用");
            }
        });
        ServerUI.getSendMessageButton().addActionListener(new ActionListener() {//发送消息
            public void actionPerformed(ActionEvent e) {
                String message = ServerUI.getInputContentText().getText();
                if("ALL".equals(ToTargetName)) {
                    for(ConcurrentHashMap.Entry<String, ClientThread> entry: clientThreads.entrySet()) {
                        entry.getValue().SendMessage("MESSAGE@"+ToTargetName+"@SERVER@"+message);
                    }
                }else {
                    clientThreads.get(ToTargetName).SendMessage("MESSAGE@"+ToTargetName+"@SERVER@"+message);
                }
            }
        });
        ServerUI.getOnlineClientList().addListSelectionListener(new ListSelectionListener() { //检验目标发送者是谁
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // TODO Auto-generated method stub
                int index = ServerUI.getOnlineClientList().getSelectedIndex();
                if(index<0) {
                    Error("Server：消息目标用户下标错误");
                    return;
                }
                if(index == 0) {
                    ToTargetName = "ALL";
                }else {
                    String ToClientNickName = (String)ServerUI.getOnlineClientNickName().getElementAt(index);
                    ToTargetName = ToClientNickName;
                }
                Success("成功修改消息目标用户为："+ToTargetName);
            }

        });
    }
    //4.3 输出消息内容
    private void Log(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ServerUI.getLogsLabel().setText(ServerUI.getLogsLabel().getText()+message+"<br />");
    }
    //4.4 输出错误信息
    private void Error(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ServerUI.getLogsLabel().setText(ServerUI.getLogsLabel().getText()+"<span color='red'>Error："+message+"</span>"+"<br />");
    }
    //4.4 输出成功信息
    private void Success(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ServerUI.getLogsLabel().setText(ServerUI.getLogsLabel().getText()+"<span color='green'>Success："+message+"</span>"+"<br />");
    }

    //5.服务器线程
    private class ServerThread1 implements Runnable{//在serverScoket.accept()的时候防止界面死锁
        //5.1 isRuning为true时，服务器不停的accept下一个连接
        boolean isRuning = true;
        //5.1构造函数
        public ServerThread1() {
            new Thread(this).start();
        }
        @Override
        //5.2线程开始后自动运行
        public void run() {
            //5.2.1 循环accept下一个socket连接
            while(isRuning) {
                if(!serverSocket.isClosed()) {
                    try {
                        Socket socket = serverSocket.accept();//这里要为每个即将连接的客户端新建一个socket，注：必须在try内部声明socket
                        ClientThread clientThread = new ClientThread(socket);//每接收到一个服务器请求，就为其新建一个客户线程
                        String ClientNickName = clientThread.getClientNickName();
                        clientThreads.put(ClientNickName, clientThread);//将每个客户端的线程都存在ConcurrentHashMap中
                        Success("为用户"+ClientNickName+"新建线程完毕");
                    } catch (IOException e) {
                        Error("Server：建立客户线程失败"+e.getMessage());
                    }
                }else {
                    Error("Server:服务器socket已关闭");
                }
            }
        }
    }

    //6.客户端线程
    public class ClientThread implements Runnable{//为每个客户端建立线程，并存在ConcurrentHashMap中，建立新的线程可防止等待用户输入时死锁
        //6.1 与该用户的socket相关输入输出
        private Socket socket;
        private BufferedReader input;
        private PrintStream output;

        //6.2 由于服务器主线程需要以客户端姓名为key，故需要调用getClientNickName()返回昵称
        private String ClientNickName;

        //6.3用于区分第一次连接和之后接收的消息
        boolean isRuning = false;

        //6.4 构造函数
        public ClientThread(Socket clientSocket) {
            this.socket = clientSocket;
            isRuning = Initialize();
            new Thread(this).start();
        }
        //6.5 第一次生成线程时（用户刚登陆时）调用
        public synchronized boolean Initialize() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintStream(socket.getOutputStream());
                //6.5.1 接收用户的输入数据
                String clientInputStr;
                clientInputStr = input.readLine();//readline运行时阻塞，故须建立客户端线程
                Log("Client："+clientInputStr);
                //6.5.2 检验信息头是否为LOGIN，是则向所有其他用户转发
                Tokenizer tokens = new Tokenizer(clientInputStr,"@");
                String MessageType = tokens.nextToken();
                if("LOGIN".equals(MessageType)) {
                    ClientNickName = tokens.nextToken();
                    ServerUI.getOnlineClientNickName().addElement(ClientNickName);//服务端在线用户列表显示该用户昵称
                    Broadcast(clientInputStr);//向所有已登陆用户广播该用户登陆的信息
                    for(ConcurrentHashMap.Entry<String, ClientThread> entry: clientThreads.entrySet()) {
                        SendMessage("LOGIN@"+entry.getKey());
                    }
                }else {
                    Error("Server:初次接收的消息不为LOGIN");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Error("Server：Initialize无法读取下一行 "+e.getMessage());
            }
            Success("用户线程初始化完毕");
            return true;
        }
        @Override
        //6.6 线程开始时自动调用该方法
        public void run() {
            PreparedStatement stmt = null;
            Connection conn = null;
            while(isRuning) {
                try {
                    //6.6.1接收用户的输入数据
                    String clientInputStr;
                    clientInputStr = input.readLine();
                    Log("Server："+clientInputStr);

                    conn = DBHelper.getConnection();
                    String sql = "insert into tb_chatcontent values(null,'" + clientInputStr + "')";
                    stmt = conn.prepareStatement(sql);
                    int i = stmt.executeUpdate();
                    if (i > 0) {
                        System.out.println("添加成功");
                    } else {
                        System.out.println("添加失败");
                    }
                    if (stmt != null) {
                            stmt.close();
                    }
                    if (conn != null) {
                            conn.close();
                    }

                    //6.6.2按信息头部分类处理
                    Tokenizer tokens = new Tokenizer(clientInputStr,"@");
                    String MessageType = tokens.nextToken();
                    switch(MessageType) {
                        case "MESSAGE":{//消息
                            String ToClientNickName = tokens.nextToken();
                            if(ToClientNickName.equals("ALL")) {
                                //对消息进行广播转发
                                Broadcast(clientInputStr);
                                tokens.nextToken();
                                Log("Server：已将消息广播转发，消息内容为"+tokens.nextToken());
                            }else {
                                //对消息进行一对一的转发
                                clientThreads.get(ToClientNickName).SendMessage(clientInputStr);
                                Log("Server: 已将来自"+tokens.nextToken()+"的消息"+tokens.nextToken()+"转发给"+ToClientNickName);
                            }
                            break;
                        }
                        case "LOGOUT":{//登出
                            Broadcast(clientInputStr);
                            Log("Server：用户"+tokens.nextToken()+"退出");
                            break;
                        }
                        default : {
                            Error("Server: 服务器收到的消息格式错误");
                            break;
                        }
                    }
                } catch (IOException | SQLException e) {
                    // TODO Auto-generated catch block
                    Error("Server：run无法读取下一行 "+e.getMessage());
                }
            }

        }
        //6.7 返回该用户昵称
        public String getClientNickName() {
            return ClientNickName;
        }
        //6.8 发送消息
        public void SendMessage(String Message) {
            output.println(Message);
            output.flush();
        }
        //6.9 向全体在线账号发送消息
        public void Broadcast(String Message) {
            for(ConcurrentHashMap.Entry<String, ClientThread> entry: clientThreads.entrySet()) {
                entry.getValue().SendMessage(Message);
            }
        }

    }

}