package net.moussa.moussa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.moussa.moussa.model.Produit;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProduitService {
    
    @PersistenceContext(unitName = "mycnx")
    private EntityManager em;
    
    @Transactional
    public void save(Produit produit) {
        if (produit.getId() == null) {
            em.persist(produit);
        } else {
            em.merge(produit);
        }
    }
    
    public List<Produit> findAll() {
        return em.createQuery("SELECT p FROM Produit p ORDER BY p.libelle", Produit.class)
                .getResultList();
    }
    
    public List<Produit> findDisponibles() {
        return em.createQuery("SELECT p FROM Produit p WHERE p.stock > 0 ORDER BY p.libelle", Produit.class)
                .getResultList();
    }
    
    public Optional<Produit> findById(Long id) {
        Produit produit = em.find(Produit.class, id);
        return Optional.ofNullable(produit);
    }
    
    @Transactional
    public void delete(Long id) {
        try {
            // First, delete all ligne_panier that reference this product
            em.createQuery("DELETE FROM LignePanier lp WHERE lp.produit.id = :produitId")
                .setParameter("produitId", id)
                .executeUpdate();
            
            // Now delete the product
            Produit produit = em.find(Produit.class, id);
            if (produit != null) {
                em.remove(produit);
            }
        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
            throw new RuntimeException("Impossible de supprimer le produit. Il est peut-être utilisé dans des commandes.", e);
        }
    }
    
    @Transactional
    public void updateStock(Long produitId, int nouvelleQuantite) {
        Produit produit = em.find(Produit.class, produitId);
        if (produit != null) {
            produit.setStock(nouvelleQuantite);
            em.merge(produit);
        }
    }
    
    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Produit p", Long.class)
                .getSingleResult();
    }
}

