package me.young1lin.multiplethreading.deadlock;

import java.util.Random;

/**
 * Spring core code style
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/16 11:43 下午
 * @version 1.0
 */
@Deadlock(false)
public class DemonstrateDeadLock {

	private static final Object TIE_LOCK = new Object();

	private static final int NUM_THREAD = 20;

	private static final int NUM_ACCOUNTS = 5;

	private static final int NUM_ITERATION = 1000000;


	public static void main(String[] args) {
		Random random = new Random();
		Account[] accounts = new Account[NUM_ACCOUNTS];
		for (int i = 0; i < accounts.length; i++) {
			accounts[i] = new Account();
		}
		class TransferThread extends Thread {
			@Override
			public void run() {
				for (int i = 0; i < NUM_ITERATION; i++) {
					int fromAcct = random.nextInt(NUM_ACCOUNTS);
					int toAcct = random.nextInt(NUM_ACCOUNTS);
					DollarAmount amount = new DollarAmount(random.nextInt(1000));
					transferMoney(accounts[fromAcct], accounts[toAcct], amount);
				}
			}
		}

		for (int i = 0; i < NUM_THREAD; i++) {
			new TransferThread().start();
		}
	}

	public static void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount) {

		class Helper {
			public void transfer() {
				if (fromAccount.getBalance().compareTo(amount) < 0) {
					throw new IllegalArgumentException("balance is not allow to transfer");
				}
				else {
					fromAccount.debit(amount);
					toAccount.credit(amount);
				}
			}
		}

		int fromHash = System.identityHashCode(fromAccount);
		int toHash = System.identityHashCode(toAccount);
		if (fromHash < toHash) {
			synchronized (fromAccount) {
				synchronized (toAccount) {
					new Helper().transfer();
				}
			}
		}
		else if (fromHash > toHash) {
			synchronized (toAccount) {
				synchronized (fromAccount) {
					new Helper().transfer();
				}
			}
		}
		else {
			synchronized (TIE_LOCK) {
				synchronized (fromAccount) {
					new Helper().transfer();
				}
			}
		}
	}

}
