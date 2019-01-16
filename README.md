# ChatThePhoqueApp

## Cahier des Charges

Réalisation d’une application de messagerie instantanée grâce à l’API Google Cloud Messaging.

### Tâches :
-	Ecran d’accueil : Liste des différentes conversations + FloatingButton pour accéder à d’autres écran (Nouvelle conversation => Choix d’un contact).
-	Click sur une conversation => Ouverture de la conversation dans un nouvel écran
-	Les conversations sont triées par ordre décroissant du dernier message reçu. Lorsqu’une conversation est affichée on peut swiper de droite à gauche pour afficher la conversation suivante grâce à des Fragments.
-	Les messages sont envoyés et reçus grâce à l’API Cloud Messaging
-	Les utilisateurs seront sauvegardés sur Firebase, ainsi que les messages associés.
-	L’utilisateur peut localement customiser l’application en modifiant la police ou la couleur des conversations.
-	AppWidget permettant d’afficher les nouveaux messages (et d'y répondre)
