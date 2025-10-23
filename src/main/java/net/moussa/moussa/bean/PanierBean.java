package net.moussa.moussa.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.moussa.moussa.model.LignePanier;
import net.moussa.moussa.service.PanierService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Setter
public class PanierBean implements Serializable {
    
    @Inject
    private PanierService panierService;
    
    @Inject
    private InternauteBean internauteBean;
    
    private List<LignePanier> lignes = new ArrayList<>();
    @Getter
    private BigDecimal total = BigDecimal.ZERO;
    @Getter
    private int nombreArticles = 0;
    @Getter
    private boolean initialized = false;
    
    public void refresh() {
        try {
            System.out.println("=== PanierBean.refresh() called ===");
            if (internauteBean != null && internauteBean.isLoggedIn()) {
                Long panierId = internauteBean.getPanierId();
                System.out.println("Panier ID: " + panierId);
                if (panierId != null && panierService != null) {
                    panierService.findById(panierId).ifPresent(panier -> {
                        System.out.println("Panier trouvé avec " + panier.getLignes().size() + " lignes");
                        lignes = new ArrayList<>(panier.getLignes());
                        total = panier.getTotal();
                        nombreArticles = panier.getNombreArticles();
                        System.out.println("Nombre articles: " + nombreArticles);
                    });
                    if (lignes.isEmpty()) {
                        System.out.println("ATTENTION: Panier trouvé mais aucune ligne!");
                    }
                    initialized = true;
                } else {
                    System.out.println("Panier ID null ou PanierService null");
                }
            } else {
                System.out.println("User non connecté");
                lignes.clear();
                total = BigDecimal.ZERO;
                nombreArticles = 0;
                initialized = true;
            }
        } catch (Exception e) {
            System.err.println("ERREUR dans PanierBean.refresh(): " + e.getMessage());
            e.printStackTrace();
            lignes.clear();
            total = BigDecimal.ZERO;
            nombreArticles = 0;
        }
    }
    
    public void augmenterQuantite(Long ligneId) {
        try {
            LignePanier ligne = lignes.stream()
                .filter(l -> l.getId().equals(ligneId))
                .findFirst()
                .orElse(null);
            
            if (ligne != null) {
                int nouvelleQuantite = ligne.getQuantite() + 1;
                panierService.modifierQuantite(ligneId, nouvelleQuantite);
                refresh();
                addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Quantité mise à jour");
            }
        } catch (IllegalStateException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la mise à jour");
        }
    }
    
    public void diminuerQuantite(Long ligneId) {
        try {
            LignePanier ligne = lignes.stream()
                .filter(l -> l.getId().equals(ligneId))
                .findFirst()
                .orElse(null);
            
            if (ligne != null) {
                int nouvelleQuantite = ligne.getQuantite() - 1;
                panierService.modifierQuantite(ligneId, nouvelleQuantite);
                refresh();
                addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Quantité mise à jour");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la mise à jour");
        }
    }
    
    public void supprimerLigne(Long ligneId) {
        try {
            panierService.supprimerLigne(ligneId);
            refresh();
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                "Produit retiré du panier");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la suppression");
        }
    }
    
    public void viderPanier() {
        try {
            Long panierId = internauteBean.getPanierId();
            if (panierId != null) {
                panierService.viderPanier(panierId);
                refresh();
                addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Panier vidé");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors du vidage du panier");
        }
    }
    
    public String validerCommande() {
        try {
            Long panierId = internauteBean.getPanierId();
            if (panierId != null) {
                panierService.validerCommande(panierId);
                refresh();
                addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Commande validée avec succès !");
                return "/checkout.xhtml?faces-redirect=true";
            }
        } catch (IllegalStateException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage());
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la validation de la commande");
        }
        return null;
    }
    
    public List<LignePanier> getLignes() {
        if (!initialized) {
            refresh();
        }
        return lignes;
    }
    
    public boolean isPanierVide() {
        if (!initialized) {
            refresh();
        }
        return lignes == null || lignes.isEmpty();
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}

