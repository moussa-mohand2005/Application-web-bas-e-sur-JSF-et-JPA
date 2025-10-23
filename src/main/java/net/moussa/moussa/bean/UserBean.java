package net.moussa.moussa.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.moussa.moussa.model.Internaute;
import net.moussa.moussa.model.Role;
import net.moussa.moussa.service.InternauteService;

import java.util.List;

@Named
@jakarta.enterprise.context.SessionScoped
@Getter
@Setter
public class UserBean implements java.io.Serializable {
    
    @Inject
    private InternauteService internauteService;
    
    @Inject
    private InternauteBean internauteBean;
    
    private List<Internaute> users;
    private Internaute selectedUser;
    
    // Roles pour modification
    private boolean adminRole = false;
    private boolean vendeurRole = false;
    private boolean acheteurRole = false;
    
    @PostConstruct
    public void init() {
        loadUsers();
    }
    
    public void loadUsers() {
        if (users == null) {
            users = internauteService.findAll();
        }
    }
    
    public List<Internaute> getUsers() {
        loadUsers();
        return users;
    }
    
    public void deleteUser(Long id) {
        try {
            // Empêcher l'auto-suppression
            if (id.equals(internauteBean.getCurrentUserId())) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                    "Vous ne pouvez pas supprimer votre propre compte");
                return;
            }
            
            internauteService.delete(id);
            loadUsers();
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la suppression: " + e.getMessage());
        }
    }
    
    public void updateUserRoles() {
        try {
            System.out.println("=== updateUserRoles() called ===");
            System.out.println("selectedUser: " + (selectedUser != null ? selectedUser.getEmail() : "NULL"));
            System.out.println("adminRole: " + adminRole);
            System.out.println("vendeurRole: " + vendeurRole);
            System.out.println("acheteurRole: " + acheteurRole);
            
            if (selectedUser != null) {
                // Clear existing roles
                selectedUser.getRoles().clear();
                
                // Add selected roles
                if (adminRole) {
                    selectedUser.addRole(Role.ADMIN);
                    System.out.println("Added ADMIN role");
                }
                if (vendeurRole) {
                    selectedUser.addRole(Role.VENDEUR);
                    System.out.println("Added VENDEUR role");
                }
                if (acheteurRole) {
                    selectedUser.addRole(Role.ACHETEUR);
                    System.out.println("Added ACHETEUR role");
                }
                
                // Ensure at least one role
                if (selectedUser.getRoles().isEmpty()) {
                    selectedUser.addRole(Role.ACHETEUR);
                    System.out.println("Added default ACHETEUR role");
                }
                
                System.out.println("Final roles: " + selectedUser.getRoles());
                internauteService.update(selectedUser);
                loadUsers();
                addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Rôles mis à jour avec succès");
            }
        } catch (Exception e) {
            System.err.println("ERROR in updateUserRoles(): " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la mise à jour: " + e.getMessage());
        }
    }
    
    public void prepareEditUser(Internaute user) {
        System.out.println("=== prepareEditUser() called ===");
        System.out.println("User: " + user.getEmail());
        System.out.println("Current roles: " + user.getRoles());
        
        selectedUser = user;
        adminRole = user.getRoles().contains(Role.ADMIN);
        vendeurRole = user.getRoles().contains(Role.VENDEUR);
        acheteurRole = user.getRoles().contains(Role.ACHETEUR);
        
        System.out.println("Set adminRole: " + adminRole);
        System.out.println("Set vendeurRole: " + vendeurRole);
        System.out.println("Set acheteurRole: " + acheteurRole);
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}

