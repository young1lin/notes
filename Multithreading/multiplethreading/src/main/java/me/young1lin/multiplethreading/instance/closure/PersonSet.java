package me.young1lin.multiplethreading.instance.closure;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/11/19 9:48 下午
 */
public class PersonSet {

    private final Set<String> mySet = new HashSet<>();

    public synchronized void addString(String s){
        mySet.add(s);
    }

    public synchronized boolean containsString(String s){
        return mySet.contains(s);
    }

}
