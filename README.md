# 🚨Système de Gestion d'Incidents (Architecture Microservices)

Bienvenue sur le projet **Gestion d'Incidents**. Il s'agit d'une application web complète permettant de déclarer, suivre et gérer des incidents en temps réel. 

Ce projet a été conçu avec une **architecture orientée microservices** pour garantir une haute scalabilité, une séparation des responsabilités et une maintenance facilitée.

---

## 🏗️ Architecture du Projet

Le système repose sur un backend en **Java (Spring Boot / Spring Cloud)** et un frontend moderne en **React (Vite)**. 

Voici comment les différents blocs communiquent entre eux :

1. **Frontend (React)** : L'interface utilisateur. Elle ne parle jamais directement aux microservices de données, mais passe toujours par la Gateway.
2. **API Gateway (Port 8080)** : Le "concierge" du système. C'est le point d'entrée unique. Il gère le routage (en supprimant le préfixe `/api`), le Load Balancing et la politique CORS.
3. **Eureka Server (Port 8761)** : L'annuaire du système (Service Discovery). Chaque microservice vient s'y inscrire au démarrage. Ainsi, la Gateway sait toujours où trouver les services sans avoir besoin de coder leurs adresses IP en dur.
4. **Incident Service (Port 8081)** : Le cœur métier actuel (MVP). Il gère la création et la récupération des incidents en base de données.

---

## 🚀 Fonctionnalités Actuelles (MVP)

- [x] Affichage en temps réel de la liste des incidents.
- [x] Création d'un nouvel incident (Titre, Description, Statut).
- [x] Communication sécurisée Frontend ↔ Backend via l'API Gateway.
- [x] Enregistrement dynamique des services via Eureka.

*À venir : Service de Chat, Service de Commentaires, Tableau de bord Administrateur.*

---

## 🛠️ Technologies Utilisées

* **Backend** : Java 21, Spring Boot, Spring Cloud (Gateway MVC, Netflix Eureka).
* **Frontend** : React.js, Vite, Axios (pour les requêtes HTTP), Lucide-React (pour les icônes).
* **Gestionnaire de paquets** : Maven (Backend), npm (Frontend).

---

## 📂 Structure du Projet

```text
GESTION_INCIDENT_LOCAL/
├── eureka-server/           # Annuaire des microservices (Port 8761)
├── gateway-service/         # Point d'entrée unique & Routage (Port 8080)
├── services/                # Dossier contenant tous les microservices métiers
│   ├── incident-service/    # Gère la logique des incidents (Port 8081)
│   ├── chat-service/        # (À venir)
│   └── comment-service/     # (À venir)
├── frontend/                # Interface Utilisateur React (Port 5173)
└── README.md                # Documentation du projet

⚙️ Prérequis
Pour exécuter ce projet localement, vous devez avoir installé :

Java Development Kit (JDK) 21

Node.js & npm (pour le frontend)

Maven (inclus via le wrapper mvnw dans chaque projet Spring)

🏃‍♂️ Comment lancer le projet localement ?
L'ordre de lancement est très important dans une architecture microservices. L'annuaire (Eureka) doit être lancé en premier.

1. Lancer l'Annuaire (Eureka Server)
Ouvrez un terminal dans le dossier eureka-server :

Bash
./mvnw spring-boot:run
Vérification : Allez sur http://localhost:8761. Vous devriez voir le tableau de bord d'Eureka.

2. Lancer le Service Métier (Incident Service)
Ouvrez un terminal dans le dossier services/incident-service :

Bash
./mvnw spring-boot:run
Vérification : Actualisez la page d'Eureka, vous devriez voir INCIDENT-SERVICE apparaître dans la liste des instances.

3. Lancer la Gateway (Point d'entrée)
Ouvrez un terminal dans le dossier gateway-service :

Bash
./mvnw spring-boot:run
Note : La Gateway est configurée en Java (via RouterFunction) pour router /api/incidents/** vers le service d'incidents, tout en gérant le CORS pour le port 5173.

4. Lancer le Frontend (React)
Ouvrez un terminal dans le dossier frontend :

Bash
npm install
npm run dev
L'application sera accessible sur http://localhost:5173. Vous pouvez maintenant créer et voir les incidents !

🤝 Comment contribuer ?
Faire un Fork du dépôt.

Créer une branche pour votre fonctionnalité (git checkout -b feature/MaNouvelleFonctionnalite).

Commiter vos changements (git commit -m 'Ajout d'une nouvelle fonctionnalité').

Pousser vers la branche (git push origin feature/MaNouvelleFonctionnalite).

Ouvrir une Pull Request.