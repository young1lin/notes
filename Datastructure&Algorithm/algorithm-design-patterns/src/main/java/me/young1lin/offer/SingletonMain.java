package me.young1lin.offer;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 上午8:40
 * @version 1.0
 */
public class SingletonMain {

	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			Thread t1 = new Thread(() -> System.out.println(Singleton.getInstance()));
			t1.start();
		}
	}

}
