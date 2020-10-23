package tk.young1lin.rpc.zookeeper.service.impl;

import tk.young1lin.rpc.zookeeper.service.EventTypeDoService;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/29 4:53 下午
 */
public class NoneEventTypeDoService implements EventTypeDoService {
    @Override
    public void doSomeThing() {
        System.out.println("什么事也没发生=========");
    }
}
