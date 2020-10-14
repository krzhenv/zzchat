package com.mychat;


//消息分割器
public class Tokenizer{
    String Tokens[];
    int TokenIndex = 0;
    //7.1 将消息Message按照Delimiter分割
    public Tokenizer(String Message, String Delimiter) {
        Tokens = Message.split(Delimiter);
    }
    //7.2 返回下一个内容
    public String nextToken() {
        TokenIndex++;
        return Tokens[TokenIndex-1];
    }
}
