package me.young1in.samples.autoconfigure.formatter;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/21 8:04 上午
 * @version 1.0
 */
public class DefaultFormatter implements Formatter {

	@Override
	public String format(Object obj) {
		return String.valueOf(obj);
	}

}
