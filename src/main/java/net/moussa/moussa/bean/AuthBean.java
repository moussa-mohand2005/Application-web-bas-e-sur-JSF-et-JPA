package net.moussa.moussa.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import net.moussa.moussa.model.Internaute;
import net.moussa.moussa.model.Role;
import net.moussa.moussa.service.InternauteService;

import java.io.Serializable;
import java.util.Optional;

@Named("authBean")
@RequestScoped
public class AuthBean implements Serializable {
    
    @Inject
    private InternauteService internauteService;
    
    private String email;
    private String password;
    private String nom;
    private String confirmPassword;
    private String selectedRole = "ACHETEUR";
    
    // Constructor
    public AuthBean() {
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getSelectedRole() {
        return selectedRole;
    }
    
    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
    }
    
    public String login() {
        // Validation
        if (email == null || email.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "L'email est obligatoire");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le mot de passe est obligatoire");
            return null;
        }
        
        // Rechercher l'utilisateur
        Optional<Internaute> optInternaute = internauteService.findByEmail(email);
        
        if (optInternaute.isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Email ou mot de passe incorrect");
            return null;
        }
        
        Internaute internaute = optInternaute.get();
        
        // Vérifier le mot de passe (comparaison simple)
        if (!password.equals(internaute.getMotDePasse())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Email ou mot de passe incorrect");
            return null;
        }
        
        // Connexion réussie! Recharger l'utilisateur avec le panier
        Internaute freshUser = internauteService.findById(internaute.getId()).orElse(internaute);
        
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("User ID: " + freshUser.getId());
        System.out.println("User Email: " + freshUser.getEmail());
        System.out.println("Panier: " + (freshUser.getPanier() != null ? freshUser.getPanier().getId() : "NULL"));
        
        // Stocker l'utilisateur dans la session
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().setAttribute("currentUser", freshUser);
        
        addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Bienvenue " + freshUser.getNom() + " !");
        
        return "/index.xhtml?faces-redirect=true";
    }
    
    public String register() {
        // Validation
        if (email == null || email.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "L'email est obligatoire");
            return null;
        }
        
        if (password == null || password.length() < 6) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Le mot de passe doit contenir au moins 6 caractères");
            return null;
        }
        
        if (!password.equals(confirmPassword)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Les mots de passe ne correspondent pas");
            return null;
        }
        
        // Vérifier si l'email existe déjà
        if (internauteService.emailExists(email)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Cet email est déjà utilisé");
            return null;
        }
        
        try {
            // Créer le nouvel utilisateur
            Internaute internaute = new Internaute();
            internaute.setNom(nom);
            internaute.setEmail(email);
            internaute.setMotDePasse(password);
            
            Role role = Role.valueOf(selectedRole);
            internauteService.register(internaute, role);
            
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            
            return "/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Une erreur est survenue lors de l'inscription: " + e.getMessage());
            return null;
        }
    }
    
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        try {
            request.logout();
            request.getSession().invalidate();
            return "/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", 
                "Erreur lors de la déconnexion: " + e.getMessage());
            return null;
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}

