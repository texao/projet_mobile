var express = require("express");
const fs = require("fs");
const https = require("https");
const { MongoClient, ObjectID, ObjectId} = require("mongodb");

var app = express();
app.use(express.urlencoded({ extended: true }));
app.use(express.json())
app.use(function (req, res, next) {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
    res.setHeader('Access-Control-Allow-Headers', '*');
    next();
});


// Charger le certificat SSL/TLS
const options = {
    key: fs.readFileSync('/home/alex/private_key.pem'),
    cert: fs.readFileSync('/home/alex/public_key.crt'),
    secureProtocol: 'TLSv1_2_method',
    ciphers: 'ECDHE-RSA-AES256-GCM-SHA384'
};


// il faut d'abord lancer sur le terminal: mongod


// Créer une fonction asynchrone pour démarrer le serveur
async function startServer() {
    // Connexion à MongoDB
    const url = "mongodb://localhost:27017";
    const client = new MongoClient(url);

    try {
        await client.connect();
        console.log("Serveur connecté à MongoDB");

        const db = client.db("project_mobile");

        // Définition des routes
        app.get("/annonces", async (req, res) => {
            console.log("/annonces");
            let documents = await db.collection("Annonces").find().toArray();
            res.json(documents);
        });


        app.get("/offre", async (req, res) => {
            console.log("/offre");
            let documents = await db.collection("Offres_emploi").find().toArray();
            res.json(documents);
        });


        app.post("/offre_employe", async (req, res) => {
            console.log("/offre employe");

         
        let nomEntreprise = req.body.nomEntreprise;

        if (!nomEntreprise) {
            return res.status(400).json({ error: "Employee name is required" });
        }

        try {
            // Recherchez l'employé en fonction de son nom
            let employe = await db.collection("Employes").findOne({ NomEntreprise: nomEntreprise });

            if (!employe) {
                return res.status(404).json({ error: "Aucun employé trouvé avec ce nom" });
         }

         let employeurObjectId = new ObjectId(employe._id);

                // Récupérer les offres d'emploi associées à l'employé dans la base de données
                 let offres = await db.collection("Offres_emploi").find({ Employeur_ID: employeurObjectId }).toArray();
           
                 res.json(offres);
         } catch (error) {
             console.error("Error retrieving job offers:", error);
                res.status(500).json({ error: "Internal server error" });
        }
    });



        // Endpoint pour gérer l'inscription d'un nouvel utilisateur
        // inscription candidat
        app.post("/inscription", async (req, res) => {
            try {
                // Récupérer les données de l'utilisateur à partir du corps de la requête
                const { nom, prenom, email, dateNaissance, type } = req.body;

                // Créer un nouvel utilisateur dans la base de données avec le type "candidat"
                await db.collection("Utilisateurs").insertOne({
                    nom: nom,
                    prenom: prenom,
                    email: email,
                    type: type,
                    dateNaissance: dateNaissance
                });

                // Répondre avec un code de succès HTTP
                res.status(201).send("Utilisateur enregistré avec succès !");
            } catch (err) {
                console.error("Erreur lors de l'inscription :", err);
                res.status(500).send("Erreur lors de l'inscription, veuillez réessayer.");
            }
        });

        // inscription employe
        app.post("/inscription/employe", async (req, res) => {
            try {
                // Récupérer les données de l'utilisateur à partir du corps de la requête
                const { NomEntreprise, email, NumTelephone, Adresse, lienPublic } = req.body;

                // Créer un nouvel utilisateur dans la base de données avec le type "candidat"
                await db.collection("Employes").insertOne({
                    NomEntreprise: NomEntreprise,
                    email: email,
                    NumTelephone: NumTelephone,
                    Adresse: Adresse,
                    lienPublic: lienPublic
                });


                // Répondre avec un code de succès HTTP
                res.status(201).send("employe enregistré avec succès !");
            } catch (err) {
                console.error("Erreur inscription d'un employe :", err);
                res.status(500).send("Erreur inscription  d'un employe, veuillez réessayer.");
            }
        });


        app.post("/connexion", async (req, res) => {

            let name = req.body.nom;
            let email = req.body.email;

            // Rechercher l'utilisateur dans la base de données par email et type "Candidat"
            let user_candidat = await db.collection("Utilisateurs").findOne({ nom: name, email: email, type: 'Candidat' });

            // Vérifier si l'utilisateur candidat existe
            if (user_candidat) {
                // L'utilisateur est connecté en tant que candidat
                res.json({
                    "success": true,
                    "message": "Authentification réussie en tant que candidat",
                    "nom": user_candidat.nom,
                    "prenom": user_candidat.prenom,
                    "dateNaissance": user_candidat.dateNaissance
                });
            } else {
                // L'utilisateur n'est pas un candidat ou les informations d'identification sont incorrectes
                res.json({ "success": false, "message": "Nom ou email incorrect ou utilisateur non autorisé" });
            }
        });


        app.post("/connexion/employe", async (req, res) => {
            // Récupérer les données d'authentification de la requête

            let name = req.body.nom;
            let email = req.body.email;

            let user_candidat = await db.collection("Employes").findOne({ NomEntreprise: name, Email: email });

            // Vérifier si l'utilisateur candidat existe
            if (user_candidat) {
                // L'utilisateur est connecté en tant que candidat
                res.json({ "success": true, "message": "Authentification réussie en tant que candidat" });
            } else {
                // L'utilisateur n'est pas un candidat ou les informations d'identification sont incorrectes
                res.json({ "success": false, "message": "Nom ou email incorrect ou utilisateur non autorisé" });
            }
        });






        app.post("/candidature", async (req, res) => {
            // Récupérer les données de la requête
            let nom = req.body.nom;
            let prenom = req.body.prenom;
            let dateNaissance = req.body.dateNaissance;
            let nationalite = req.body.nationalite;
            let cv = req.body.cv;
            let statut = req.body.Statut;
            let dateCandidature = req.body.dateCandidature;
            let offreNom = req.body.offreNom;
            let metier = req.body.metier;


            // Rechercher l'utilisateur correspondant au nom et prénom dans la collection "Utilisateurs"
            let utilisateur = await db.collection("Utilisateurs").findOne({ nom: nom, prenom: prenom });

            if (utilisateur) {
                // Si l'utilisateur est trouvé, récupérer son ID
                let userId = utilisateur._id;

                // Insérer la candidature dans la base de données avec l'ID de l'utilisateur
                try {
                    await db.collection("Candidatures").insertOne({
                        userId: userId, // Ajouter l'ID de l'utilisateur
                        offreNom: offreNom,
                        nom: nom,
                        prenom: prenom,
                        dateNaissance: dateNaissance,
                        nationalite: nationalite,
                        CV: cv,
                        Statut: statut,
                        dateCandidature: dateCandidature,
                        metier: metier
                      
                    });
                    res.json({ "success": true, "message": "Candidature enregistrée avec succès" });
                } catch (error) {
                    console.error("Erreur lors de l'insertion de la candidature :", error);
                    res.status(500).json({ "success": false, "message": "Une erreur s'est produite lors de l'enregistrement de la candidature" });
                }
            } else {
                // Si l'utilisateur n'est pas trouvé, renvoyer un message d'erreur
                res.status(404).json({ "success": false, "message": "Utilisateur non trouvé pour le nom et prénom donnés" });
            }
        });







// Route pour afficher toutes les candidatures
app.get('/affichecandidature', async (req, res) => {

    let documents = await db.collection("Candidatures").find().toArray();
    res.json(documents);

    
});



// Route pour afficher toutes les candidatures pour l'employeur
app.post('/affichecandidatureEmployeur', async (req, res) => {

    // Récupérer le nom de l'entreprise depuis le corps de la requête
    const nomEntreprise = req.body.nomEntreprise;

    if (!nomEntreprise) {
        return res.status(400).send('Le nom de l\'entreprise est requis');
    }

    
    try {

        // Rechercher les candidatures avec le statut "En attente" pour l'entreprise spécifiée
        const candidatures = await db.collection('Candidatures').find({
            Statut: "En attente"
        }).toArray();

        res.status(200).json(candidatures);
    } catch (error) {
        console.error('Erreur lors de la récupération des candidatures :', error);
        res.status(500).send('Erreur lors de la récupération des candidatures');
    }
});



// Route pour afficher toutes les candidatures pour l'employeur
app.post('/affichecandidatureEmployeur_accepte', async (req, res) => {

    // Récupérer le nom de l'entreprise depuis le corps de la requête
    const nomEntreprise = req.body.nomEntreprise;

    if (!nomEntreprise) {
        return res.status(400).send('Le nom de l\'entreprise est requis');
    }

    
    try {

        const candidatures = await db.collection('Candidatures').find({
            Statut: "accepte"
        }).toArray();

        res.status(200).json(candidatures);
    } catch (error) {
        console.error('Erreur lors de la récupération des candidatures :', error);
        res.status(500).send('Erreur lors de la récupération des candidatures');
    }
});







// modifié candidature apres avoir accepte ou refuse

app.put("/candidature/:id/statut", async (req, res) => {
    const candidatureId = req.body._id;
    const statut  = req.body.statut;

   
    try {
        const candidature = await db.collection("Candidatures").findOne({ _id: new ObjectId(candidatureId) });

        if (!candidature) {
            return res.status(404).json({ success: false, message: "Candidature non trouvée" });
        }

            const result = await db.collection("Candidatures").updateOne(
                { _id: new ObjectId(candidatureId) },
                { $set: { Statut: statut } }
            );

            if (result.modifiedCount > 0) {
                res.status(200).json({ success: true, message: "Statut mis à jour avec succès" });
            } else {
                res.status(500).json({ success: false, message: "Erreur lors de la mise à jour du statut" });
            }
    
    } catch (error) {
        console.error("Erreur lors de la mise à jour du statut :", error);
        res.status(500).json({ success: false, message: "Une erreur s'est produite lors de la mise à jour du statut" });
    }
});





app.post("/depotOffre", async (req, res) => {
    try {
        

        let nom = req.body.nom;
        let metier = req.body.metier;
        let description = req.body.description;
        let periode = req.body.periode;
        let remuneration = req.body.remuneration;
        let nomEntreprise = req.body.nomEntreprise;
        let emailEntreprise = req.body.emailEntreprise;

        let employeur_id = await db.collection("Employes").findOne({ NomEntreprise: nomEntreprise, Email: emailEntreprise });

        // Créer un nouvel utilisateur dans la base de données avec le type "candidat"
        await db.collection("Offres_emploi").insertOne({
            nom: nom,
            metier: metier,
            Description: description,
            Periode: periode,
            Remuneration: remuneration,
            datePublication: new Date(),
            Employeur_ID: employeur_id._id

        });





        // Répondre avec un code de succès HTTP
        res.status(201).send("Utilisateur enregistré avec succès !");
    } catch (err) {
        console.error("Erreur lors de l'inscription :", err);
        res.status(500).send("Erreur lors de l'inscription, veuillez réessayer.");
    }
});








app.post("/supprimerOffre", async (req, res) => {
   
console.log("/supprimerOffre");

    try {
        const nomOffre = req.body.nom;
        const metierOffre = req.body.metier;
    
        const result = await db.collection('Offres_emploi').deleteOne({ nom: nomOffre, metier: metierOffre });

         // Vérifier si une offre a été supprimée
        if (result.deletedCount === 1) {
            res.status(200).send('Offre d\'emploi supprimée avec succès');
        } else {
            res.status(404).send('Aucune offre d\'emploi correspondante trouvée');
        }
    } catch (error) {
        console.error('Erreur lors de la suppression de l\'offre d\'emploi :', error);
        res.status(500).send('Erreur lors de la suppression de l\'offre d\'emploi');
    } finally {
        // Déconnexion du client MongoDB
        await client.close();
    }
});







// Route pour afficher toutes les candidatures pour l'employeur
app.post('/affichecandidatureEmployeur_accepte', async (req, res) => {

    // Récupérer le nom de l'entreprise depuis le corps de la requête
    const nomEntreprise = req.body.nomEntreprise;

    if (!nomEntreprise) {
        return res.status(400).send('Le nom de l\'entreprise est requis');
    }

    
    try {

        // Rechercher les candidatures avec le statut "En attente" pour l'entreprise spécifiée
        const candidatures = await db.collection('Candidatures').find({
            Statut: "accepte"
        }).toArray();

        res.status(200).json(candidatures);
    } catch (error) {
        console.error('Erreur lors de la récupération des candidatures :', error);
        res.status(500).send('Erreur lors de la récupération des candidatures');
    }
});









// Route pour afficher toutes les candidatures pour l'employeur
app.post('/get_user_email', async (req, res) => {


    
    try {
        // Rechercher les candidatures acceptées pour l'entreprise spécifiée
        const candidatures = await db.collection('Candidatures').find({
            Statut: "accepte"
        }).toArray();

        // Récupérer l'email de l'utilisateur associé à chaque candidature acceptée
        const candidaturesWithEmail = await Promise.all(candidatures.map(async (candidature) => {
            // Récupérer l'userId de la candidature
            const userId = candidature.userId;

            // Rechercher l'utilisateur dans la collection "Utilisateur" avec cet userId
            const utilisateur = await db.collection('Utilisateurs').findOne({
                _id: new ObjectId(userId)
            });

          
            // Si l'utilisateur est trouvé, renvoyer son email
            if (utilisateur && utilisateur.email) {
                return utilisateur.email;
            }

            return null;
        }));

        res.status(200).json(candidaturesWithEmail);
    } catch (error) {
        console.error('Erreur lors de la récupération des candidatures :', error);
        res.status(500).send('Erreur lors de la récupération des candidatures');
    }
});







        // Démarrer le serveur HTTPS
        const server = https.createServer(options, app);
        server.listen(8888, () => {
            console.log("Serveur démarré en mode HTTPS sur le port 8888");
        });
    } catch (error) {
        console.error("Erreur lors de la connexion à MongoDB:", error);
    }





}

// Appel de la fonction pour démarrer le serveur
startServer();








