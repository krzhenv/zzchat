package com.mychat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ServerUI {

    //1.1 UI组件
    private static JButton StartServer;//开始服务器
    private static JButton StopServer;//停止服务器
    private static JButton SendMessageButton;//发送消息
    private static JTextField MaxNumberText;//最大连接人数
    private static JTextField ServerPortText;//监听端口
    private static JTextField InputContentText;//消息输入框
    private static JList OnlineClientList;//在线列表
    private static JLabel LogsLabel;//日志栏

    private static DefaultListModel<String> OnlineClientNickName;//在线用户昵称，向其中插入数据，自动将数据插入到JList中


    public ServerUI() {
        //4.1 生成一个JFrame窗体
        JFrame ServerFrame = new JFrame("服务器");
        //4.1.1设置长宽，注意数值不需要引号
        ServerFrame.setSize(800,600);
        //4.1.2设置在屏幕中央显示
        ServerFrame.setLocationRelativeTo(null);
        //4.1.3设置默认关闭按钮，不设置也可以
        ServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //4.2 声明服务端信息栏
        JPanel ServerIdPanel = new JPanel();
        //4.2.1 设置服务端信息栏的布局为浮动布局
        ServerIdPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ServerIdPanel.setSize(800, 100);
        //4.2.2 最大连接人数
        JLabel MaxNumberLabel = new JLabel("    最大连接人数");
        MaxNumberText = new JTextField(10);
        MaxNumberText.setText("10");
        ServerIdPanel.add(MaxNumberLabel);
        ServerIdPanel.add(MaxNumberText);
        //4.2.3 端口号
        JLabel ServerPortLabel = new JLabel("    端口");
        ServerPortText = new JTextField(10);
        ServerPortText.setText("8288");
        ServerIdPanel.add(ServerPortLabel);
        ServerIdPanel.add(ServerPortText);
        //4.2.5 启动/停止服务器
        StartServer = new JButton("启动");
        StopServer = new JButton("停止");
        ServerIdPanel.add(StartServer);
        ServerIdPanel.add(StopServer);
        //4.2.6 设置标题
        ServerIdPanel.setBorder(new TitledBorder("服务器信息栏"));

        //4.3 在线用户列表栏
        JPanel FriendListPanel = new JPanel();
        FriendListPanel.setPreferredSize(new Dimension(200,400));
        FriendListPanel.setBorder(new TitledBorder("好友列表"));
        //4.3.1 好友列表内容
        OnlineClientNickName = new DefaultListModel<String>();
        OnlineClientList = new JList(OnlineClientNickName);
        FriendListPanel.add(OnlineClientList);

        //4.4 日志面板
        JPanel LogsPanel = new JPanel();
        LogsPanel.setPreferredSize(new Dimension(590,400));
        LogsPanel.setBorder(new TitledBorder("日志内容"));
        //4.4.1 日志内容标签
        LogsLabel = new JLabel("<html>");
        LogsLabel.setPreferredSize(new Dimension(590,400));
        LogsPanel.add(LogsLabel);

        //4.5 声明输入内容面板
        JPanel InputContentPanel = new JPanel();
        InputContentPanel.setPreferredSize(new Dimension(600,100));
        //4.5.1 定义聊天输入框
        InputContentText = new JTextField();
        InputContentText.setPreferredSize(new Dimension(600,60));
        //4.5.2 发送按钮
        SendMessageButton = new JButton("发送");
        InputContentPanel.add(InputContentText);
        InputContentPanel.add(SendMessageButton);
        InputContentPanel.setBorder(new TitledBorder("输入内容"));

        //4.6 服务端整体布局
        ServerFrame.add(ServerIdPanel, BorderLayout.NORTH);
        ServerFrame.add(FriendListPanel, BorderLayout.WEST);
        ServerFrame.add(LogsPanel, BorderLayout.CENTER);
        ServerFrame.add(InputContentPanel,BorderLayout.SOUTH);

        //4.7 设置可见
        ServerFrame.setVisible(true);

        //4.8 添加监听事件
    }

    public static JButton getStartServer() {
        return StartServer;
    }

    public static JButton getStopServer() {
        return StopServer;
    }

    public static JButton getSendMessageButton() {
        return SendMessageButton;
    }

    public static JTextField getMaxNumberText() {
        return MaxNumberText;
    }

    public static JTextField getServerPortText() {
        return ServerPortText;
    }

    public static JTextField getInputContentText() {
        return InputContentText;
    }

    public static JList getOnlineClientList() {
        return OnlineClientList;
    }

    public static JLabel getLogsLabel() {
        return LogsLabel;
    }

    public static DefaultListModel<String> getOnlineClientNickName() {
        return OnlineClientNickName;
    }
}
