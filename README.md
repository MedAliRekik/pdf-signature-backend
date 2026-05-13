# PDF Signature Backend

Backend **Spring Boot** qui permet d’uploader un PDF, d’ajouter une signature visuelle générée à partir d’un nom, d’ajouter un texte optionnel, puis de retourner le fichier signé en téléchargement.

## Objectif du projet

Ce service expose une API REST pour :

1. recevoir un fichier PDF,
2. valider la requête et les paramètres,
3. ajouter une signature texte (nom du signataire) à une position donnée,
4. ajouter un texte personnalisé optionnel,
5. générer et renvoyer le PDF signé.

## Technologies utilisées

- Java 21
- Spring Boot 4
- Spring Web
- Spring Validation
- Apache PDFBox 3.0.7
- springdoc-openapi (Swagger UI)
- Maven

## Structure actuelle

```text
src/main/java/com/onlyu/pdfsignature
├── controller
│   └── PdfSignatureController.java
├── dto
│   ├── PdfSignatureRequest.java
│   └── PdfSignatureResponse.java
├── exception
│   ├── GlobalExceptionHandler.java
│   └── PdfProcessingException.java
├── service
│   ├── PdfSignatureService.java
│   └── impl
│       └── PdfSignatureServiceImpl.java
└── PdfSignatureBackendApplication.java
```

## Endpoints disponibles

### `POST /api/pdf/sign`

- **Consomme** : `multipart/form-data`
- **Produit** : `application/pdf`
- **Description** : signe visuellement un PDF et renvoie le document final.

#### Paramètres multipart

- `file` *(file, obligatoire)* : document PDF à signer.
- `request` *(json, obligatoire)* : objet de signature :
    - `signerName` *(string, obligatoire)*
    - `additionalText` *(string, optionnel)*
    - `pageNumber` *(int, obligatoire, >= 1)*
    - `x` *(float, obligatoire, >= 0)*
    - `y` *(float, obligatoire, >= 0)*

## Exemple d’appel API (`multipart/form-data`)

```bash
curl -X POST "http://localhost:8080/api/pdf/sign" \
  -H "Accept: application/pdf" \
  -F "file=@/chemin/document.pdf;type=application/pdf" \
  -F 'request={"signerName":"Ali Rekik","additionalText":"Bon pour accord","pageNumber":1,"x":120,"y":140};type=application/json' \
  --output signed-document.pdf
```

## Lancement du projet

### Prérequis

- Java 21
- Maven 3.9+

### Commandes

```bash
mvn clean install
mvn spring-boot:run
```

Application : `http://localhost:8080`

Swagger UI : `http://localhost:8080/swagger-ui.html`
OpenAPI JSON : `http://localhost:8080/v3/api-docs`

## Sécurité et robustesse déjà en place

- validation des champs (`@Valid`, contraintes Bean Validation),
- vérification du type MIME `application/pdf`,
- vérification du header binaire `%PDF-`,
- limite de taille des fichiers (Spring multipart + garde applicative),
- validation du numéro de page sur le document réel,
- gestion centralisée des erreurs avec messages maîtrisés,
- logs applicatifs sur les étapes clés sans fuite de contenu PDF.

## Améliorations futures

- signature graphique (image manuscrite) en plus du texte,
- choix de police/couleur/taille dynamiques,
- support multi-signatures et multi-pages en une requête,
- ajout d’une signature numérique cryptographique (PKCS#12),
- authentification/autorisation (JWT, OAuth2),
- audit trail (traçabilité des signatures),
- tests unitaires et d’intégration plus complets (controller/service),
- conteneurisation Docker et CI/CD.
