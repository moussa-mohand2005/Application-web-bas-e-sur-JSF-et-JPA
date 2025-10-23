# 🛒 Application web basée sur JSF et JPA

## 📖 Introduction
Ce projet est une application web de **gestion des commandes e-commerce** destinée aux clients, vendeurs et administrateurs.  
Elle repose sur l’architecture **MVC2** avec **Jakarta EE 10**, **JSF 4.0**, **PrimeFaces**, **CDI**, **JPA/Hibernate**, et **MySQL**.  
Les beans JSF jouent le rôle de contrôleurs, les pages XHTML constituent la vue, et les entités JPA assurent la couche modèle.  
L’objectif principal est de proposer une application claire, modulaire, sécurisée et facilement déployable sur **WildFly** via **Maven**.

---

## 🧩 Modélisation UML
Le modèle métier comprend cinq classes principales :  
**Internaute**, **Panier**, **LignePanier**, **Produit**, et **Role**.  
Les relations traduisent les interactions classiques d’un système e-commerce :
- Un **Internaute** possède un **Panier** unique et plusieurs **Rôles**.  
- Un **Panier** contient plusieurs **Lignes de panier**.  
- Chaque **LignePanier** est liée à un **Produit** précis.  
- Les rôles gèrent les droits d’accès : `ADMIN`, `VENDEUR`, `ACHETEUR`.


<img width="428" height="858" alt="XLDHRzem47xFhx3e2-j4jKdR2qAHQA5Q9OOgA7ityIsSS7oPVIb3K__xR2Tfay5eyf3pzrs-k-ziCvM2IMo9cGgXq4B6I2PnUpIbjB8GMvdjSqkLKNUaoOh33XawlDj9HGSqkVWfjUio6WtmiGH1bdVK8J8jdHD_4Y5k_n6_kf-6Dp1a5TW6_H8VZqwjfKC3VesF1-_9y9gF6lnFMYKlIPB" src="https://github.com/user-attachments/assets/5a6641ff-04ab-4f3b-a67a-1b16b5b00c8f" />

---

## 🧱 Architecture et Organisation du code
L’application respecte l’architecture **MVC2** :
- **Contrôleurs** → Beans JSF gérant les actions, les formulaires et la logique métier.  
- **Vues** → Pages XHTML structurées autour d’un layout principal (`layout.xhtml`).  
- **Modèle** → Entités JPA et services assurant la persistance via Hibernate/MySQL.  
- **Sécurité & CDI** → Gestion des rôles et injection des dépendances.

### 📁 Structure des packages :
net.moussa.moussa.model → Entités JPA (Internaute, Produit, Panier, LignePanier, Role)
net.moussa.moussa.service → Services métier et gestion CRUD
net.moussa.moussa.bean → JSF Beans (Contrôleurs)
net.moussa.moussa.security → Gestion des rôles, sessions et accès

---

## 🔐 Sécurité (RBAC, Sessions, CSRF)
L’application met en œuvre un contrôle d’accès par rôles (**RBAC**) :
- **ADMIN** → Administration complète des utilisateurs et produits  
- **VENDEUR** → Gestion des produits et commandes reçues  
- **CLIENT (ACHETEUR)** → Gestion du panier et des commandes personnelles  

Autres mécanismes :
- Sessions JSF pour la persistance de l’authentification  
- Protection **CSRF** intégrée à JSF  
- Annotations `@RolesAllowed` pour la sécurité des beans  

---

## 🗄️ Accès aux données et Services
Les données sont gérées via **JPA 3.0/Hibernate** avec une **DataSource JNDI** (`java:jboss/datasources/MySQLDS`).  
Les services (ProduitService, InternauteService, PanierService) offrent :
- CRUD complet des entités  
- Validation et cohérence métier (stock, quantité, etc.)  
- Requêtes sécurisées **JPQL/Criteria** contre l’injection SQL  
- Authentification sécurisée (hachage SHA-256)

---

## 💻 Vues JSF (Interface utilisateur)
Les pages XHTML utilisent **JSF + PrimeFaces** pour un rendu dynamique et réactif :
- `index.xhtml` → Catalogue des produits  
- `login.xhtml` → Authentification  
- `register.xhtml` → Inscription  
- `panier.xhtml` → Gestion du panier  
- `checkout.xhtml` → Validation de commande  
- `admin/users.xhtml`, `admin/produits.xhtml` → Gestion par l’administrateur  

Les composants JSF affichent les données via EL (`#{}`), et les actions sont reliées aux beans JSF.  
La structure est centralisée via `layout.xhtml` avec des inclusions (`<ui:include>`) pour l’en-tête, le pied de page et les messages.

---

## ⚙️ Technologies principales
| Technologie | Rôle |
|--------------|------|
| **Jakarta EE 10** | Base du framework web |
| **JSF 4.0 / PrimeFaces** | Interface et interaction utilisateur |
| **JPA / Hibernate** | Persistance des données |
| **MySQL** | Base de données |
| **WildFly 37** | Serveur d’applications |
| **Maven** | Gestion de projet |
| **CDI** | Injection de dépendances |

---

## 🚀 Déploiement
1. Compiler le projet :  
   ```bash
   mvn clean package
 Déployer le fichier moussa.war sur WildFly :

Copier dans standalone/deployments

Ou déployer depuis IntelliJ / Maven Plugin

Accéder à l’application :
http://localhost:8080/moussa   



Maquettes et interfaces finales

Authentification :

<img width="807" height="938" alt="A2" src="https://github.com/user-attachments/assets/dc70c8dc-1eb8-4b5b-8588-cc987170d435" />

<img width="669" height="675" alt="A1" src="https://github.com/user-attachments/assets/17f33778-543a-4362-aec4-7f8527b0dc21" />

Client :

<img width="1896" height="935" alt="C2" src="https://github.com/user-attachments/assets/e0ec9e1b-b0d9-486d-98b7-1405f5a79910" />
<img width="1882" height="950" alt="C1" src="https://github.com/user-attachments/assets/572f0c21-43b6-46fb-ad6b-12e59e73f4f5" />


Vendeur :

<img width="1828" height="870" alt="V2" src="https://github.com/user-attachments/assets/bc1c251a-db00-4a5b-b6eb-dbe1986e7684" />
<img width="629" height="658" alt="V1" src="https://github.com/user-attachments/assets/1ae2fc4a-588e-4670-bb8e-d78f98ea3611" />

Admin :


<img width="1915" height="795" alt="AD1" src="https://github.com/user-attachments/assets/9a29a8fd-df36-4031-b6c6-0b77701b46eb" />

