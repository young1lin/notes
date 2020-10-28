package me.young1lin.spring.in.action.domain;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 8:27 下午
 */
@Data
@Table(name = "Taco_Order")
@Entity
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date placedAt;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "street is required")
    private String street;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "zip is required")
    private String zip;

    @CreditCardNumber(message = "Not a valid credit card number")
    @NotBlank(message = "ccNumber is required")
    private String ccNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2]([\\/])([1-9][0-9]))$",message = "Must be formatted MM/YY")
    private String ccExpiration;

    @Digits(integer = 3,fraction = 0,message = "InvalidCVV")
    private String ccCVV;
    @ManyToMany(targetEntity = Taco.class)
    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco taco){
        tacos.add(taco);
    }

    public List<Taco> getTacos(){
        return tacos;
    }

    @PrePersist
    void placedAt(){
        this.placedAt = new Date();
    }
}
