<?php
include_once RACINE . '/classes/Etudiant.php';
include_once RACINE . '/connexion/Connexion.php';
include_once RACINE . '/dao/IDao.php';

class EtudiantService implements IDao {
    private $connexion;

    function __construct() {
        $this->connexion = new Connexion();
    }

    public function create($o) {
        $query = "INSERT INTO Etudiant (nom, prenom, ville, sexe) 
                  VALUES (:nom, :prenom, :ville, :sexe)";
         $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([
            ':nom' => $o->getNom(),
            ':prenom' => $o->getPrenom(),
            ':ville' => $o->getVille(),
            ':sexe' => $o->getSexe()
        ]) or die('Erreur SQL (create)');
    }

    public function delete($o) {
        $query = "DELETE FROM Etudiant WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([':id' => $o->getId()]) or die('Erreur SQL (delete)');
    }

    public function findAll() {
        $etds = array();
        $query = "SELECT * FROM Etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $etds[] = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe);
        }
        return $etds;
    }

    public function findById($id) {
        $query = "SELECT * FROM Etudiant WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([':id' => $id]);
        if ($e = $req->fetch(PDO::FETCH_OBJ)) {
            return new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe);
        }
        return null;
    }

    public function update($o) {
        $query = "UPDATE Etudiant 
                  SET nom = :nom, prenom = :prenom, ville = :ville, sexe = :sexe 
                  WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([
            ':nom' => $o->getNom(),
            ':prenom' => $o->getPrenom(),
            ':ville' => $o->getVille(),
            ':sexe' => $o->getSexe(),
            ':id' => $o->getId()
        ]) or die('Erreur SQL (update)');
    }

    public function findAllApi() {
        $query = "SELECT * FROM Etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>
