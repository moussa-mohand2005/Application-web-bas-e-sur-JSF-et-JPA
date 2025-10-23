package net.moussa.moussa.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import net.moussa.moussa.model.Internaute;
import net.moussa.moussa.model.Role;
import net.moussa.moussa.service.InternauteService;

import java.io.Serializable;

@Named
@SessionScoped
@Getter
@Setter
public class InternauteBean implements Serializable {
    
    @Inject
    private InternauteService internauteService;
    
    private Internaute currentUser;
    
    @PostConstruct
    public void init() {
        loadCurrentUser();
    }
    
    public void loadCurrentUser() {
        // قراءة المستخدم من الـ session
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            currentUser = (Internaute) request.getSession().getAttribute("currentUser");
        }
    }
    
    public boolean isLoggedIn() {
        loadCurrentUser(); // تحديث المستخدم في كل مرة
        return currentUser != null;
    }
    
    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRoles().contains(Role.ADMIN);
    }
    
    public boolean isVendeur() {
        return isLoggedIn() && currentUser.getRoles().contains(Role.VENDEUR);
    }
    
    public boolean isAcheteur() {
        return isLoggedIn() && currentUser.getRoles().contains(Role.ACHETEUR);
    }
    
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getNom() : "Invité";
    }
    
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }
    
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    public Long getPanierId() {
        // إعادة تحميل المستخدم من Database للتأكد من الـ Panier محدّث
        System.out.println("=== getPanierId() called ===");
        System.out.println("currentUser: " + (currentUser != null ? currentUser.getEmail() : "NULL"));
        
        if (currentUser != null && internauteService != null) {
            System.out.println("currentUser ID: " + currentUser.getId());
            internauteService.findById(currentUser.getId()).ifPresent(freshUser -> {
                System.out.println("Fresh user loaded from DB");
                if (freshUser.getPanier() != null) {
                    System.out.println("Panier ID from DB: " + freshUser.getPanier().getId());
                    // تحديث panier في currentUser
                    currentUser.setPanier(freshUser.getPanier());
                } else {
                    System.out.println("WARNING: Fresh user has NULL panier!");
                }
            });
        } else {
            System.out.println("WARNING: currentUser or internauteService is NULL!");
        }
        
        Long panierId = currentUser != null && currentUser.getPanier() != null 
            ? currentUser.getPanier().getId() 
            : null;
        System.out.println("Returning panier ID: " + panierId);
        return panierId;
    }
}

