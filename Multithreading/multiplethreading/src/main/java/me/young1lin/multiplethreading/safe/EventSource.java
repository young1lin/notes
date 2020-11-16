package me.young1lin.multiplethreading.safe;

/**
 * @author <a href="mailto:young1lin0108@gmail.com"></a>young1lin
 * @version 1.0
 * @date 2020/11/16 11:03 下午
 */
public interface EventSource {

    void registerListener(EventListener listener);
}
