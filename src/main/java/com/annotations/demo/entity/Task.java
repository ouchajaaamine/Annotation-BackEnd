package com.annotations.demo.entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"couples", "annotateur"})
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date dateLimite;
    
    @ManyToOne
    @JoinColumn(name = "annotateur_id")
    @JsonIgnoreProperties({"taches", "annotations", "password"})
    private Annotateur annotateur;

    //hibernete va cree ici un table association qui lie entre les taches et les couples de textes

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "tache_couple",
        joinColumns = @JoinColumn(name = "tache_id"),
        inverseJoinColumns = @JoinColumn(name = "couple_id")
    )
    @JsonIgnoreProperties("taches")
    private List<CoupleText> couples = new ArrayList<>();
    

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

}
