# Rapport de Projet — Products API

## 1. Présentation

Cette API REST permet de gérer un catalogue de produits avec leurs catégories, fournisseurs et commandes. Elle expose des opérations CRUD complètes, de la validation, une gestion structurée des erreurs, de la pagination et une documentation OpenAPI interactive.

Le projet a été réalisé dans le cadre des TP1 à TP4 du cours Jakarta EE / JEE. Il s'agit d'un backend métier représentatif d'une application de gestion de stock e-commerce.

Les entités principales sont `Product`, `Category`, `Supplier`, `Order` et `OrderItem`, reliées par des relations JPA bidirectionnelles. L'API respecte les principes REST et renvoie des réponses JSON structurées, y compris pour les erreurs.

## 2. Architecture Technique

### 2.1 Framework Utilisé

**Jakarta EE 10** déployé sur **WildFly** (conteneur Jakarta EE certifié) avec **PostgreSQL 16** comme base de données relationnelle, le tout orchestré via **Docker Compose**.

Jakarta EE a été choisi car il couvre nativement tous les besoins du projet : JAX-RS pour les endpoints REST, JPA/Hibernate pour la persistence, CDI pour l'injection de dépendances, Bean Validation pour les contraintes, et MicroProfile pour OpenAPI.

### 2.2 Couches de l'Application

| Couche | Package | Rôle |
|--------|---------|------|
| **Presentation** | `controller/` | JAX-RS Resources — reçoit les requêtes HTTP, valide avec `@Valid`, retourne les réponses JSON |
| **Application** | `service/` | Services métier — orchestrent la logique, gèrent les transactions `@Transactional` |
| **Domain** | `model/` | Entités JPA — représentent le modèle métier avec annotations de persistance et de validation |
| **Infrastructure** | `repository/` | Repositories JPA — accès à la base de données via EntityManager |

Les services dépendent d'interfaces (`IProductRepository`, `ICategoryRepository`, etc.) et non des implémentations concrètes — respect du **Dependency Inversion Principle** (SOLID).

### 2.3 Technologies Utilisées

- **Jakarta EE 10** (JAX-RS, JPA, CDI, Bean Validation)
- **WildFly** (serveur d'application Jakarta EE certifié)
- **MicroProfile OpenAPI 3.1** (documentation interactive)
- **Hibernate 6** (implémentation JPA)
- **PostgreSQL 16** (base de données relationnelle)
- **Docker / Docker Compose** (conteneurisation)

### 2.4 Modèle de Données

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
│ sku                              │            │ unitPrice    │
│ createdAt / updatedAt            │            │ subtotal     │
│ category_id (FK)                 │            │ product_id   │
│ supplier_id (FK)                 │            │ order_id     │
└──────────────────────────────────┘            └──────────────┘
```

**Relations JPA :**
- `Category` → `Product` : `@OneToMany` / `@ManyToOne`
- `Supplier` → `Product` : `@OneToMany` / `@ManyToOne`
- `Order` → `OrderItem` : `@OneToMany(cascade=ALL, orphanRemoval=true)` / `@ManyToOne`
- `OrderItem` → `Product` : `@ManyToOne`

## 3. Fonctionnalités Implémentées

### 3.1 CRUD

CRUD complet sur les 4 ressources principales :

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST`   | `/api/v1/products`          | Créer un produit |
| `GET`    | `/api/v1/products`          | Lister tous les produits |
| `GET`    | `/api/v1/products?page=0&size=10` | Lister avec pagination |
| `GET`    | `/api/v1/products/{id}`     | Détail d'un produit |
| `PUT`    | `/api/v1/products/{id}`     | Mettre à jour un produit |
| `DELETE` | `/api/v1/products/{id}`     | Supprimer un produit |
| `POST`   | `/api/v1/categories`        | Créer une catégorie |
| `GET`    | `/api/v1/categories`        | Lister toutes les catégories |
| `POST`   | `/api/v1/suppliers`         | Créer un fournisseur |
| `POST`   | `/api/v1/orders`            | Créer une commande (avec items) |

### 3.2 Validation

Validation déclarative via **Bean Validation** sur les entités et les DTOs :

| Annotation | Entité / Champ | Règle |
|------------|----------------|-------|
| `@NotBlank` + `@Size` | `Product.name` | Requis, 2–200 caractères |
| `@NotNull` + `@DecimalMin` | `Product.price` | Requis, ≥ 0.01 |
| `@Min(0)` | `Product.stockQuantity` | Quantité non négative |
| `@Email` | `Order.customerEmail` | Format email valide |
| `@ValidSKU` (custom) | `Product.sku` | Format `ABC123` — 3 maj + 3 chiffres |
| `@ValidPrice` (custom) | `Product.price` | Maximum 2 décimales |
| `@ValidDateRange` (custom) | `Order` | `deliveryDate >= orderDate` |

### 3.3 Gestion des Erreurs

Chaque type d'erreur est intercepté par un **Exception Mapper JAX-RS** (`@Provider`) et retourne un JSON structuré :

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2026-02-26T10:00:00",
  "errors": [
    { "field": "price", "message": "Price must be at least 0.01", "rejectedValue": -5 }
  ]
}
```

| Mapper | HTTP | Déclencheur |
|--------|------|-------------|
| `ValidationExceptionMapper` | 400 | Contrainte Bean Validation violée |
| `InsufficientStockExceptionMapper` | 400 | Stock insuffisant pour la commande |
| `NotFoundExceptionMapper` | 404 | Produit inexistant |
| `CategoryNotFoundExceptionMapper` | 404 | Catégorie inexistante |
| `ConflictExceptionMapper` | 409 | SKU dupliqué |
| `CategoryNotEmptyExceptionMapper` | 409 | Suppression d'une catégorie non vide |
| `GenericExceptionMapper` | 500 | Fallback — désencapsule les exceptions CDI |

### 3.4 Fonctionnalités Avancées

**Pagination** — `GET /api/v1/products?page=0&size=10` retourne :
```json
{
  "data": [...],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5
}
```

**Versioning d'API** — Tous les endpoints sont préfixés `/api/v1` via `@ApplicationPath("/api/v1")`.

**Documentation OpenAPI / Swagger** — WildFly MicroProfile OpenAPI génère automatiquement :
- Spec OpenAPI : `http://localhost:8080/openapi`
- Swagger UI : `http://localhost:8080/openapi-ui/`

