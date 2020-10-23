package tk.young1lin.rcp.consumer;

import tk.young1lin.rpc.simple.http.Encode;
import tk.young1lin.rpc.simple.http.model.Request;
import tk.young1lin.rpc.simple.http.model.Response;
import tk.young1lin.rpc.simple.http.util.ProtocolUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 12:39 下午
 */
public class SimpleHttpConsumer {
    public static void main(String[] args) throws IOException {
        String command = "HELLO";
        command = "HELLO77777";
        // 创建请求
        Request request = new Request.RequestBuilder()
                .command(command)
                .commandLength(command.length())
                .encode(Encode.UTF8.getValue())
                .build();
        Socket client = new Socket("127.0.0.1", 4567);
        OutputStream output = client.getOutputStream();
        // 发送请求
        ProtocolUtil.writeRequest(output, request);
        //读取响应请求
        InputStream input = client.getInputStream();
        Response response = ProtocolUtil.readResponse(input);
        System.out.println(response);
    }
}
