package net.moussa.moussa.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.moussa.moussa.model.Produit;
import net.moussa.moussa.service.ProduitService;

import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class ProduitBean {
    
    @Inject
    private ProduitService produitService;
    
    @Inject
    private InternauteBean internauteBean;
    
    private List<Produit> produits;
    private Produit selectedProduit;
    private Produit newProduit = new Produit();
    
    public void loadProduits() {
        if (produits == null) {
            produits = produitService.findAll();
        }
    }
    
    public List<Produit> getProduits() {
        loadProduits();
        return produits;
    }
    
    @RolesAllowed({"ADMIN", "VENDEUR"})
    public void saveProduit() {
        try {
            produitService.save(newProduit);
            loadProduits();
            newProduit = new Produit(); // Reset
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                "Produit enregistré avec succès");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }
    
    @RolesAllowed({"ADMIN", "VENDEUR"})
    public void updateProduit() {
        try {
            if (selectedProduit != null) {
                produitService.save(selectedProduit);
                loadProduits();
                addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Produit mis à jour avec succès");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la mise à jour: " + e.getMessage());
        }
    }
    
    @RolesAllowed({"ADMIN", "VENDEUR"})
    public void deleteProduit(Long id) {
        try {
            produitService.delete(id);
            loadProduits();
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                "Produit supprimé avec succès");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la suppression: " + e.getMessage());
        }
    }
    
    public boolean canManageProduits() {
        return internauteBean.isAdmin() || internauteBean.isVendeur();
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}

