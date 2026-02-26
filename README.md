# TP2 → TP4 - Products API — Jakarta EE / JEE

## Auteur

De Souza Morais Gabriel

## Framework

**Jakarta EE 10** — WildFly (MicroProfile profile) + PostgreSQL 16

## Description

API REST Jakarta EE complète avec architecture 4 couches, persistence JPA, validation, gestion structurée des erreurs, pagination et documentation OpenAPI interactive.

- **Architecture** : 4 couches séparées (Presentation / Application / Domain / Infrastructure) avec interfaces Repository
- **5 entités JPA** : `Product`, `Category`, `Supplier`, `Order`, `OrderItem`
- **Relations** : `@ManyToOne`, `@OneToMany` bidirectionnelles avec `CASCADE` et `orphanRemoval`
- **Requêtes JPQL** optimisées avec `LEFT JOIN FETCH` et Entity Graphs (anti N+1)
- **Transactions** JTA via `@Transactional` CDI
- **Validation** : Bean Validation + contraintes custom (`@ValidSKU`, `@ValidPrice`, `@ValidDateRange`)
- **Exception Mappers** JAX-RS pour des réponses d'erreur structurées (400 / 404 / 409 / 500)
- **Pagination** : `GET /api/v1/products?page=0&size=10` avec métadonnées
- **Versioning** : Tous les endpoints sous `/api/v1`
- **Swagger UI** : Documentation interactive via MicroProfile OpenAPI (`/openapi-ui/`)

## Lancement rapide

```bash
# Démarrer PostgreSQL + WildFly (MicroProfile profile avec OpenAPI)
docker compose up --build

# Peupler la base avec des données de test
chmod +x seed.sh && ./seed.sh
```

| URL | Description |
|-----|-------------|
| `http://localhost:8080/api/v1` | Base de l'API (v1) |
| `http://localhost:8080/openapi` | Spec OpenAPI (JSON/YAML) |
| `http://localhost:8080/openapi-ui/` | Swagger UI interactif |

## Modèle de Données

```
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│   Category   │        │   Supplier   │        │    Order     │
│──────────────│        │──────────────│        │──────────────│
│ id (UUID)    │        │ id (UUID)    │        │ id (BIGINT)  │
│ name         │        │ name         │        │ orderNumber  │
│ description  │        │ email        │        │ customerName │
│ createdAt    │        │ phone        │        │ customerEmail│
└──────┬───────┘        │ createdAt    │        │ status       │
       │ 1              └──────┬───────┘        │ totalAmount  │
       │                       │ 1              │ orderDate    │
       │ *                     │ *              └──────┬───────┘
┌──────┴───────────────────────┴───┐                   │ 1
│             Product              │                   │
│──────────────────────────────────│                   │ *
│ id (UUID)                        │            ┌──────┴───────┐
│ name                             │            │  OrderItem   │
│ description                      │◄───────────│──────────────│
│ price                            │  *       1 │ id (BIGINT)  │
│ stockQuantity                    │            │ quantity     │
│ createdAt / updatedAt            │            │ unitPrice    │
│ category_id (FK)                 │            │ subtotal     │
│ supplier_id (FK)                 │            │ product_id   │
└──────────────────────────────────┘            │ order_id     │
                                                └──────────────┘
```

**Relations :**
- `Category` → `Product` : `@OneToMany` / `@ManyToOne`
- `Supplier` → `Product` : `@OneToMany` / `@ManyToOne`
- `Order` → `OrderItem` : `@OneToMany(cascade=ALL, orphanRemoval=true)` / `@ManyToOne`
- `OrderItem` → `Product` : `@ManyToOne`

**Statuts de commande :** `PENDING` → `CONFIRMED` → `SHIPPED` → `DELIVERED` | `CANCELLED`

## Endpoints

### Products (`/api/v1/products`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/v1/products` | Liste tous les produits (JOIN FETCH optimisé) |
| `GET` | `/api/v1/products?page=0&size=10` | **Pagination** — page courante + métadonnées |
| `GET` | `/api/v1/products?category={id}` | Filtre par catégorie |
| `GET` | `/api/v1/products/slow` | Liste sans JOIN FETCH (démo N+1) |
| `GET` | `/api/v1/products/fast` | Liste avec JOIN FETCH (solution N+1) |
| `GET` | `/api/v1/products/{id}` | Détail d'un produit |
| `GET` | `/api/v1/products/{id}/full` | Détail avec Entity Graph (category + supplier) |
| `POST` | `/api/v1/products` | Créer un produit |
| `PUT` | `/api/v1/products/{id}` | Mettre à jour un produit |
| `PATCH` | `/api/v1/products/{id}/stock` | Mettre à jour le stock |
| `DELETE` | `/api/v1/products/{id}` | Supprimer un produit |
| `GET` | `/api/v1/products/stats/count-by-category` | Nombre de produits par catégorie |
| `GET` | `/api/v1/products/stats/avg-price-by-category` | Prix moyen par catégorie |
| `GET` | `/api/v1/products/stats/top-expensive?limit=10` | Top N produits les plus chers |
| `GET` | `/api/v1/products/stats/never-ordered` | Produits jamais commandés |
| `GET` | `/api/v1/products/stats/category-stats` | Stats complètes par catégorie (DTO projeté) |
| `GET` | `/api/v1/products/stats/categories-with-min-products?min=1` | Catégories avec au moins N produits |

