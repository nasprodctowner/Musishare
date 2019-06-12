# Musishare

**Musishare**, Ajoutez une mélodie à vos rencontre !

## Qu'est ce que c'est ?

Musishare est une application de rencontre qui te permet de savoir ce que 

## Fonctionnalités 

### Version 1.0 ALPHA :
  * Login et création de profil
  * Connexion au compte Spotify et récupérer l'état du lecteur
  * Partage de la localisation
  * Système de like/dislike
  * Messagerie


## Système de Build
* [Gradle](https://gradle.org/)

## Installation

### Prérequis

Avant de pouvoir utiliser notre projet, il faut installer certains outils.

#### Installer Android Studio et le SDK

- Télécharger Android studio et l'installer : [Télécharger Android Studio](https://developer.android.com/studio/index.html)

- Télécharger le SDK : 

Depuis la fenêtres de bienvenue : 
  Configure > SDK Manager > SDK TOOLS > Cocher Android SDK Tools > OK
  
#### Installer Git

- Télécharger et installer Git : [Télécharger Git](https://gitforwindows.org/)

### Cloner Musishare

Une fois Git installé, il suffit de suivre les instructions ci-dessous.

#### Lier Git à Android Studio 

Depuis Android studio : 
  File > Settings > Version Control > Git > Dans " path to git Excecutable ", coller le chemin du git.exe > OK
  
#### Cloner le répertoire Musishare

Depuis Android studio : 
  VCS > Checkout from version control > Git > Dans " Git Repository URL ", coller https://github.com/nassimkhatir/Musishare.git > OK
  
####  Créer un compte Spotify Developer :


 * pour récupérer le Client ID et le remplacer au niveau du PlayerActivity 
 ```java
public class PlayerActivity extends Activity {
    private static final String CLIENT_ID = "coller ici";
    private static final String REDIRECT_URI = "http://fr.nashani.musishare/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    ...
```
 * Lier l'application au projet (android studio) en reportant le sha1 au niveau du dashboard.
 
 ![alt text](https://cdn-images-1.medium.com/max/1600/1*8c7agz6nxmez9-bm2NFCxQ.jpeg)
 
 ####  Créer un compte firebase : 
 
Suivre les instructions : https://firebase.google.com/docs/android/setup 

Ne pas oublier d'ajouter le sha1 aussi.
 
 C'est parti ! 

## Auteurs
* **KHATIR Nassim** - [Github](https://github.com/nassimkhatir)
* **ABDULWAHAB Hani - [Github](https://github.com/HaniAbd)
