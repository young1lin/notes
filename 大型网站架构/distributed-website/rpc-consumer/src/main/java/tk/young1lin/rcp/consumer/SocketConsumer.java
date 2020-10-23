package tk.young1lin.rcp.consumer;

import tk.young1lin.rpc.service.SayHelloService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 10:14 上午
 */
public class SocketConsumer {
    public static void main(String[] args) throws NoSuchMethodException, IOException, ClassNotFoundException {
        // 接口名称
        String interfaceName = SayHelloService.class.getName();
        // 需要远程执行的方法
        Method method = SayHelloService.class.getMethod("sayHello",java.lang.String.class);
        // 需要传递的参数
        Object[] arguments = {"hello"};
        Socket socket = new Socket("127.0.0.1",1234);
        // 将方法名称和参数传递到远端
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeUTF(interfaceName);
        output.writeUTF(method.getName());
        output.writeObject(method.getParameterTypes());
        output.writeObject(arguments);

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        // 从远端读取方法执行结果
        Object result = input.readObject();
        System.out.println(result);
        socket.close();
    }
}
