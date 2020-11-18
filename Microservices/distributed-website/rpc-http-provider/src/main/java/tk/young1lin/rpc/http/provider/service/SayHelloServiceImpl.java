package tk.young1lin.rpc.http.provider.service;

import org.springframework.stereotype.Service;
import tk.young1lin.rpc.http.service.BaseService;


/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 4:33 下午
 */
@Service("tk.young1lin.rpc.http.provider.service.sayHelloServiceImpl")
public class SayHelloServiceImpl implements BaseService {

    private static final String HELLO_STR = "hello";

    @Override
    public Object execute(String arg1) {
        // 魔法值提示，硬编码一下
        if (HELLO_STR.equals(arg1)) {
            return "hello";
        }
        return "bye bye!";
    }
}
