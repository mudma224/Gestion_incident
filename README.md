# 🚨 Système de Gestion d'Incidents (Architecture Microservices)

Bienvenue sur le projet **Gestion d'Incidents**. Il s'agit d'une application web complète permettant de déclarer, suivre et gérer des incidents en temps réel.

Ce projet a été conçu avec une **architecture orientée microservices** pour garantir une haute scalabilité, une séparation des responsabilités et une maintenance facilitée.

---

## 🏗️ Architecture du Projet

Le système repose sur un backend en **Java (Spring Boot / Spring Cloud)** et un frontend moderne en **React (Vite)**.

Voici comment les différents blocs communiquent entre eux :

1. **Frontend (React)** : L'interface utilisateur. Elle ne parle jamais directement aux microservices de données, mais passe toujours par la Gateway.
2. **API Gateway (Port 8080)** : Le "concierge" du système. C'est le point d'entrée unique. Il gère le routage, le Load Balancing et la politique CORS.
3. **Eureka Server (Port 8761)** : L'annuaire du système (Service Discovery). Chaque microservice vient s'y inscrire au démarrage.
4. **Config Server (Port 8888)** : Centralise toutes les configurations des microservices via un repo Git.
5. **User Service (Port 8081)** : Gestion des profils utilisateurs et synchronisation avec Keycloak.
6. **Incident Service (Port 8082)** : Le cœur métier. Gère le cycle de vie complet des incidents.
7. **Comment Service (Port 8083)** : Gestion des commentaires sur les incidents.
8. **Notification Service (Port 8085)** : Gestion des notifications in-app et email. *(Nouveau)*
9. **Keycloak (Port 8180)** : Serveur d'authentification IAM — gère les utilisateurs, rôles et tokens JWT.
10. **MinIO (Port 9000)** : Stockage de fichiers compatible S3 — avatars et captures d'écran.

---

## 🚀 Fonctionnalités

- [x] Affichage en temps réel de la liste des incidents.
- [x] Création d'un nouvel incident (Titre, Description, Statut, Priorité).
- [x] Gestion complète du cycle de vie des incidents (Nouveau → Assigné → En cours → Résolu → Fermé).
- [x] Système de commentaires sur les incidents.
- [x] Gestion des profils utilisateurs avec avatars (MinIO).
- [x] Authentification sécurisée avec Keycloak (JWT).
- [x] 🔔 Système de notifications in-app et email (RGPD-compliant).
- [ ] Service de Chat intelligent (à venir).
- [ ] Tableau de bord Administrateur React (à venir).

---

## 🛠️ Technologies Utilisées

* **Backend** : Java 21, Spring Boot 4.0.6, Spring Cloud 2025.1.1 (Gateway MVC, Netflix Eureka, Config Server).
* **Sécurité** : Keycloak 23.0, Spring Security 7, OAuth2 / JWT.
* **Base de données** : PostgreSQL 18 (une base par service).
* **Stockage fichiers** : MinIO.
* **Notifications** : Spring Mail, Thymeleaf.
* **Frontend** : React.js, Vite, Axios, Lucide-React.
* **DevOps** : Docker, Docker Compose, Git.
* **Gestionnaire de paquets** : Maven (Backend), npm (Frontend).

---

## 📂 Structure du Projet

```text
Gestion_incident/
├── services/
│   ├── eureka-server/           # Annuaire des microservices (Port 8761)
│   ├── config-server/           # Configuration centralisée (Port 8888)
│   ├── gateway/                 # Point d'entrée unique & Routage (Port 8080)
│   ├── user-service/            # Gestion des utilisateurs (Port 8081)
│   ├── incident-service/        # Gestion des incidents (Port 8082)
│   ├── comment-service/         # Gestion des commentaires (Port 8083)
│   └── notification-service/    # 🔔 Gestion des notifications (Port 8085)
├── config-repo/                 # Fichiers de configuration Git
├── frontend/                    # Interface Utilisateur React (Port 5173)
├── docker-compose.yml           # Keycloak + MinIO
└── README.md
```

---

## 🔔 Service de Notification

> **Responsable** : [Ton nom]

### Description
Service responsable de l'envoi et la gestion des notifications pour tous les événements du système (changement de statut d'incident, assignation, commentaires...).

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| POST | `/api/notifications/send` | Envoyer une notification |
| GET | `/api/notifications/me` | Mes notifications |
| GET | `/api/notifications/me/unread` | Notifications non lues |
| GET | `/api/notifications/me/unread/count` | Compteur non lues |
| PATCH | `/api/notifications/{id}/read` | Marquer comme lue |
| DELETE | `/api/notifications/me` | Supprimer mes notifications (RGPD) |

### Conformité RGPD
- Email **non stocké** en base — uniquement `recipientKeycloakId`
- Droit d'accès : chaque utilisateur ne voit que ses propres notifications
- Droit à l'effacement : `DELETE /api/notifications/me`

---

## ⚙️ Prérequis

- Java Development Kit (JDK) 21
- Node.js & npm (pour le frontend)
- Maven (inclus via le wrapper `mvnw`)
- PostgreSQL 18 (local)
- Docker Desktop (pour Keycloak et MinIO)

### Bases de données à créer

```sql
CREATE USER incidents_user WITH PASSWORD 'incidents_pass';
CREATE DATABASE user_db OWNER incidents_user;
CREATE DATABASE incident_db OWNER incidents_user;
CREATE DATABASE comment_db OWNER incidents_user;
CREATE DATABASE notification_db OWNER incidents_user;
CREATE DATABASE keycloak_db OWNER incidents_user;
```

---

## 🏃‍♂️ Comment lancer le projet localement ?

L'ordre de lancement est très important.

**1. Lancer Docker (Keycloak + MinIO)**
```bash
docker compose up -d
```
Vérifications :
- Keycloak → http://localhost:8180 (admin / admin)
- MinIO → http://localhost:9001 (minio_user / minio_pass_2024)

**2. Lancer Eureka Server**
```bash
cd services/eureka-server
./mvnw spring-boot:run
```
Vérification → http://localhost:8761

**3. Lancer Config Server**
```bash
cd services/config-server
./mvnw spring-boot:run
```
Vérification → http://localhost:8888/user-service/default

**4. Lancer les services métiers**
```bash
# User Service
cd services/user-service && ./mvnw spring-boot:run

# Incident Service
cd services/incident-service && ./mvnw spring-boot:run

# Comment Service
cd services/comment-service && ./mvnw spring-boot:run

# Notification Service
cd services/notification-service && ./mvnw spring-boot:run
```

**5. Lancer la Gateway (en dernier)**
```bash
cd services/gateway
./mvnw spring-boot:run
```

**6. Lancer le Frontend**
```bash
cd frontend
npm install
npm run dev
```
L'application sera accessible sur http://localhost:5173.

---

## 🤝 Comment contribuer ?

1. Créer une branche pour votre fonctionnalité :
```bash
git checkout -b feature/MaNouvelleFonctionnalite
```
2. Commiter vos changements :
```bash
git commit -m 'feat: Ajout d'une nouvelle fonctionnalité'
```
3. Pousser vers la branche :
```bash
git push origin feature/MaNouvelleFonctionnalite
```
4. Ouvrir une **Pull Request** vers `main`.