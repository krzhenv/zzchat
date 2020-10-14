package com.mychat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ClientUI {
    //1.1 UI组件
    private static JButton ConnectServer;//连接
    private static JButton DisconnectServer;//断开
    private static JButton SendMessageButton;//发送
    private static JTextField NickNameText;//昵称
    private static JTextField ServerIPAddressText;//服务器ip
    private static JTextField ServerPortText;//服务器端口
    private static JTextField InputContentText;//输入内容
    private static JList OnlineClientList;//在线列表
    private static JLabel ChatContentLabel;//聊天内容

    //1.3  用户昵称
    private static DefaultListModel<String> OnlineClientNickName;//在线用户昵称列表：向其中插入数据，自动将数据插入到JList中

    //4. UI相关
    //4.1界面
    public ClientUI() {
        //4.1.1 总窗口
        JFrame ClientFrame = new JFrame("客户端");
        ClientFrame.setSize(800,600);//设置长宽，注意数值不需要引号
        ClientFrame.setLocationRelativeTo(null);//设置在屏幕中央显示
        ClientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认关闭按钮，不设置也可以

        //4.1.2 客户端信息
        JPanel ClientIdPanel = new JPanel();
        ClientIdPanel.setLayout(new FlowLayout(FlowLayout.LEFT));//4.2.1 设置客户端id栏的布局为浮动布局
        ClientIdPanel.setSize(800, 100);
        //4.1.2.2 昵称栏
        JLabel NickNameLabel = new JLabel("昵称");
        NickNameText = new JTextField(10);
        NickNameText.setText("jiangbowen");
        ClientIdPanel.add(NickNameLabel);
        ClientIdPanel.add(NickNameText);
        //4.1.2.3 服务器IP地址
        JLabel ServerIPAddressLabel = new JLabel("IP地址");
        ServerIPAddressText = new JTextField(10);
        ServerIPAddressText.setText("127.0.0.1");
        ClientIdPanel.add(ServerIPAddressLabel);
        ClientIdPanel.add(ServerIPAddressText);
        //4.1.2.4 端口号
        JLabel ServerPortLabel = new JLabel("端口");
        ServerPortText = new JTextField(10);
        ServerPortText.setText("8288");
        ClientIdPanel.add(ServerPortLabel);
        ClientIdPanel.add(ServerPortText);
        //4.1.2.5 连接服务器/断开连接
        ConnectServer = new JButton("连接");
        DisconnectServer = new JButton("断开");
        ClientIdPanel.add(ConnectServer);
        ClientIdPanel.add(DisconnectServer);
        //4.1.2.6 设置标题
        ClientIdPanel.setBorder(new TitledBorder("用户信息栏"));

        //4.1.3 好友列表
        JPanel FriendListPanel = new JPanel();
        FriendListPanel.setPreferredSize(new Dimension(200,400));
        FriendListPanel.setBorder(new TitledBorder("好友列表"));
        //4.1.3.1 好友列表内容
        OnlineClientNickName = new DefaultListModel<String>();
        OnlineClientList = new JList(OnlineClientNickName);
        FriendListPanel.add(OnlineClientList);

        //4.1.4 聊天内容面板
        JPanel ChatContentPanel = new JPanel();
        ChatContentPanel.setPreferredSize(new Dimension(490,400));
        ChatContentPanel.setBorder(new TitledBorder("聊天内容"));
        //4.1.4.1 声明聊天内容标签
        ChatContentLabel = new JLabel("<html>");
        ChatContentLabel.setPreferredSize(new Dimension(490,400));
        ChatContentPanel.add(ChatContentLabel);

        //4.1.5 输入内容面板
        JPanel InputContentPanel = new JPanel();
        InputContentPanel.setPreferredSize(new Dimension(600,100));
        //4.1.5.1 聊天输入框
        InputContentText = new JTextField();
        InputContentText.setPreferredSize(new Dimension(600,60));
        //4.1.5.2 发送按钮
        SendMessageButton = new JButton("发送");
        InputContentPanel.add(InputContentText);
        InputContentPanel.add(SendMessageButton);
        InputContentPanel.setBorder(new TitledBorder("输入内容"));

        //4.1.6 客户端整体布局
        ClientFrame.add(ClientIdPanel, BorderLayout.NORTH);
        ClientFrame.add(FriendListPanel, BorderLayout.WEST);
        ClientFrame.add(ChatContentPanel,BorderLayout.CENTER);
        ClientFrame.add(InputContentPanel,BorderLayout.SOUTH);
        //4.1.7设置可见
        ClientFrame.setVisible(true);	//设置可见必须在所有内容都add进Frame之后

    }

    public static JTextField getServerIPAddressText() {
        return ServerIPAddressText;
    }

    public static JTextField getServerPortText() {
        return ServerPortText;
    }

    public static JTextField getNickNameText() {
        return NickNameText;
    }

    public static DefaultListModel<String> getOnlineClientNickName() {
        return OnlineClientNickName;
    }

    public static JLabel getChatContentLabel() {
        return ChatContentLabel;
    }

    public static JButton getConnectServer() {
        return ConnectServer;
    }

    public static JButton getDisconnectServer() {
        return DisconnectServer;
    }

    public static JButton getSendMessageButton() {
        return SendMessageButton;
    }

    public static JTextField getInputContentText() {
        return InputContentText;
    }

    public static JList getOnlineClientList() {
        return OnlineClientList;
    }


}