### Categories (`/api/v1/categories`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/v1/categories` | Liste toutes les catégories |
| `GET` | `/api/v1/categories/{id}` | Détail d'une catégorie |
| `POST` | `/api/v1/categories` | Créer une catégorie |
| `PUT` | `/api/v1/categories/{id}` | Mettre à jour une catégorie |
| `DELETE` | `/api/v1/categories/{id}` | Supprimer une catégorie |

### Suppliers (`/api/v1/suppliers`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/v1/suppliers` | Liste tous les fournisseurs |
| `GET` | `/api/v1/suppliers/{id}` | Détail d'un fournisseur |
| `POST` | `/api/v1/suppliers` | Créer un fournisseur |
| `PUT` | `/api/v1/suppliers/{id}` | Mettre à jour un fournisseur |
| `DELETE` | `/api/v1/suppliers/{id}` | Supprimer un fournisseur |

### Orders (`/api/v1/orders`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `POST` | `/api/v1/orders` | Créer une commande (avec items) |
| `GET` | `/api/v1/orders` | Liste toutes les commandes |
| `GET` | `/api/v1/orders?status={STATUS}` | Filtre par statut |
| `GET` | `/api/v1/orders?email={email}` | Filtre par email client |
| `GET` | `/api/v1/orders/{id}` | Détail d'une commande |
| `PUT` | `/api/v1/orders/{id}/status` | Mettre à jour le statut |
| `DELETE` | `/api/v1/orders/{id}` | Supprimer une commande |
| `GET` | `/api/v1/orders/stats/total-revenue` | Chiffre d'affaires total (DELIVERED) |
| `GET` | `/api/v1/orders/stats/count-by-status` | Nombre de commandes par statut |
| `GET` | `/api/v1/orders/stats/most-ordered-products?limit=5` | Produits les plus commandés |

## Validation Implémentée

### Contraintes Standards

| Entité | Champ | Contrainte |
|--------|-------|------------|
| `Product` | `name` | `@NotBlank`, `@Size(min=2, max=200)` |
| `Product` | `price` | `@NotNull`, `@DecimalMin("0.01")`, `@Digits(integer=8, fraction=2)` |
| `Product` | `stockQuantity` | `@NotNull`, `@Min(0)` |
| `Category` | `name` | `@NotBlank`, `@Size(min=2, max=100)` |
| `Order` | `customerEmail` | `@Email` |
| `OrderItem` | `quantity` | `@Min(1)` |

### Contraintes Custom

| Annotation | Règle |
|------------|-------|
| `@ValidSKU` | Format `ABC123` — 3 lettres majuscules + 3 chiffres |
| `@ValidPrice` | Maximum 2 décimales |
| `@ValidDateRange` | `deliveryDate >= orderDate` (validation cross-champs) |

## Gestion des Erreurs

### Exception Mappers JAX-RS (`mapper/`)

| Mapper | Exception | HTTP |
|--------|-----------|------|
| `ValidationExceptionMapper` | `ConstraintViolationException` | 400 + liste de champs |
| `NotFoundExceptionMapper` | `ProductNotFoundException` | 404 |
| `CategoryNotFoundExceptionMapper` | `CategoryNotFoundException` | 404 |
| `ConflictExceptionMapper` | `DuplicateProductException` | 409 |
| `CategoryNotEmptyExceptionMapper` | `CategoryNotEmptyException` | 409 |
| `InsufficientStockExceptionMapper` | `InsufficientStockException` | 400 |
| `GenericExceptionMapper` | `Exception` (fallback) | 500 |

> Le `GenericExceptionMapper` désencapsule les exceptions wrappées par le CDI `@Transactional` de WildFly avant de déléguer au mapper spécifique.

### Validations Métier

- **SKU dupliqué** — vérifié à la création et mise à jour d’un produit (`existsBySku`)
- **Stock insuffisant** — vérifié à la création d’une commande et via `decreaseStock`
- **Suppression de catégorie non vide** — la catégorie doit avoir 0 produits avant suppression

