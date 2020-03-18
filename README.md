# ExerciceExplorer
ExerciceExplorer est un logiciel écrit en java swing permettant d'éditer un fichier tex rassamblant plusieurs exercices présent sur une base de donnée partagée (sous git)

## Installation
L'installation nécéssite d'être familier avec le terminal (testée sous DEBIAN et OSX) 
Reccupérez le projet : 
```bash
git clone git@github.com:mbrebion/exerciceExplorer.git
```

Une fois le projet téléchargé/cloné, vous pouvez  : 
 - l'ouvrir à l'aide de Netbeans (ouvrir projet/open project) et l'executer depuis l'IDE
 - l'executer (le fichier jar se trouve dans le sous-dossier dist) en ligne de commande : 
 ```bash
 java -jar ExerciceExplorer.jar 
 ```

 - l'executer en double-cliquant sur le fichier ExerciceExplorer.jar.

## Première utilisation
Pour utiliser ce logiciel dans les meilleurs conditions, vous devez disposer d'un compte [github](https://github.com/) puis me contacter afin que je puisse vous ajouter à la base de donnée (cette dernière étant en accès privé). Vous pourrez ensuite effectuer les quelques réglagles nécéssaires au bon fonctionnement du logiciel ExerciceExplorer.

### Réglage git sur machine locale

Sur votre machine vous devez parametrer git (une fois installé si ce n'est pas encore le cas)
```bash
git config --global user.email "you@example.com"
git config --global user.name "Your Name"
```
ces informations seront incluses lorsque vous effectuerez une mise à jour de la base de donnée (afin d'ajouter un peu de tracabilité)

### Réglage github (accès via clé rsa)
Si vous ne disposez pas encore d'une clé publique (absence de fichier ~/.ssh/id_rsa.pub), vous pouvez en créer une à l'aide de la commande
 ```bash
ssh-keygen -t rsa
```
de mon côté, je donne les réponses par défaut aux questions posées.

Une fois la paire de clé créée, il faut suivre le tutoriel suivant disponible [ici](https://help.github.com/en/github/authenticating-to-github/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)

Finalement, vous pouvez reccuperer la base de donnée (environ 200 mo en mars 2018)
```bash
git clone git@github.com:mabuchet/commun_PCSI.git
```
Attention, cette étape ne pourra être réalisée qu'après m'avoir contacté et donné votre login sous github afin que je puisse vous autoriser l'accès à la base de donnée d'exercices.

### Spécification des chemins d'accès
Une fois git & github correctement paramétrés, vous pouvez aller dans l'onglet "Options" du logiciel puis remplir les différents champs requis
- chemins d'accès vers des exécutables (pdflatex, ouverture d'un fichier avec le logiciel par défaut)
- Le dossier git est le dossier "commun_PCSI" qui contient les fichiers d'exercices
- Le dossier modèles doit contenir les fichiers DMmodel.tex, DSmodel.tex, COLLEmodel.tex et TDmodel.tex qui seront utilisés comme base par le logiciel lors de l'édition d'un sujet. Une implémentation par défaut de ces fichiers est disponible.



## Site web
Plus d'info sur la page suivante : 
https://mmb.netlify.com/exerciceexplorer/exerciceexplorer

