package tk.young1lin.rpc.http.provider;

import tk.young1lin.rpc.simple.http.Encode;
import tk.young1lin.rpc.simple.http.model.Request;
import tk.young1lin.rpc.simple.http.model.Response;
import tk.young1lin.rpc.simple.http.util.ProtocolUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 3:20 下午
 */
public class SimpleHttpProvider {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(4567);

        while (true) {
            Socket client = server.accept();
            InputStream input = client.getInputStream();
            Request request = ProtocolUtil.readRequest(input);

            OutputStream output = client.getOutputStream();
            String responseContent;
            if ("HELLO".equals(request.getCommand())) {
                responseContent = "hello!";
            } else {
                responseContent = "bye bye! this is my last word";
            }
            Response response = new Response.ResponseBuilder()
                    .encode(Encode.UTF8.getValue())
                    .responseLength(responseContent.length())
                    .response(responseContent)
                    .build();
            ProtocolUtil.writeResponse(output, response);
        }
    }
}
