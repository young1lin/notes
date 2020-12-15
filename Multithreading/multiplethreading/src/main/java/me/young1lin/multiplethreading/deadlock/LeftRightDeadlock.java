package me.young1lin.multiplethreading.deadlock;


/**
 * Deadlock
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/15 11:33 下午
 */
@Deadlock
public class LeftRightDeadlock {

    private final Object left = new Object();

    private final Object right = new Object();


    public void leftRight(){
        synchronized (this.left){
            synchronized (this.right){
                doSomething();
            }
        }
    }

    private void doSomething() {
        System.out.println("11111");
    }

    private void rightLeft(){
        synchronized (this.right){
            synchronized (this.left){
                doSomethingElse();
            }
        }
    }

    private void doSomethingElse() {
        System.out.println("2222");
    }

}
