# Pictura
A brand new application for you to keep the pictures you like the most.
---

Dans ce TP, j'ai réalisé une application Android sur l’ IDE Android Studio en utilisant le langage de programmation Kotlin. L'application avait pour objectif d'afficher des images provenant de l'API Unsplash et de permettre aux utilisateurs de les aimer pour les afficher dans une liste de favoris sur une autre page.

Pour récupérer les images à partir de l'API Unsplash, j'ai utilisé la bibliothèque Retrofit, qui permet de facilement effectuer des requêtes HTTP et de les parser en objets Java/Kotlin. Pour charger les images dans l'application j'ai utilisé Picasso, une bibliothèque populaire pour charger les images efficacement. (Glide est une autre alternative)

Pour l'affichage des images sur la première page, j'ai utilisé un RecyclerView pour afficher la liste des images et j'ai ajouté une popup de détails avec des boutons pour aimer ou ne pas aimer les images. Les images aimées ont été stockées dans la mémoire cache de l'application mobile avec l'aide de Room, une bibliothèque de persistance de données pour Android qui facilite l'utilisation de SQLite.
En utilisant Room, j'ai pu créer une base de données SQLite pour stocker les images aimées et les afficher dans une liste de favoris sur une autre page.

Enfin, j'ai testé mon application sur différents appareils et résolutions pour m'assurer qu'elle fonctionnait correctement et je l’ai optimisé pour une bonne performance.












Le schéma fonctionnel de l'application Android peut être décrit comme suit :

L'utilisateur ouvre l'application et est accueilli sur la première page qui affiche une liste d'images provenant de l'API Unsplash.

L'application utilise Retrofit pour faire une requête à l'API Unsplash et récupérer les images.

Picasso est utilisé pour charger les images récupérées dans l'interface utilisateur.

L'utilisateur peut ajouter des images à ses favoris en cliquant sur le bouton "J'aime" sur la popup de détails.

Les images aimées par l'utilisateur sont enregistrées dans la base de données Room de l'application.

L'utilisateur peut accéder à sa liste de favoris en cliquant sur un bouton de navigation.

La seconde page affiche la liste des images aimées par l'utilisateur en utilisant les données stockées dans la base de données Room.

L'application utilise le cache pour stocker les données et afficher rapidement la liste de favoris sans avoir à refaire une requête à l'API Unsplash à chaque fois.

L’utilisateur peut effectuer une recherche par mots clé sur une autre page qui fonctionne comme la page principale.

L'utilisateur peut quitter l'application à tout moment en faisant retour.

Librairies : 
Retrofit (HTML Requests)
Room (SQLite Database, CRUD, ORM)
Picasso (Images management)
Zoomage (Zoom for images)


Les images ont été téléchargées au format regular et compressées à 50% de leur qualité pour des raisons d’optimisation.

Le bearer token de l’API est stocké en local dans le fichier “local.properties” et n’est donc pas disponible sur le repository Github.

Les requêtes avec Retrofit sont toutes gérées pour informer l’utilisateur si jamais il y a une erreur de connexion réseau.

![](https://i.imgur.com/6ZUJpVV.jpg)

Capture 1: Activité page d’accueil

La page d’accueil affiche les 50 dernières images de la première page, si l’on fait défiler la liste, on passe aux pages suivantes avec un ajout des images à chaque fin de page grâce au addOnScrollListener associé à notre RecyclerView.
On peut refresh la liste en swipant vers le bas tout en haut de la liste pour la mettre à jour grâce au SwipeRefreshLayout.
Les images sont récupérées avec la librairie Retrofit et aucune donnée n’est stockée pour cet affichage.
L’utilisateur peut cliquer sur une image pour afficher les détails de celle-ci.

![](https://i.imgur.com/wPxbwOs.jpg)

Capture 2: Activité dialogue de détails

Sur cette activité, on retrouvera toutes les informations concernant notre image après avoir cliqué dessus sur la page principale.
Elle aura sa taille d’origine et on pourra zoomer sur celle-ci grâce à la librairie Zoomage.
On peut accéder au profil Instagram de l’auteur de l’image.
On peut aussi aimer l’image, l’image sera donc enregistrée à 50% de sa qualité en tant que Blob dans la base de données SQLite avec Room, ainsi que les autres informations visibles sur la page de détails.

![](https://i.imgur.com/BuHynCb.jpg)

Capture 3: Activité liste de favoris

L’activité ci-dessus permet à l’utilisateur de retrouver les images qu’il a aimé sur la page principale. 
Toutes les images affichées sont stockées sur une base de données SQLite à l’aide de la librairie Room.
A partir d’ici, il pourra cliquer sur une image pour afficher les détails de celle-ci qui sont aussi stockés en base.
L’utilisateur peut supprimer autant d'images qu’il le souhaite en appuyant sur le bouton en croix.

![](https://i.imgur.com/U1O5KSW.jpg)

Capture 4: Activité détails d’une image favorite

Cette activité affiche les détails d’une image favorite, on peut toujours accéder au compte Instagram.
On peut maintenant enregistrer cette photo sur le stockage local du téléphone Android.
On peut aussi zoomer sur l’image avec Zoomage sur cette activité.

![](https://i.imgur.com/fees9Pu.jpg)

Capture 5: Activité recherche d’images par mots clé

Cette dernière activité permet de faire des recherches par mots clé sur l’API Unsplash.
Les images qui correspondent à ces recherches sont retournées et affichées comme sur la page d’accueil et on peut interagir avec celle-ci.
Il faut bien appuyer sur la loupe dans la barre de recherche pour pouvoir saisir le texte.
La recherche s’effectue au clic sur le bouton “Entrer” et non à chaque saisie de touche pour éviter le spam de requêtes API.
La recherche est définie en français “fr” au lieu de l’anglais “en” par défaut.













Diagramme UML des objets du projet Pictura

![](https://i.imgur.com/IJEU3Kf.png)
