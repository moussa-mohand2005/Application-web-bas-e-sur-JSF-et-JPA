-- Données initiales pour E-Commerce

-- Insertion de produits de démonstration
INSERT INTO produit (libelle, description, prix, stock, image_url) VALUES 
('Laptop HP', 'Ordinateur portable HP 15.6 pouces, Intel Core i5, 8GB RAM, 256GB SSD', 6500.00, 15, 'https://via.placeholder.com/300x300.png?text=Laptop+HP'),
('Smartphone Samsung Galaxy', 'Samsung Galaxy A54 5G, 128GB, Écran AMOLED 6.4 pouces', 3200.00, 25, 'https://via.placeholder.com/300x300.png?text=Samsung+Galaxy'),
('Casque Bluetooth Sony', 'Casque sans fil Sony WH-1000XM4, Réduction de bruit active', 2800.00, 30, 'https://via.placeholder.com/300x300.png?text=Casque+Sony'),
('Tablette iPad Air', 'Apple iPad Air 10.9 pouces, WiFi, 64GB', 4500.00, 10, 'https://via.placeholder.com/300x300.png?text=iPad+Air'),
('Montre Connectée Apple Watch', 'Apple Watch Series 8, GPS, 41mm', 3800.00, 20, 'https://via.placeholder.com/300x300.png?text=Apple+Watch'),
('Clavier Mécanique Logitech', 'Clavier gaming Logitech G Pro X, Switches mécaniques', 1200.00, 40, 'https://via.placeholder.com/300x300.png?text=Clavier+Logitech'),
('Souris Gaming Razer', 'Souris Razer DeathAdder V2, Capteur optique 20000 DPI', 550.00, 50, 'https://via.placeholder.com/300x300.png?text=Souris+Razer'),
('Écran Dell UltraSharp', 'Moniteur Dell 27 pouces 4K, IPS, USB-C', 4200.00, 12, 'https://via.placeholder.com/300x300.png?text=Ecran+Dell'),
('Imprimante Canon', 'Imprimante multifonction Canon PIXMA, WiFi, Impression couleur', 980.00, 18, 'https://via.placeholder.com/300x300.png?text=Imprimante+Canon'),
('Disque Dur Externe Seagate', 'Disque dur externe 2TB, USB 3.0, Portable', 680.00, 35, 'https://via.placeholder.com/300x300.png?text=HDD+Seagate');

-- Note: Les utilisateurs seront créés via l'interface d'inscription
-- car les mots de passe doivent être hashés avec PBKDF2

