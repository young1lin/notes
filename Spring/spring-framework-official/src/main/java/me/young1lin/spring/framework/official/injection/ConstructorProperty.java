package me.young1lin.spring.framework.official.injection;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/7 上午12:20
 * @version 1.0
 */
public class ConstructorProperty {

	private int years;

	private String ultimateAnswer;


	public int getYears() {
		return years;
	}

	public void setYears(int years) {
		this.years = years;
	}

	public String getUltimateAnswer() {
		return ultimateAnswer;
	}

	public void setUltimateAnswer(String ultimateAnswer) {
		this.ultimateAnswer = ultimateAnswer;
	}

	@Override
	public String toString() {
		return "ConstructorProperty{" +
				"years=" + years +
				", ultimateAnswer='" + ultimateAnswer + '\'' +
				'}';
	}

}
