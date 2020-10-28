package me.young1lin.spring.in.action.repository;

import me.young1lin.spring.in.action.domain.Taco;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/7 10:19 上午
 */
@Repository
public interface TacoRepository extends CrudRepository<Taco,Long> {
}
