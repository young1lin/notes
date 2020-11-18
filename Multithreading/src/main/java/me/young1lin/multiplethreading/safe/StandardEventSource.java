package me.young1lin.multiplethreading.safe;

import java.util.HashSet;

/**
 * @author <a href="mailto:young1lin0108@gmail.com"></a>young1lin
 * @version 1.0
 * @since 2020/11/16 11:04 下午
 */
public class StandardEventSource implements EventSource {

    private HashSet<EventListener> eventListeners = new HashSet<>(16);

    @Override
    public void registerListener(EventListener listener) {
        eventListeners.add(listener);
    }
}
