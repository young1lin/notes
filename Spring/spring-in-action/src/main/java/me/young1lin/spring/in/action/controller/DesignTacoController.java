package me.young1lin.spring.in.action.controller;

import me.young1lin.spring.in.action.repository.JdbcIngredientRepository;
import me.young1lin.spring.in.action.domain.Ingredient;
import me.young1lin.spring.in.action.domain.Order;
import me.young1lin.spring.in.action.domain.Taco;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 7:40 下午
 */

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {

    private final JdbcIngredientRepository jdbcIngredientRepository;

    @Autowired
    public DesignTacoController(JdbcIngredientRepository jdbcIngredientRepository) {
        this.jdbcIngredientRepository = jdbcIngredientRepository;
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }


/*

    @Deprecated
    @GetMapping
    public String showDesignFormOld(Model model){
      List<Ingredient> ingredientList = Arrays.asList(
        new Ingredient("FLTO","Flour Tortilla", Ingredient.Type.WRAP),
        new Ingredient("COTO","Corn Tortilla", Ingredient.Type.WRAP),
        new Ingredient("GRBF","Ground Beef", Ingredient.Type.PROTEIN),
        new Ingredient("CARN","Carnitas", Ingredient.Type.PROTEIN),
        new Ingredient("TMTO","Diced Tomatoes", Ingredient.Type.VEGGIES),
        new Ingredient("LETC","Lettuce", Ingredient.Type.VEGGIES),
        new Ingredient("CHED","Cheddar", Ingredient.Type.CHEESE),
        new Ingredient("JACK","Monterrey Jack", Ingredient.Type.CHEESE),
        new Ingredient("SLSA","Salsa", Ingredient.Type.SAUCE),
        new Ingredient("SRCR","Sour Cream", Ingredient.Type.SAUCE)
      );
        Type[] types = Ingredient.Type.values();
        for(Type type : types){
            model.addAttribute(type.toString().toLowerCase(),filterByType(ingredientList,type));
            model.addAttribute("design",new Taco());
        }
        return "design";
    }
*/


    private List<Ingredient> filterByType(List<Ingredient> ingredients, Ingredient.Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

/*    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors) {
        if (errors.hasErrors()) {
            return "design";
        }
        log.info("Processing design : {}", design);
        return "redirect:/orders/current";
    }*/

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        jdbcIngredientRepository.findAll().forEach(i -> ingredients.add(i));
        Ingredient.Type[] types = Ingredient.Type.values();
        for (Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
            model.addAttribute("design", new Taco());
        }
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Taco design,@ModelAttribute Order order, Errors errors) {
        if (errors.hasErrors()) {
            return "design";
        }
        Taco saved = jdbcIngredientRepository.save(design);
        order.addDesign(saved);
        log.info("Processing design : {}", design);
        return "redirect:/orders/current";
    }
}
