package com.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 精简版tomcat
 */
public class HttpServer {

    //设置访问web资源目录
    public static final String WBE_ROOT = System.getProperty("user.dir")
            + File.separator + "webRoot";

    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    private boolean shutdown = false;


    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            //backlog=1表示允许一个客户端socket连接
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("serverSocket创建出现异常。。");
            System.exit(1);
        }

        while (!shutdown) {
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                //监听客户端访问服务端8080端口，如果有客户端连接，则和客户端建立一个双向流
                socket = serverSocket.accept();
                System.out.println("有客户端连接进来了。。。");

                try {
                    System.out.println("正在处理中。。");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                //将inputStream由Request去解析
                Request request = new Request(inputStream);
                request.parse();

                //将outputStream由Response去解析
                Response response = new Response(outputStream);
                response.setRequest(request);
                response.sendStaticResource();

                //关闭socket
                socket.close();
                //请求的uri=SHUTDOWN,则停服务
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        System.out.println("服务端socket启动成功，监听8080端口。。。");
        server.await();
    }


}
