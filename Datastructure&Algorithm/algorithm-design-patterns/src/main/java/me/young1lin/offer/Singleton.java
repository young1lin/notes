package me.young1lin.offer;

/**
 * 第一题：单例模式
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 上午12:42
 * @version 1.0
 */
public class Singleton {

	private static Singleton INSTANCE;


	private Singleton() {
		if (INSTANCE != null) {
			throw new IllegalArgumentException();
		}
	}

	public static Singleton getInstance() {
		if (INSTANCE == null) {
			synchronized (Singleton.class) {
				if (INSTANCE == null) {
					INSTANCE = new Singleton();
				}
			}
		}
		return INSTANCE;
	}

}

