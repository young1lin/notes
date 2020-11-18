package tk.young1lin.rpc.http.provider.impl;

import tk.young1lin.rpc.service.SayHelloService;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 8:02 上午
 */
public class SayHelloServiceImpl implements SayHelloService {

    private static final String HELLO_STR = "hello";

    @Override
    public String sayHello(String helloArg) {
        if (HELLO_STR.equals(helloArg)) {
            return "how are u";
        }
        return "bye bye";
    }
}
