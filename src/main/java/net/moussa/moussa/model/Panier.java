package net.moussa.moussa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "panier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Panier implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "internaute_id")
    private Internaute internaute;
    
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();
    
    @Transient
    public BigDecimal getTotal() {
        return lignes.stream()
            .map(LignePanier::getSousTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Transient
    public int getNombreArticles() {
        return lignes.stream()
            .mapToInt(LignePanier::getQuantite)
            .sum();
    }
    
    public void addLigne(LignePanier ligne) {
        lignes.add(ligne);
        ligne.setPanier(this);
    }
    
    public void removeLigne(LignePanier ligne) {
        lignes.remove(ligne);
        ligne.setPanier(null);
    }
}

