package me.young1lin.spring.batch.mapper;

import java.util.List;

import me.young1lin.spring.batch.entity.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2021/8/7 上午9:29
 * @version 1.0
 */
@Mapper
public interface PersonMapper {

	/**
	 * 列出所有的 Person
	 *
	 * @return 所有的 person
	 */
	@Select("SELECT * FROM person")
	List<Person> listAll();

}
