# Gestion RH - Système de Contrats

## Architecture

Ce projet est composé de :
- **Backend** : Spring Boot (cc-java-k) - API REST pour la gestion des contrats
- **Frontend** : React (grh-contra-frontend) - Interface utilisateur

## Configuration de la Communication

### 1. Configuration CORS (Backend)
Le contrôleur `ContratController` est configuré avec :
```java
@CrossOrigin(origins = "http://localhost:3000")
```
Cette annotation autorise les requêtes provenant du frontend React.

### 2. Configuration Proxy (Frontend)
Le fichier `vite.config.js` contient une configuration proxy :
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
    }
  }
}
```
Cette configuration redirige automatiquement les requêtes `/api/*` vers le backend Spring Boot.

### 3. Services API
Le fichier `src/services/api.js` contient les fonctions pour communiquer avec l'API :
- `getContrats()` : Récupérer tous les contrats
- `createContrat()` : Créer un nouveau contrat
- `activerContrat()` : Activer un contrat existant

## Démarrage

### Backend (Spring Boot)
```bash
# Depuis la racine du projet cc-java-k
./mvnw spring-boot:run
```
Le backend sera disponible sur `http://localhost:8080`

### Frontend (React)
```bash
# Depuis le dossier grh-contra-frontend
npm install
npm run dev
```
Le frontend sera disponible sur `http://localhost:5173`

## Fonctionnalités

### Gestion des Contrats
- **Création** : Formulaire pour créer de nouveaux contrats
- **Liste** : Affichage de tous les contrats avec leur statut
- **Activation** : Possibilité d'activer un contrat en statut "BROUILLON"

### Statuts des Contrats
- `BROUILLON` : Contrat nouvellement créé
- `EN_COURS` : Contrat actif
- `TERMINE` : Contrat terminé
- `ANNULE` : Contrat annulé

## API Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/contrats` | Récupérer tous les contrats |
| POST | `/api/contrats` | Créer un nouveau contrat |
| PUT | `/api/contrats/{id}/activer` | Activer un contrat |

## Technologies

### Backend
- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- PostgreSQL
- Lombok

### Frontend
- React 19.2.5
- Vite 8.0.9
- Tailwind CSS
- JavaScript ES6+

## Notes de Développement

### Modifications apportées pour la communication :

1. **vite.config.js** : Ajout de la configuration proxy pour rediriger `/api/*` vers `http://localhost:8080`
2. **src/services/api.js** : Création du service API avec les méthodes HTTP nécessaires
3. **src/components/ContratList.jsx** : Composant pour afficher et gérer la liste des contrats
4. **src/components/ContratForm.jsx** : Formulaire de création de contrats
5. **src/App.jsx** : Intégration des composants dans une interface complète
6. **src/index.css** : Ajout de Tailwind CSS pour le style
7. **tailwind.config.js** : Configuration de Tailwind CSS

### Raisons des modifications :

- **Proxy Vite** : Évite les problèmes CORS en développement et simplifie les appels API
- **Service API** : Centralise la logique de communication avec le backend
- **Composants React** : Structure modulaire et réutilisable pour l'interface
- **Tailwind CSS** : Framework CSS moderne pour un design responsive rapide
- **CORS Backend** : Autorise explicitement le frontend à communiquer avec l'API
