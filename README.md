# pdf-signature-backend
# PDF Signature Backend

Backend Spring Boot permettant de signer visuellement un fichier PDF en ajoutant :

- Une signature générée à partir d’un nom
- Un texte personnalisé
- Une position dynamique dans le document PDF

Le projet utilise Apache PDFBox pour manipuler les fichiers PDF.

---

# Objectif du projet

Ce projet a pour objectif de fournir une API REST simple permettant de :

1. Importer un fichier PDF
2. Ajouter une signature visuelle générée à partir du nom du signataire
3. Ajouter un texte optionnel
4. Générer un nouveau PDF modifié
5. Télécharger le PDF signé

---

# Technologies utilisées

- Java 21
- Spring Boot 3
- Maven
- Apache PDFBox
- Jakarta Validation

Apache PDFBox est une bibliothèque Java open source permettant de créer et modifier des fichiers PDF.  
:contentReference[oaicite:0]{index=0}

---

# Architecture du projet

Le projet respecte les principes :

- SOLID
- Clean Code
- Separation of Concerns
- Architecture en couches

Structure actuelle :

```text
src/main/java/com/onlyu/pdfsignature
│
├── controller
│   └── PdfSignatureController.java
│
├── dto
│   ├── PdfSignatureRequest.java
│   └── PdfSignatureResponse.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   └── PdfProcessingException.java
│
├── service
│   ├── PdfSignatureService.java
│   │
│   └── impl
│       └── PdfSignatureServiceImpl.java
│
└── PdfSignatureBackendApplication.java

Fonctionnalités actuelles
Upload d’un fichier PDF
Validation des données
Vérification du format PDF
Ajout d’une signature texte
Ajout d’un texte personnalisé
Génération d’un nouveau PDF
Téléchargement du PDF signé
Gestion centralisée des exceptions
Fonctionnalités prévues
Backend
Gestion de plusieurs pages
Positionnement dynamique
Gestion des polices personnalisées
Ajout de signatures image
Historique des signatures
Génération de QR Code
Sécurisation des endpoints
Tests unitaires
Frontend Angular
Upload du PDF
Prévisualisation PDF
Drag & Drop de la signature
Ajout dynamique du texte
Téléchargement du PDF final
UI responsive
Endpoint actuel
Signature PDF
POST /api/pdf/sign
Request

Multipart form-data :

Key	Type
file	File
request	JSON

Exemple JSON :

{
  "signerName": "Mohamed Ali Rekik",
  "additionalText": "Bon pour accord",
  "pageNumber": 1,
  "x": 100,
  "y": 150
}
Response
signed-document.pdf
Dépendances Maven principales
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.7</version>
</dependency>

Apache PDFBox 3.x est la branche moderne actuelle du projet PDFBox.


Lancement du projet
Prérequis
Java 21
Maven 3.9+
IntelliJ IDEA
Installation
git clone <repository-url>

cd pdf-signature-backend
Build
mvn clean install
Run
mvn spring-boot:run

Application disponible sur :

http://localhost:8080
Philosophie du projet

Le projet est développé progressivement avec :

Une architecture propre et maintenable
Des responsabilités bien séparées
Une logique métier centralisée
Une API REST simple
Une base évolutive pour les futures fonctionnalités
Auteur

Ali Rekik
Full Stack Java / Angular Engineer

::contentReference[oaicite:2]{index=2}
