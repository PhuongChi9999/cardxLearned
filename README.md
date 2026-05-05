
    1. Dans la page app.ts/app.html : ajouter un bouton "Ajouter un dossier" qui affiche une nouvelle page qui permet de saisir les informations d'un dossier et un bouton "Créer", clickquer sur "Créer" va créer un dossier dans la DB
    2. Modifier la page dossiers pour afficher les dossiers dans un tableau, le nom des dossier sont cliquable
    3. Cliquer sur le nom d'un dossier va ouvrir une page qui affiche les détails d'un dossier, en bas de la page, un box pour sélectionner un ficher puis un bouton "Envoyer un document"
    4. Cliquer sur le bouton va envoyer le fichier choisi au backend pour analyser, puis afficher le résultat reçu


Pour tester, dans le répertoire cardxLearned :
- docker compose up

Pour arrêter fontend,backend et db:
- docker compose down

Pour recompiler:
- docker compose build --no-cache

TODO:
- SharpAPI retourne cette réponse après un envoi de fichier
Object { status_url: "https://apyhub.com/services/provider/sharpapi/api/v1/hr/parse_resume/job/status/4e465d78-5ef9-444b-abe9-b2cedf0486c4", job_id: "4e465d78-5ef9-444b-abe9-b2cedf0486c4" }
​- Il faut appeller <status_url> avec <job_id> pour interoger le statut de la tâche, l'analyse du document finit quand on a cette réponse
{
  "status": "completed",
  "data": {...}
}

Les données dans le champs "data" sont à afficher dans l'écran (message)
- Documentation
https://apyhub.com/utility/sharpapi-ai-resume-parser