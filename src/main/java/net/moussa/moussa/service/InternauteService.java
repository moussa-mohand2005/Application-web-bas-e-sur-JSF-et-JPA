package net.moussa.moussa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.moussa.moussa.model.Internaute;
import net.moussa.moussa.model.Panier;
import net.moussa.moussa.model.Role;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InternauteService {
    
    @PersistenceContext(unitName = "mycnx")
    private EntityManager em;
    
    @Transactional
    public void register(Internaute internaute, Role role) {
        // كلمة المرور تُحفظ كما هي (بدون تشفير معقد)
        
        // Ajout du rôle
        internaute.addRole(role);
        
        // Création du panier
        Panier panier = new Panier();
        panier.setInternaute(internaute);
        internaute.setPanier(panier);
        
        em.persist(internaute);
    }
    
    public Optional<Internaute> findByEmail(String email) {
        try {
            Internaute internaute = em.createQuery(
                "SELECT i FROM Internaute i WHERE i.email = :email", Internaute.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(internaute);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public Optional<Internaute> findById(Long id) {
        try {
            Internaute internaute = em.createQuery(
                "SELECT i FROM Internaute i " +
                "LEFT JOIN FETCH i.panier " +
                "WHERE i.id = :id", Internaute.class)
                .setParameter("id", id)
                .getSingleResult();
            return Optional.of(internaute);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public List<Internaute> findAll() {
        return em.createQuery("SELECT i FROM Internaute i ORDER BY i.nom", Internaute.class)
                .getResultList();
    }
    
    @Transactional
    public void update(Internaute internaute) {
        em.merge(internaute);
    }
    
    @Transactional
    public void delete(Long id) {
        Internaute internaute = em.find(Internaute.class, id);
        if (internaute != null) {
            em.remove(internaute);
        }
    }
    
    public boolean emailExists(String email) {
        Long count = em.createQuery(
            "SELECT COUNT(i) FROM Internaute i WHERE i.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return count > 0;
    }
}

