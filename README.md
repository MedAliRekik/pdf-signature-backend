# PDF Signature Backend

Backend **Spring Boot** pour une future application Angular. Le service permet d’uploader un PDF, de poser une signature visuelle générée depuis le nom du signataire, d’ajouter un texte optionnel, puis de renvoyer le PDF final.

## Objectif du projet

L’API REST expose un endpoint métier pour :
1. recevoir un fichier PDF ;
2. valider les paramètres de signature ;
3. signer visuellement à une position donnée ;
4. ajouter un texte optionnel ;
5. retourner le document signé en téléchargement.

## Technologies utilisées

- Java 21
- Spring Boot 4.0.6
- Spring Web
- Spring Validation
- Apache PDFBox 3.0.7
- springdoc-openapi (Swagger UI) 2.8.13
- SLF4J (via Spring Boot logging)
- Maven

## Architecture actuelle

Architecture en couches simple et maintenable :
- **controller** : exposition HTTP/multipart
- **dto** : objets d’entrée/sortie API
- **service** : logique métier de signature PDF
- **exception** : gestion centralisée des erreurs
- **config** : CORS global + OpenAPI

## Structure des packages

```text
src/main/java/com/onlyu/pdfsignature
├── config
│   ├── CorsConfig.java
│   ├── CorsProperties.java
│   └── OpenApiConfig.java
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

## Endpoint disponible

### `POST /api/pdf/sign`

- **Consomme** : `multipart/form-data`
- **Produit** : `application/pdf`
- **Description** : signe visuellement un PDF et renvoie le fichier signé.

### Paramètres multipart attendus

- `file` *(file, obligatoire)* : document PDF à signer.
- `request` *(json, obligatoire)* :
  - `signerName` *(string, obligatoire, non vide, max 120)*
  - `additionalText` *(string, optionnel, max 240)*
  - `pageNumber` *(int, obligatoire, >= 1)*
  - `x` *(float, obligatoire, >= 0)*
  - `y` *(float, obligatoire, >= 0)*

## Exemple d’appel API multipart/form-data

```bash
curl -X POST "http://localhost:8080/api/pdf/sign" \
  -H "Accept: application/pdf" \
  -F "file=@/chemin/document.pdf;type=application/pdf" \
  -F 'request={"signerName":"Ali Rekik","additionalText":"Bon pour accord","pageNumber":1,"x":120,"y":140};type=application/json' \
  --output signed-document.pdf
```

## Configuration CORS pour Angular

CORS est configuré **globalement** (pas de `@CrossOrigin` dans les controllers) sur `/api/**` :
- origine autorisée (temporaire) : `http://localhost:4200`
- méthodes : `GET, POST, PUT, DELETE, OPTIONS`
- headers : tous (`*`)
- header exposé : `Content-Disposition` (utile pour récupérer le nom du fichier côté Angular)

Propriété dédiée :

```properties
app.cors.allowed-origin=http://localhost:4200
```

## Swagger / OpenAPI

- Swagger UI : `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON : `http://localhost:8080/v3/api-docs`

Le endpoint `/api/pdf/sign` est documenté avec :
- payload `multipart/form-data`
- partie `file` (binaire)
- partie `request` (JSON)
- réponse `application/pdf`

## Sécurité minimale et robustesse

- validation Bean Validation sur le DTO
- vérification type MIME `application/pdf`
- vérification signature binaire `%PDF-`
- limite de taille upload via Spring + garde applicative
- validation du numéro de page contre le PDF réel
- validation des coordonnées (valeurs finies)
- gestion centralisée des erreurs
- messages d’erreur maîtrisés sans stack trace exposée au client
- logs techniques sur étapes clés sans journaliser le contenu PDF

## Instructions de lancement

### Prérequis

- Java 21
- Maven 3.9+

### Commandes

```bash
mvn clean install
mvn spring-boot:run
```

Application : `http://localhost:8080`

## Prochaines évolutions prévues

- signature image manuscrite en plus du texte
- paramétrage police/couleur/taille
- multi-signatures / multi-pages par requête
- signature numérique cryptographique (PKCS#12)
- authentification/autorisation (JWT/OAuth2)
- audit trail des opérations
- tests unitaires/intégration renforcés
- dockerisation et CI/CD
