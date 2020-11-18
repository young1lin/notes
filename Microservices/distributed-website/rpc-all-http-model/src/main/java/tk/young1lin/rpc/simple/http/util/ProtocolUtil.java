package tk.young1lin.rpc.simple.http.util;

import tk.young1lin.rpc.simple.http.Encode;
import tk.young1lin.rpc.simple.http.model.Request;
import tk.young1lin.rpc.simple.http.model.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 11:39 上午
 */
public class ProtocolUtil {
    private ProtocolUtil() {
    }

    public static void writeRequest(OutputStream output, Request request) throws IOException {
        output.write(request.getEncode());
        output.write(ByteUtil.int2ByteArray(request.getCommandLength()));
        if (Encode.GBK.getValue() == request.getEncode()) {
            output.write(request.getCommand().getBytes("GBK"));
        } else {
            output.write(request.getCommand().getBytes(StandardCharsets.UTF_8));
        }
        output.flush();
    }

    /**
     * 这种硬编码的方式，应该不是这么用的，应该要对位填充的
     *
     * @param input inputStream
     * @return
     * @throws IOException
     */
    public static Request readRequest(InputStream input) throws IOException {
        // 读取编码
        byte[] encodeByte = new byte[1];
        input.read(encodeByte);
        byte encode = encodeByte[0];

        //读取命令长度
        byte[] commandLengthBytes = new byte[4];
        // 这里不能用 available ,用 available 读取最后的数据的时候要用，前面要对齐填充内容。
        //int availableInt = input.available();
        //byte[] commandLengthBytes = new byte[availableInt];
        input.read(commandLengthBytes);
        int commandLength = ByteUtil.bytes2Int(commandLengthBytes);

        // 读取命令
        byte[] commandBytes = new byte[commandLength];
        // 我这里是因为设置 command 的时候没设置进去，出错的时候，写的排除错误的代码。如果可读的字节数是 0， read 任何大于 0 的值都会导致程序无响应
        // byte[] commandBytes = new byte[input.available()];
        input.read(commandBytes);
        String command = "";
        if (Encode.GBK.getValue() == encode) {
            command = new String(commandBytes, "GBK");
        } else {
            command = new String(commandBytes, StandardCharsets.UTF_8);
        }
        return new Request.RequestBuilder()
                .command(command)
                .encode(encode)
                .commandLength(commandLength)
                .build();
    }

    public static void writeResponse(OutputStream output, Response response) throws IOException {
        // 将 response 响应返回给客户端
        output.write(response.getEncode());
        output.write(ByteUtil.int2ByteArray(response.getResponseLength()));
        if (Encode.GBK.getValue() == response.getEncode()) {
            output.write(response.getResponse().getBytes("GBK"));
        } else {
            output.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
        }
        output.flush();
    }

    public static Response readResponse(InputStream input) throws IOException {
        // 读取编码
        byte[] encodeByte = new byte[1];
        input.read(encodeByte);
        byte encode = encodeByte[0];
        //读取命令长度
        //读取 hello!
        byte[] responseLengthBytes = new byte[4];
        input.read(responseLengthBytes);
        int responseLength = ByteUtil.bytes2Int(responseLengthBytes);
        //读取命令
        byte[] commandBytes = new byte[responseLength];
        input.read(commandBytes);
        String response;
        if (Encode.GBK.getValue() == encode) {
            response = new String(commandBytes, "GBK");
        } else {
            response = new String(commandBytes, StandardCharsets.UTF_8);
        }
        return new Response.ResponseBuilder()
                .encode(encode)
                .responseLength(responseLength)
                .response(response)
                .build();
    }
}
