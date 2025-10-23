package net.moussa.moussa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "internaute")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Internaute implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "internaute_roles",
        joinColumns = @JoinColumn(name = "internaute_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
    
    @OneToOne(mappedBy = "internaute", cascade = CascadeType.ALL, orphanRemoval = true)
    private Panier panier;
    
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}

