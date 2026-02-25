# TP1 - API REST avec Architecture en Couches

Auteur : DE SOUZA MORAIS Gabriel

## Base URL

-   Jakarta : http://localhost:8080/api/products
-   Spring : http://localhost:8081/api/products

------------------------------------------------------------------------

## Routes Disponibles

### GET /products

Récupère tous les produits.\
Possibilité de filtrer par catégorie avec un query param.

Query Param optionnel : - `category` → filtre les produits par catégorie

Exemple :

    GET /products
    GET /products?category=electronics

------------------------------------------------------------------------

### GET /products/{id}

Récupère un produit par son identifiant.

Réponses : - `200 OK` → Produit trouvé - `404 Not Found` → Produit non
trouvé

------------------------------------------------------------------------

### POST /products

Crée un nouveau produit.

Body : - `CreateProductDto`

Réponses : - `201 Created` → Produit créé - Retourne le produit créé +
header `Location`

------------------------------------------------------------------------

### PUT /products/{id}

Met à jour complètement un produit existant.

Body : - `CreateProductDto`

Réponses : - `200 OK` → Produit mis à jour - `404 Not Found` → Produit
non trouvé

------------------------------------------------------------------------

### PATCH /products/{id}/stock

Met à jour uniquement la quantité en stock.

Body :

``` json
{
  "quantity": 10
}
```

Réponses : - `200 OK` → Stock mis à jour - `404 Not Found` → Produit non
trouvé

------------------------------------------------------------------------

### DELETE /products/{id}

Supprime un produit.

Réponses : - `204 No Content` → Suppression réussie

------------------------------------------------------------------------

## Architecture

L'API suit une architecture en couches :

-   Controller → Gestion des requêtes HTTP
-   Service → Logique métier
-   DTOs → Objets de transfert de données
-   Base de données → PostgreSQL (lancée via Docker)

------------------------------------------------------------------------

## Lancement du projet

À la racine du projet :

``` bash
docker compose up -d
```

-   Jakarta → Port 8080\
-   Spring → Port 8081\
-   PostgreSQL → lancé automatiquement