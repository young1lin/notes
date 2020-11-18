package tk.young1lin.rpc.http.provider;

import tk.young1lin.rpc.http.provider.impl.SayHelloServiceImpl;
import tk.young1lin.rpc.service.SayHelloService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 10:26 上午
 */
public class SocketProvider {

    public static HashMap<String,Object> services = new HashMap<>(1<<4);

    static {
        services.put(SayHelloService.class.getName(),new SayHelloServiceImpl());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = new ServerSocket(1234);
        while (true){
            Socket socket = serverSocket.accept();
            //读取服务信息
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            String interfaceName = input.readUTF();
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
            Object[] arguments = (Object[]) input.readObject();
            //执行调用
            Class<?> serviceInterfaceClass = Class.forName(interfaceName);
            Object service = services.get(interfaceName);
            Method method = serviceInterfaceClass.getMethod(methodName,parameterTypes);
            Object result = method.invoke(service,arguments);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(result);
            output.flush();
        }
    }
}
