package me.young1lin.spring.in.action.repository;

import me.young1lin.spring.in.action.domain.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 9:49 下午
 */
@Repository
public interface IngredientRepository extends CrudRepository<Ingredient,String> {
}
