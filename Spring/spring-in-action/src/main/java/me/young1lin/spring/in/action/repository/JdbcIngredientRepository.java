package me.young1lin.spring.in.action.repository;

import me.young1lin.spring.in.action.domain.Ingredient;
import me.young1lin.spring.in.action.domain.Order;
import me.young1lin.spring.in.action.domain.Taco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/4 3:48 下午
 */
@Repository
public class JdbcIngredientRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ingredient> findAll() {
        return jdbcTemplate.query("SELECT id,name,type FROM Ingredient", this::mapRowToIngredient);
    }


    public Ingredient findOne(String id) {
        return jdbcTemplate.queryForObject("SELECT id,name,type FROM Ingredient WHERE id=?", this::mapRowToIngredient, id);
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(
                rs.getString("id"),
                rs.getString("name"),
                Ingredient.Type.value(rs.getString("type")));
    }

    public Ingredient save(Ingredient ingredient) {
        jdbcTemplate.update("INSERT INTO Ingredient (id,name,type) VALUES(?,?,?)", ingredient.getId()
                , ingredient.getName(), ingredient.getType().toString());
        return ingredient;
    }

    public Order save(Order order) {
        return null;
    }

    public Taco save(Taco taco) {
        long tacoId = saveTacoInfo(taco);
        taco.setId(tacoId);
       /* for(Ingredient ingredient : taco.getIngredients()){
            saveIngredientToTaco(ingredient,tacoId);
        }*/
        return taco;
    }

    private long saveTacoInfo(Taco taco) {
        taco.setCreateAt(new Date());
        PreparedStatementCreator psc = new PreparedStatementCreatorFactory("INSERT INTO Taco(name,createAt)values(?,?)"
                , Types.VARCHAR, Types.TIMESTAMP).newPreparedStatementCreator(Arrays.asList(taco.getName()
                , new Timestamp(taco.getCreateAt().getTime())));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc,keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
        jdbcTemplate.update("INSERT INTO Taco_Ingredients (taco,ingredient) values(?,?)",tacoId,ingredient.getId());
    }


}
