# TP2 - Persistence avec JPA

## Auteur

De Souza Morais Gabriel

## Description

API REST Jakarta EE avec persistence JPA complète déployée sur WildFly 30 + PostgreSQL 16.

- 5 entités JPA : `Product`, `Category`, `Supplier`, `Order`, `OrderItem`
- Relations complexes : `@ManyToOne`, `@OneToMany` (bidirectionnelles)
- Requêtes JPQL optimisées avec `JOIN FETCH` et `Entity Graphs`
- Transactions JTA gérées via `@Transactional`
- DTOs pour les projections (request / response)
- Statistiques d'agrégation (COUNT, AVG, SUM, GROUP BY)
- Démonstration du problème N+1 et sa résolution

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

## Lancement

```bash
# Démarrer PostgreSQL + WildFly
docker compose up --build

# Peupler la base avec des données de test
chmod +x seed.sh && ./seed.sh
```

L'API est disponible sur `http://localhost:8080/api`

## Endpoints

### Products (`/api/products`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/products` | Liste tous les produits (JOIN FETCH optimisé) |
| `GET` | `/api/products?category={name}` | Filtre par nom de catégorie |
| `GET` | `/api/products/slow` | Liste sans JOIN FETCH (démo N+1) |
| `GET` | `/api/products/fast` | Liste avec JOIN FETCH (solution N+1) |
| `GET` | `/api/products/{id}` | Détail d'un produit |
| `GET` | `/api/products/{id}/full` | Détail avec Entity Graph (category + supplier) |
| `POST` | `/api/products` | Créer un produit |
| `PUT` | `/api/products/{id}` | Mettre à jour un produit |
| `PATCH` | `/api/products/{id}/stock` | Mettre à jour le stock |
| `DELETE` | `/api/products/{id}` | Supprimer un produit |
| `GET` | `/api/products/stats/count-by-category` | Nombre de produits par catégorie |
| `GET` | `/api/products/stats/avg-price-by-category` | Prix moyen par catégorie |
| `GET` | `/api/products/stats/top-expensive?limit=10` | Top N produits les plus chers |
| `GET` | `/api/products/stats/never-ordered` | Produits jamais commandés |
| `GET` | `/api/products/stats/category-stats` | Stats complètes par catégorie (DTO projeté) |
| `GET` | `/api/products/stats/categories-with-min-products?min=1` | Catégories avec au moins N produits |

### Categories (`/api/categories`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/categories` | Liste toutes les catégories |
| `GET` | `/api/categories/{id}` | Détail d'une catégorie |
| `POST` | `/api/categories` | Créer une catégorie |
| `PUT` | `/api/categories/{id}` | Mettre à jour une catégorie |
| `DELETE` | `/api/categories/{id}` | Supprimer une catégorie |

### Suppliers (`/api/suppliers`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET` | `/api/suppliers` | Liste tous les fournisseurs |
| `GET` | `/api/suppliers/{id}` | Détail d'un fournisseur |
| `POST` | `/api/suppliers` | Créer un fournisseur |
| `PUT` | `/api/suppliers/{id}` | Mettre à jour un fournisseur |
| `DELETE` | `/api/suppliers/{id}` | Supprimer un fournisseur |

### Orders (`/api/orders`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `POST` | `/api/orders` | Créer une commande (avec items) |
| `GET` | `/api/orders` | Liste toutes les commandes |
| `GET` | `/api/orders?status={STATUS}` | Filtre par statut |
| `GET` | `/api/orders?email={email}` | Filtre par email client |
| `GET` | `/api/orders/{id}` | Détail d'une commande |
| `PUT` | `/api/orders/{id}/status` | Mettre à jour le statut |
| `DELETE` | `/api/orders/{id}` | Supprimer une commande |
| `GET` | `/api/orders/stats/total-revenue` | Chiffre d'affaires total (DELIVERED) |
| `GET` | `/api/orders/stats/count-by-status` | Nombre de commandes par statut |
| `GET` | `/api/orders/stats/most-ordered-products?limit=5` | Produits les plus commandés |

## Captures d'écran

Les captures sont disponibles dans le dossier [`screens/`](screens/) :

| Fichier | Description |
|---------|-------------|
| `1.png` | Tables PostgreSQL générées par Hibernate |
| `2.png` | Logs Hibernate (requêtes SQL formatées) |
| `3.png` | Démonstration du problème N+1 (`/products/slow`) |
| `4.0.png` | Requêtes d'agrégation |
| `4.1.png` | Statistiques catégories |
| `4.2.png` | Produits jamais commandés |
| `5.png` | Optimisation N+1 avec JOIN FETCH (`/products/fast`) |
| `6.x.png` | Tests Thunder Client — CRUD complet |

## Tests Effectués

- [x] CRUD complet sur toutes les entités (Products, Categories, Suppliers, Orders)
- [x] Relations bidirectionnelles fonctionnelles
- [x] Transactions avec cascade (`CascadeType.ALL`, `orphanRemoval`)
- [x] Requêtes d'agrégation (COUNT, AVG, SUM, GROUP BY)
- [x] Démonstration du problème N+1 (`/products/slow` vs `/products/fast`)
- [x] Entity Graphs pour chargement sélectif (`Product.withCategory`, `Product.full`)
- [x] DTOs pour projections (`CategoryStats` via `SELECT NEW`)
- [x] Filtrage par statut et email sur les commandes
- [x] Mise à jour partielle du stock (`PATCH`)
- [x] Script de seed automatisé (`seed.sh`)

Difficultés Rencontrées

1. **Problème N+1**
Lors du chargement des produits, Hibernate faisait trop de requêtes SQL (une par relation).
Solution : utiliser JOIN FETCH dans les requêtes JPQL.

2. **Configuration WildFly / DataSource**
La connexion entre WildFly et PostgreSQL a demandé plusieurs ajustements (DataSource + Docker) pour garantir que la base soit prête avant le démarrage.

3. **Boucles JSON**
Les relations bidirectionnelles causaient des boucles infinies en JSON.
Solution : utiliser des DTOs au lieu d’exposer directement les entités.

## Points Clés Appris

1. JOIN FETCH est essentiel pour éviter le problème N+1 en LAZY.

2. Les Entity Graphs permettent de choisir quelles relations charger selon le besoin.

3. Les DTOs projetés (SELECT NEW ...) sont plus efficaces que charger des entités complètes.