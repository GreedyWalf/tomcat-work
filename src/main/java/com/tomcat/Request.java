package com.tomcat;

import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义请求类
 */
public class Request {

    private InputStream inputStream;

    private String uri;

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getUri() {
        return uri;
    }

    public void parse() {
        try {
            byte[] buffer = new byte[2048];
            int len = (inputStream.read(buffer));
            String request = new String(buffer, 0, len);
            System.out.println("获取请求内容：request=" + request);
            uri = parseUri(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取到请求头信息中uri
     */
    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }

        return null;
    }
}
