package me.young1lin.multiplethreading.safe;

/**
 * @author <a href="mailto:young1lin0108@gmail.com"></a>young1lin
 * @version 1.0
 * @since 2020/11/16 11:01 下午
 */
public class SafeListener{

    private final EventListener listener;

    private SafeListener(){
        listener = new EventListener(){

            @Override
            public void onEvent(Event e){
                doSomething(e);
            }
        };
    }

    public static SafeListener newInstance(EventSource source){
        SafeListener safe = new SafeListener();
        source.registerListener(safe.listener);
        return safe;
    }

    private void doSomething(Event e){
        System.out.println(e);
    }
}