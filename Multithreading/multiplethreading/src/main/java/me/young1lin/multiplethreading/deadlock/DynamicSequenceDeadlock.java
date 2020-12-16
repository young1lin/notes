package me.young1lin.multiplethreading.deadlock;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/15 11:36 下午
 */
@Deadlock
public class DynamicSequenceDeadlock {

	public void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount) {

		synchronized (fromAccount) {
			synchronized (toAccount) {
				if (fromAccount.getBalance().compareTo(amount) < 0) {
					throw new RuntimeException("balance not allow");
				}
				else {
					fromAccount.debit(amount);
					toAccount.credit(amount);
				}
			}
		}
	}

}

class Account {

	private DollarAmount balance;


	public DollarAmount getBalance() {
		return balance;
	}

	public void setBalance(DollarAmount balance) {
		this.balance = balance;
	}

	public void debit(DollarAmount amount) {
	}

	public void credit(DollarAmount amount) {
	}

}

class DollarAmount implements Comparable<DollarAmount> {


	public DollarAmount(){}

	public DollarAmount(int balance){}

	@Override
	public int compareTo(DollarAmount o) {
		return 0;
	}

}