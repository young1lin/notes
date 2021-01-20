package me.young1lin.spring.alibab;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/20 下午10:31
 * @version 1.0
 */
public class Interview {

	public static void main(String[] args) {
		Thread aThread = new InterviewThread(null,"A","A");
		Thread bThread = new InterviewThread(aThread,"B","B");
		Thread cThread = new InterviewThread(bThread,"C","C");
		cThread.start();
		bThread.start();
		aThread.start();
	}

}

class InterviewThread extends Thread {

	private final Thread beforeRunThread;

	private final String msg;

	
	InterviewThread(Thread beforeRunThread, String msg,String threadName) {
		super(threadName);
		this.beforeRunThread = beforeRunThread;
		this.msg = msg;
	}

	@Override
	public void run() {
		if(beforeRunThread != null){
			try {
				beforeRunThread.join();
				System.out.println(msg);
			}
			catch (InterruptedException e) {
				// 理论上来说，这里应该个
				e.printStackTrace();
			}
		}else {
			System.out.println(msg);
		}
	}

}