Les annotations `@OpenAPIDefinition`, `@Tag`, `@Operation` et `@APIResponse` enrichissent la documentation.

**Optimisation N+1** — Les requêtes JPQL utilisent `LEFT JOIN FETCH` pour charger les relations en une seule requête SQL. Les Entity Graphs (`Product.withCategory`, `Product.full`) permettent un chargement sélectif selon le besoin.

**Statistiques d'agrégation** — Endpoints `/stats/` : COUNT, AVG, GROUP BY, produits jamais commandés, top N plus chers.

## 4. Difficultés Rencontrées et Solutions

### Difficulté 1 : Problème N+1 avec Hibernate

**Problème** : En chargement LAZY par défaut, chaque accès à `product.getCategory()` ou `product.getSupplier()` dans la boucle de conversion DTO déclenchait une requête SQL supplémentaire. Avec 8 produits, cela donnait 17+ requêtes au lieu de 1.

**Solution** : Remplacement de `SELECT p FROM Product p` par `SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier`. Les Entity Graphs ont été ajoutés pour les cas nécessitant un contrôle plus fin du chargement.

### Difficulté 2 : Configuration WildFly + PostgreSQL sous Docker

**Problème** : WildFly démarrait avant que PostgreSQL ne soit prêt, provoquant des erreurs de datasource. Par ailleurs, la configuration du driver JDBC PostgreSQL dans WildFly nécessite l'installation d'un module JBoss spécifique.

**Solution** : Ajout d'un `healthcheck` PostgreSQL dans Docker Compose avec `depends_on condition: service_healthy`. Le driver PostgreSQL est installé comme module WildFly via un script CLI (`wildfly-postgresql-setup.cli`) exécuté pendant le build Docker.

### Difficulté 3 : Boucles JSON avec les relations bidirectionnelles

**Problème** : Les entités JPA avec relations bidirectionnelles (`Category.products` ↔ `Product.category`) causaient des boucles infinies lors de la sérialisation JSON par Jackson/JSON-B.

**Solution** : Utilisation systématique de DTOs (classes `GetProductDto`, `GetCategoryDto`, etc.) pour n'exposer que les champs nécessaires. Les entités JPA ne sont jamais retournées directement dans les réponses.

### Difficulté 4 : Exception wrapping CDI + JAX-RS

**Problème** : Les exceptions métier (`ProductNotFoundException`, `DuplicateProductException`) lancées depuis des méthodes `@Transactional` CDI étaient encapsulées par WildFly dans des `EJBException` ou `RuntimeException`, rendant les mappers JAX-RS spécifiques inopérants.

**Solution** : Le `GenericExceptionMapper` parcourt récursivement la chaîne `getCause()` pour retrouver l'exception métier d'origine et délégue au mapper approprié en relançant l'exception désencapsulée.

## 5. Points d'Amélioration

- **Sécurité JWT** : Ajouter une authentification JWT avec MicroProfile JWT pour protéger les endpoints en écriture.
- **Cache HTTP** : Utiliser les headers `Cache-Control` et `ETag` sur les endpoints GET pour réduire la charge serveur.
- **Tests unitaires** : Ajouter des tests Arquillian ou Mockito pour les services et repositories.
- **Pagination sur d'autres ressources** : Étendre la pagination aux endpoints `/categories` et `/orders`.
- **Rate Limiting** : Implémenter une limite de requêtes par IP via un filtre JAX-RS.

## 6. Conclusion

Ce projet m'a permis d'acquérir une maîtrise concrète de l'écosystème Jakarta EE dans un contexte professionnel réaliste. Les points les plus formateurs ont été :

1. **L'architecture en couches avec interfaces** — La séparation claire entre présentation, logique métier et infrastructure, combinée à l'injection CDI, permet une application facilement maintenable et testable.

2. **JPA et ses subtilités** — Comprendre le chargement LAZY/EAGER, le problème N+1 et les Entity Graphs est essentiel pour écrire des applications JPA performantes.

3. **La gestion d'erreurs structurée** — Les Exception Mappers JAX-RS permettent une réponse cohérente pour tous les types d'erreurs sans dupliquer de code dans les controllers.
