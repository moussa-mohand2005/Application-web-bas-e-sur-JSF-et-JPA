package net.moussa.moussa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.moussa.moussa.model.LignePanier;
import net.moussa.moussa.model.Panier;
import net.moussa.moussa.model.Produit;

import java.util.Optional;

@ApplicationScoped
public class PanierService {
    
    @PersistenceContext(unitName = "mycnx")
    private EntityManager em;
    
    public Optional<Panier> findById(Long id) {
        try {
            // First query: fetch Panier with lignes
            Panier panier = em.createQuery(
                "SELECT DISTINCT p FROM Panier p " +
                "LEFT JOIN FETCH p.lignes " +
                "WHERE p.id = :id", Panier.class)
                .setParameter("id", id)
                .getSingleResult();
            
            // Second query: fetch produits for lignes
            if (panier != null && !panier.getLignes().isEmpty()) {
                em.createQuery(
                    "SELECT DISTINCT lp FROM LignePanier lp " +
                    "LEFT JOIN FETCH lp.produit " +
                    "WHERE lp.panier.id = :panierId", LignePanier.class)
                    .setParameter("panierId", id)
                    .getResultList();
            }
            
            return Optional.ofNullable(panier);
        } catch (Exception e) {
            System.err.println("Error in findById: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    @Transactional
    public void ajouterProduit(Long panierId, Long produitId, int quantite) {
        Panier panier = em.find(Panier.class, panierId);
        Produit produit = em.find(Produit.class, produitId);
        
        if (panier == null || produit == null) {
            throw new IllegalArgumentException("Panier ou produit introuvable");
        }
        
        if (produit.getStock() < quantite) {
            throw new IllegalStateException("Stock insuffisant");
        }
        
        // Vérifier si le produit existe déjà dans le panier
        Optional<LignePanier> ligneExistante = panier.getLignes().stream()
            .filter(l -> l.getProduit().getId().equals(produitId))
            .findFirst();
        
        if (ligneExistante.isPresent()) {
            // Mettre à jour la quantité
            LignePanier ligne = ligneExistante.get();
            int nouvelleQuantite = ligne.getQuantite() + quantite;
            
            if (produit.getStock() < nouvelleQuantite) {
                throw new IllegalStateException("Stock insuffisant");
            }
            
            ligne.setQuantite(nouvelleQuantite);
        } else {
            // Créer une nouvelle ligne
            LignePanier nouvelleLigne = new LignePanier();
            nouvelleLigne.setPanier(panier);
            nouvelleLigne.setProduit(produit);
            nouvelleLigne.setQuantite(quantite);
            nouvelleLigne.setPrixUnitaire(produit.getPrix());
            panier.addLigne(nouvelleLigne);
        }
        
        em.merge(panier);
    }
    
    @Transactional
    public void modifierQuantite(Long ligneId, int nouvelleQuantite) {
        LignePanier ligne = em.find(LignePanier.class, ligneId);
        
        if (ligne == null) {
            throw new IllegalArgumentException("Ligne introuvable");
        }
        
        if (nouvelleQuantite <= 0) {
            // Supprimer la ligne
            ligne.getPanier().removeLigne(ligne);
            em.remove(ligne);
        } else {
            // Vérifier le stock
            if (ligne.getProduit().getStock() < nouvelleQuantite) {
                throw new IllegalStateException("Stock insuffisant");
            }
            ligne.setQuantite(nouvelleQuantite);
            em.merge(ligne);
        }
    }
    
    @Transactional
    public void supprimerLigne(Long ligneId) {
        LignePanier ligne = em.find(LignePanier.class, ligneId);
        if (ligne != null) {
            ligne.getPanier().removeLigne(ligne);
            em.remove(ligne);
        }
    }
    
    @Transactional
    public void viderPanier(Long panierId) {
        Panier panier = em.find(Panier.class, panierId);
        if (panier != null) {
            panier.getLignes().clear();
            em.merge(panier);
        }
    }
    
    @Transactional
    public void validerCommande(Long panierId) {
        Panier panier = em.find(Panier.class, panierId);
        if (panier != null) {
            // Décrémenter le stock des produits
            for (LignePanier ligne : panier.getLignes()) {
                Produit produit = ligne.getProduit();
                int nouveauStock = produit.getStock() - ligne.getQuantite();
                if (nouveauStock < 0) {
                    throw new IllegalStateException("Stock insuffisant pour: " + produit.getLibelle());
                }
                produit.setStock(nouveauStock);
                em.merge(produit);
            }
            
            // Vider le panier après validation
            panier.getLignes().clear();
            em.merge(panier);
        }
    }
}

