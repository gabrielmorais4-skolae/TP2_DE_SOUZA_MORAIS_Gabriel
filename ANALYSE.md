# Analyse Comparative : Jakarta EE vs Spring Boot  

### Configuration
Avec Jakarta EE, on utilise plusieurs fichiers XML comme `beans.xml`, `web.xml`, `persistence.xml`, en plus des annotations
Avec Spring Boot, presque toute la configuration est dans `application.properties` avec des annotations
Spring est donc plus simple car tout est centralisé

### Annotations
Jakarta utilise des annotations standards comme `@Inject`, `@Path`, `@GET`
Spring utilise ses propres annotations comme `@Service`, `@RestController`, `@GetMapping`
Les deux fonctionnent de manière similaire mais avec des annotations différentes

### Démarrage
Avec Jakarta, il faut un serveur externe comme <entity type="organization" name="Payara" disambiguation="jakarta ee app server"></entity> déjà démarré
Avec Spring Boot, le serveur est embarqué (souvent <entity type="organization" name="Apache Tomcat" disambiguation="java servlet container"></entity>) et il suffit d’exécuter la classe `main` 
Spring Boot est donc plus rapide à lancer

### Packaging
Jakarta génère généralement un fichier WAR à déployer sur un serveur
Spring Boot génère un fichier JAR autonome avec le serveur inclus

### Hot Reload
Avec Jakarta, il faut souvent redéployer l’application manuellement
Avec Spring Boot et `spring-boot-devtools`, l’application redémarre automatiquement en cas de modification

### Conclusion
Spring Boot est plus simple pour démarrer rapidement un projet
Jakarta est plus traditionnel et souvent utilisé dans des environnements d’entreprise

### Architecture

Oui, les deux applications ont la même structure en couches :

- Controller : reçoit les requêtes HTTP
- Service : contient la logique métier 
- Repository : gère l’accès aux données
- Model / DTO : représente les données

Cette organisation est une bonne pratique indépendante du framework

### Dépendances

Dans `ProductService`, on injecte une interface (`IProductRepository`) et non une classe concrète

Cela permet de changer l’implémentation sans modifier le service
Par exemple, on peut remplacer `InMemoryProductRepository` par `JpaProductRepository` sans changer `ProductService`

Cela respecte le principe DIP (Dependency Inversion Principle)

## SOLID

### SRP
Chaque classe a une seule responsabilité :
- Controller : gestion HTTP
- Service : logique métier
- Repository : persistance

### OCP  
On peut ajouter une nouvelle implémentation du repository sans modifier le service

### LSP  
Si deux classes implémentent la même interface, on peut les remplacer l’une par l’autre sans problème

### ISP
L’interface contient uniquement les méthodes nécessaires

### DIP
Le service dépend d’une interface et non d’une classe concrète

## Tests

Pour tester `ProductService` sans base de données, on utilise un mock de `IProductRepository`

On simule le comportement du repository
Ainsi, on teste uniquement la logique métier sans utiliser une vraie base de données

## Évolution (ajout de JPA)

### À créer :
- `JpaProductRepository`
- La configuration de la base de données (`application.properties` ou `persistence.xml`)

### À modifier :
- La classe `Product` pour ajouter les annotations JPA comme `@Entity`, `@Id`

### À ne pas modifier :
- `ProductService`
- `ProductController`
- `IProductRepository`
- Les DTO

Car le service dépend uniquement de l’interface