### Format de Réponse d’Erreur

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2026-02-26T08:00:00",
  "errors": [
    {
      "field": "price",
      "message": "Price must be at least 0.01",
      "rejectedValue": -10
    }
  ]
}
```

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with ID: abc-123",
  "timestamp": "2026-02-26T08:00:00"
}
```

## Captures d’écran

### TP2 — [`screens/tp2/`](screens/tp2/)

| Fichier | Description |
|---------|-------------|
| `1.png` | Tables PostgreSQL générées par Hibernate |
| `2.png` | Logs Hibernate (requêtes SQL formatées) |
| `3.png` | Démonstration du problème N+1 (`/products/slow`) |
| `4.0.png` | Requêtes d’agrégation |
| `4.1.png` | Statistiques catégories |
| `4.2.png` | Produits jamais commandés |
| `5.png` | Optimisation N+1 avec JOIN FETCH (`/products/fast`) |
| `6.0.png` — `6.8.png` | Tests Thunder Client — CRUD complet |

### TP3 — [`screens/tp3/`](screens/tp3/)

| Fichier | Description |
|---------|-------------|
| `1.png` | Validation 400 — `price: -1200` déclenche `@DecimalMin` → "Price must be at least 0.01" |
| `2.png` | Validation 400 — `price: 99.999` déclenche `@ValidPrice` + `@Digits` (2 erreurs simultanées) |
| `3.0.png` | 404 structuré — `GET /api/products/toto` → "Product not found with ID: toto" |
| `3.1.png` | 400 structuré — réponse JSON avec liste `errors[]` contenant champ, message et valeur rejetée |
| `4.png` | 409 structuré — `DELETE /api/categories/{id}` → "Cannot delete category ‘Électronique’ because it still contains products" |

## Tests Effectués

- [x] CRUD complet sur toutes les entités (Products, Categories, Suppliers, Orders)
- [x] Relations bidirectionnelles fonctionnelles
- [x] Transactions avec cascade (`CascadeType.ALL`, `orphanRemoval`)
- [x] Requêtes d’agrégation (COUNT, AVG, SUM, GROUP BY)
- [x] Démonstration du problème N+1 (`/products/slow` vs `/products/fast`)
- [x] Entity Graphs pour chargement sélectif (`Product.withCategory`, `Product.full`)
- [x] DTOs pour projections (`CategoryStats` via `SELECT NEW`)
- [x] Filtrage par statut et email sur les commandes
- [x] Mise à jour partielle du stock (`PATCH`)
- [x] Script de seed automatisé (`seed.sh`)
- [x] Validation échouée → 400 avec liste de champs invalides
- [x] Contraintes custom `@ValidSKU`, `@ValidPrice`, `@ValidDateRange` fonctionnelles
- [x] Produit non trouvé → 404 structuré
- [x] SKU dupliqué → 409 structuré
- [x] Catégorie avec produits → 409 à la suppression
- [x] Stock insuffisant → 400 avec message clair
- [x] Erreur inattendue → 500 générique
- [x] Pagination — `GET /api/v1/products?page=0&size=5` → réponse avec `data`, `totalElements`, `totalPages`
- [x] Versioning — tous les endpoints sous `/api/v1`
- [x] Swagger UI accessible à `http://localhost:8080/openapi-ui/`

## Difficultés Rencontrées

1. **Problème N+1**
Lors du chargement des produits, Hibernate faisait trop de requêtes SQL (une par relation).
Solution : utiliser JOIN FETCH dans les requêtes JPQL.

2. **Configuration WildFly / DataSource**
La connexion entre WildFly et PostgreSQL a demandé plusieurs ajustements (DataSource + Docker) pour garantir que la base soit prête avant le démarrage.

3. **Boucles JSON**
Les relations bidirectionnelles causaient des boucles infinies en JSON.
Solution : utiliser des DTOs au lieu d’exposer directement les entités.

4. **Exception wrapping CDI + JAX-RS**
Les exceptions lancées depuis des méthodes `@Transactional` CDI sont encapsulées par WildFly avant d’atteindre les mappers JAX-RS, les rendant invisibles aux mappers spécifiques.
Solution : le `GenericExceptionMapper` parcourt la chaîne de causes et délègue au mapper approprié.

## Points Clés Appris

1. JOIN FETCH est essentiel pour éviter le problème N+1 en LAZY.

2. Les Entity Graphs permettent de choisir quelles relations charger selon le besoin.

3. Les DTOs projetés (SELECT NEW ...) sont plus efficaces que charger des entités complètes.

4. Les `@Provider` JAX-RS sont auto-découverts quand la classe `Application` est vide.

5. En Jakarta EE, les `RuntimeException` lancées depuis des beans CDI `@Transactional` peuvent être wrappées — le mapper générique doit désencapsuler la cause pour déléguer correctement.