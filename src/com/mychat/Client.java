package com.mychat;

import com.util.DBHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Client {
    //1.以下为必须在外部声明的变量

    //1.2 socket相关
    private  Socket socket;//input和output是通过socket定义的，如果socket关闭了，其他两个也失效
    private  BufferedReader input;//input为服务器传来的数据
    private  PrintStream output;//output为向服务器输出的数据

    private  String ToTargetName = "ALL";//目标用户昵称：OnlineClientList的监听器对其修改

    //1.4客户端线程
    ClientThread cliendThread;

    //2.构造函数
    public Client() {
        //2.1 调用UI函数显示窗口
        ClientUI clientUI = new ClientUI();
        AddActionListener();

    }

    public static void main(String srgs[]) {
        Client client = new Client();

    }

    //3.与连接服务器相关
    //3.1连接服务器
    public void ConnectServer() {
        //3.1.1 获取基本信息
        String ServerIPAddress = ClientUI.getServerIPAddressText().getText().trim();
        int ServerPort = Integer.parseInt(ClientUI.getServerPortText().getText().trim());
        String NickName = ClientUI.getNickNameText().getText();

        try {
            //3.1.2 socket相关
            socket = new Socket(ServerIPAddress, ServerPort);
            //1.获取字节输入流，2.将字节输入流转换为字符流，3.为输入流添加缓冲
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //1.获得自己输出流，2、将输出流包装为打印流
            output = new PrintStream(socket.getOutputStream());
            //3.1.2 在线列表添加所有人标签
            ClientUI.getOnlineClientNickName().addElement("所有人");
            //3.1.3 向服务器发送本帐号登陆消息
            SendMessage("LOGIN@"+NickName);

            //3.1.4 为客户端建立线程
            cliendThread = new ClientThread();

        } catch (UnknownHostException e) {
            Error("Client：主机地址异常"+e.getMessage());
            return;
        } catch (IOException e) {
            Error("Client：连接服务器异常"+e.getMessage());
            return;
        }
    }
    //3.2 断开连接

    //3.3连接或断开时的按钮是否可点击设置

    //4.2 添加事件监听
    private void AddActionListener() {
        //4.2.1 点击连接
        ClientUI.getConnectServer().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConnectServer();
            }
        });
        //4.2.2 点击断开
        ClientUI.getDisconnectServer().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        //4.2.3 点击发送
        ClientUI.getSendMessageButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = ClientUI.getInputContentText().getText().trim();//trim() 方法用于删除字符串的头尾空白符。
                SendMessage("MESSAGE@"+ToTargetName+"@"+ClientUI.getNickNameText().getText()+"@"+message);
            }
        });

        //4.2.4 检验目标发送者是谁
        ClientUI.getOnlineClientList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = ClientUI.getOnlineClientList().getSelectedIndex();
                if(index<0) {
                    new Error("Client：检测到目标发送者下标为负数");
                    return;
                }
                if(index == 0) {
                    ToTargetName = "ALL";
                }else {
                    String ToClientNickName = (String)ClientUI.getOnlineClientNickName().getElementAt(index);
                    ToTargetName = ToClientNickName;
                }
            }
        });
    }


    //4.3 输出错误（红色）
    private  void Error(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ClientUI.getChatContentLabel().setText(ClientUI.getChatContentLabel().getText()+"<span color='red'>"+message+"</span>"+"<br />");
    }

    //4.4 输出上线下线内容
    private void Log(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ClientUI.getChatContentLabel().setText(ClientUI.getChatContentLabel().getText()+"<span color='blue'>"+message+"</span>"+"<br />");
    }
    //4.5 输出私聊内容
    private void Message(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ClientUI.getChatContentLabel().setText(ClientUI.getChatContentLabel().getText()+"<span color='black'>"+message+"</span>"+"<br />");
    }
    //4.6 输出广播内容
    private void MessageTotal(String message){
        //JLabel不支持\n换行，故添加html标签进行换行，没有</html>结束标签不影响显示
        ClientUI.getChatContentLabel().setText(ClientUI.getChatContentLabel().getText()+"<span color='green'>"+message+"</span>"+"<br />");
    }


    //5. 客户端线程 内部类
    public  class ClientThread implements Runnable{
        //与服务器建立连接时，新建客户端线程，否则无法接收信息
        //与服务器断开连接时，向服务器告知，杀掉客户端进程
        //客户端调用readline时会产生死锁，故需要新建一个线程
        boolean isRuning = true;
        //5.1 构造函数
        public ClientThread () {
            //5.1.1 开始本线程
            new Thread(this).start();
        }
        @Override
        //5.2 run函数会在线程开始时自动调用
        public void run() {
            PreparedStatement stmt = null;
            Connection conn = null;
            while(isRuning) {//循环用于重复接收消息，客户端断开连接之前不停止
                // TODO Auto-generated method stub
                String message;
                try {
                    //5.2.1 在服务器传来的消息中读取下一行
                    //readline会产生死锁，如果没有下一条消息则继续等待
                    //正是因为死锁，才要新建一个客户端线程
                    message = input.readLine();
                    conn = DBHelper.getConnection();
                    String sql = "insert into tb_chatcontent values(null,'" + message + "')";
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

                    Tokenizer tokens = new Tokenizer(message, "@");//对原有消息进行分割
                    String MessageType = tokens.nextToken();

                    //5.2.2根据人为定义的传输协议对消息进行显示
                    switch(MessageType) {
                        case "LOGIN":{//其他用户上线
                            String LoginClientNickName = tokens.nextToken();
                            Log("上线通知：用户"+LoginClientNickName+"已上线");
                            ClientUI.getOnlineClientNickName().addElement(LoginClientNickName);
                            break;
                        }

                        case "MESSAGE":{//聊天消息
                            String ToClientNickName = tokens.nextToken();
                            String FromClientNickName = tokens.nextToken();
                            String content = tokens.nextToken();
                            if("ALL".equals(ToClientNickName)) {
                                MessageTotal("来自"+FromClientNickName+ "对全体的消息："+content);
                            }else {
                                Message("来自"+FromClientNickName+ "对您的私聊消息："+content);
                            }
                            break;
                        }

                        case "LOGOUT":{//其他用户下线的消息

                            break;
                        }
                        default :{
                            Error("客户端接收消息格式错误");
                            break;
                        }
                    }
                    System.out.println("客户端接收到"+message);
                } catch (IOException | SQLException e) {
                    // TODO Auto-generated catch block
                    Error("Client：客户端接收消息失败"+ e.getMessage());
                }
            }
        }
    }

    //6. 消息相关
    //6.1 发送消息
    public void SendMessage(String message) {
        output.println(message);
        output.flush();
    }

}