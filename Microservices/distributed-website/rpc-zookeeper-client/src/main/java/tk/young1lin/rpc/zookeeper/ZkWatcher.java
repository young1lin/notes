package tk.young1lin.rpc.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import tk.young1lin.rpc.zookeeper.service.EventTypeDoService;

import java.util.Map;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/29 4:28 下午
 */
public class ZkWatcher implements Watcher {

    private final Map<Event.EventType, EventTypeDoService> eventTypeToDoMap;

    public ZkWatcher(Map<Event.EventType, EventTypeDoService> eventTypeToDoMap) {
        this.eventTypeToDoMap = eventTypeToDoMap;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        eventTypeToDoMap.get(watchedEvent.getType()).doSomeThing();
    }
}
