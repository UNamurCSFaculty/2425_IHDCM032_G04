# e-Anacarde

[![Status](https://github.com/UNamurCSFaculty/2425_IHDCM032_G04/actions/workflows/java.yml/badge.svg?branch=main)](https://github.com/UNamurCSFaculty/2425_IHDCM032_G04/actions/workflows/java.yml)
[![Status](https://github.com/UNamurCSFaculty/2425_IHDCM032_G04/actions/workflows/client-pipeline.yml/badge.svg?branch=main)](https://github.com/UNamurCSFaculty/2425_IHDCM032_G04/actions/workflows/client-pipeline.yml)
[![Java CodeQL](https://github.com/UNamurCSFaculty/2425_IHDCM032_G04/actions/workflows/java-codeql.yml/badge.svg?branch=main)](https://github.com/UNamurCSFaculty/2425_IHDCM032_G04/actions/workflows/java-codeql.yml) 
![Coverage](.github/badges/jacoco.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)


## Pré-requis
L'application nécessite l'installation de [Docker](https://www.docker.com/)

## Démarrage
L'application peut être démarrée à vide ou inclure des données de test réalistes. Après avoir cloné le repository, exécutez l'une des commandes suivantes à la racine du dossier.

#### Démarrage à vide
```
docker-compose -f docker-compose.yml up
```

#### Démarrage avec données de test
```
docker-compose -f docker-compose.yml -f docker-compose.testdata.yml up
```

#### Arrêt

```
docker-compose down --volumes
```

## Utilisation

#### Endpoints
L'application expose deux endpoints.

| URL                         |  Service     |
|------------------------------|------------------|
| https://localhost:3000             | Frontend - Interface WEB        |
| https://localhost:8080/api         | Backend - API REST      |

#### Administrateur par défaut
Un utilisateur administrateur est automatiquement créé dans la plateforme avec les valeurs par défaut suivantes. Ces paramètres peuvent être modifiés directement dans le fichier Docker Compose, via les propriétés `APP_ADMIN_EMAIL` et `APP_ADMIN_PASSWORD`.


| Profil | Email                         | Mot de passe     |
|----------------|--------------|------------------|
| Administrateur | admin@anacarde.local           | AdminDev@2025         |

#### Utilisateurs de test
Les utilisateurs suivants sont insérés avec des données de test afin de faciliter l’évaluation de la plateforme.

| Profil | Email                         | Mot de passe     |
|----------------|--------------|------------------|
| Administrateur | admin@example.com            | password         |
| Producteur | producer@example.com         | password      |
| Transformateur | transformer@example.com      | password   |
| Exportateur | exporter@example.com         | password      |
| Transporteur | carrier@example.com          | password       |
| Qualiticien | quality_inspector@example.com | password     |
