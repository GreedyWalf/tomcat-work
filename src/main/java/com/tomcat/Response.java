package com.tomcat;

import java.io.*;

/**
 * 自定义响应类
 */
public class Response {

    private static final int BUFFER_SIZE = 1024 * 1024;
    private Request request;
    private OutputStream outputStream;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    /**
     * 解析webapp目录下的静态资源，响应到客户端
     */
    public void sendStaticResource() {
        FileInputStream fis = null;

        //获取项目路径下webRoot目录静态资源
        File file = new File(HttpServer.WBE_ROOT, request.getUri());
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                String responseHeader = "HTTP/1.1 200\r\n";
                if (file.getName().endsWith(".html")) {
                    responseHeader += "Content-Type: text/html\r\n";
                } else if (file.getName().endsWith(".gif")) {
                    responseHeader += "Content-Type: image/jpeg\r\n";
                }

                byte[] buffer = new byte[BUFFER_SIZE];
                int len = fis.read(buffer);
                responseHeader += "Content-Length: " + len + "\r\n\r\n";

                byte[] responseHeaderBytes = responseHeader.getBytes();
                byte[] responseBytes = new byte[responseHeaderBytes.length + len];
                System.arraycopy(responseHeaderBytes, 0, responseBytes, 0, responseHeaderBytes.length);
                System.arraycopy(buffer, 0, responseBytes, responseHeaderBytes.length, len);
                outputStream.write(responseBytes);
                outputStream.flush();
            } else {
                //文件未找到 404
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "Content-Length: 23\r\n" + "\r\n"
                        + "<h1>File Not Found</h1>";
                outputStream.write(errorMessage.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("解析静态资源文件出错。。");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
