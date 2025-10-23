package net.moussa.moussa.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.moussa.moussa.model.Produit;
import net.moussa.moussa.service.PanierService;
import net.moussa.moussa.service.ProduitService;

import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class VitrineBean {
    
    @Inject
    private ProduitService produitService;
    
    @Inject
    private PanierService panierService;
    
    @Inject
    private InternauteBean internauteBean;
    
    @Inject
    private PanierBean panierBean;
    
    private List<Produit> produits;
    private int quantiteAAjouter = 1;
    
    public void loadProduits() {
        if (produits == null) {
            produits = produitService.findDisponibles();
        }
    }
    
    public List<Produit> getProduits() {
        loadProduits();
        return produits;
    }
    
    public String ajouterAuPanier(Long produitId) {
        if (internauteBean == null || !internauteBean.isLoggedIn()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Connexion requise", 
                "Veuillez vous connecter pour ajouter des produits au panier");
            return null;
        }
        
        Long panierId = internauteBean.getPanierId();
        if (panierId == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Panier introuvable");
            return null;
        }
        
        try {
            panierService.ajouterProduit(panierId, produitId, quantiteAAjouter);
            panierBean.refresh(); // تحديث السلة بعد الإضافة
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                "Produit ajouté au panier");
            return "/panier.xhtml?faces-redirect=true"; // توجيه لصفحة السلة
        } catch (IllegalStateException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de l'ajout au panier");
            return null;
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}

