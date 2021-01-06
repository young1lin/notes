package me.young1lin.java8.base.date;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/6 下午3:23
 * @version 1.0
 */
public class DateMain {

	public static void main(String[] args) {
		LocalDate date1 = LocalDate.of(2020, 10, 25);
		int year1 = date1.getYear();
		Month month1 = date1.getMonth();
		int day1 = date1.getDayOfMonth();
		DayOfWeek dayOfWeek1 = date1.getDayOfWeek();
		int len1 = date1.lengthOfMonth();
		boolean leap1 = date1.isLeapYear();
		System.out.printf("year : %s, month : %s, day : %s, dayOfWeek : %s, len : %s, leap : %s\n"
				, year1, month1.getValue(), day1, dayOfWeek1.getValue(), len1, leap1);

		// LocalTime 和上面类似
		// LocalDateTime 是两个的整合

		LocalDate today = LocalDate.now();
		System.out.println(today);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
		System.out.println(df.format(now));

		// Duration 仅仅支持 LocalTime 以及 LocalDateTime
		LocalTime localTimeNow = LocalTime.now();
		LocalTime morning = LocalTime.of(7, 2);
		Duration d = Duration.between(morning, localTimeNow);
		boolean negative = d.isNegative();
		boolean zero = d.isZero();
		Long secs = d.getSeconds();
		System.out.printf("negative : %s, zero : %s, secs : %s\n", negative, zero, secs);

		// Period 支持 LocalDate
		Period period = Period.between(date1, today);
		System.out.printf("years : %s, days : %s, months : %s",
				period.getYears(), period.getDays(), period.getMonths());

		// 只有 4 种日历，民国日历，日本日历，泰国日历，伊斯兰日历。

	}

}
