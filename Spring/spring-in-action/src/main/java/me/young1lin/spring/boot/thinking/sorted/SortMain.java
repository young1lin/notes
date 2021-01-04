package me.young1lin.spring.boot.thinking.sorted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 上午8:39
 * @version 1.0
 */
public class SortMain {

	public static void main(String[] args) {
		// Annotation
		List<PrintRunner> annotationList = Arrays.asList(new AnnotationSortClass3()
				, new AnnotationSortClass1(), new AnnotationSortClass2());
		AnnotationAwareOrderComparator.sort(annotationList);
		annotationList.forEach(PrintRunner::print);

		// Interface
		List<PrintRunner> interfaceList = Arrays.asList(new InterfaceSortClass6()
				, new InterfaceSortClass4(), new InterfaceSortClass5());
		AnnotationAwareOrderComparator.sort(interfaceList);
		interfaceList.forEach(PrintRunner::print);

		// Mixed
		List<PrintRunner> mixedList = Arrays.asList(new InterfaceSortClass6()
				, new InterfaceSortClass4(), new InterfaceSortClass5()
				, new AnnotationSortClass3(), new AnnotationSortClass1()
				, new AnnotationSortClass2());
		System.out.println("before sorted==========");
		mixedList.forEach(PrintRunner::print);
		AnnotationAwareOrderComparator.sort(mixedList);
		System.out.println("after sorted===========");
		mixedList.forEach(PrintRunner::print);
	}

}
