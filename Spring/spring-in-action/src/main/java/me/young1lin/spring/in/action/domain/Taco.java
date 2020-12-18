package me.young1lin.spring.in.action.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 7:53 下午
 */
@Entity
public class Taco {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createAt;

    @NotNull
    @Size(min=5,message = "Name must be at least 5 characters long")
    private String name;
    //@ManyToMany(targetEntity = Ingredient.class)
    //@Size(min = 1,message = "You must choose at least 1 ingredient")
    //private List<Ingredient> ingredients;

    @PrePersist
    void createdAt(){
        this.createAt = new Date();
    }

    public Taco() {
    }

    public Taco(Long id, Date createAt, @NotNull @Size(min = 5, message = "Name must be at least 5 characters long") String name) {
        this.id = id;
        this.createAt = createAt;
        this.name = name;
    }

    public Taco(Date createAt, @NotNull @Size(min = 5, message = "Name must be at least 5 characters long") String name) {
        this.createAt = createAt;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
