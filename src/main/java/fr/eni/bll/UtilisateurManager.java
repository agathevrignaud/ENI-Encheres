package fr.eni.bll;

import fr.eni.bo.Utilisateur;
import fr.eni.dal.DAOFactory;
import fr.eni.dal.UtilisateurDAO;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurManager {
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurManager() {
        utilisateurDAO = DAOFactory.getUtilisateurDAO();
    }

    public List<Utilisateur> getAllUsers() {
        return utilisateurDAO.selectAll();
    }

    public Utilisateur getUserById(int userId) {
        return utilisateurDAO.selectById(userId);
    }

    public void addNewUser(String pseudo, String nom, String prenom, String email, String telephone, String rue, String codePostal, String ville, String motDePasse, String motDePasseConfirmation) throws Exception {
        if (isUserInfoValid(pseudo, email, motDePasse, motDePasseConfirmation)) {
            Utilisateur lUtilisateur = new Utilisateur(pseudo, nom, prenom, email, telephone, rue, codePostal, ville, motDePasse);
            utilisateurDAO.createUser(lUtilisateur);
        } else {
            System.out.println("Erreur lors de l'ajout de l'utilisateur");
            throw new Exception();
        }
    }

    public boolean isUserInfoValid(String pseudo, String email, String mdp, String mdpConf) {
        return isPseudoAndEmailValid(pseudo, email) && isPasswordValid(mdp, mdpConf) ;
    }

    public boolean isPseudoAndEmailValid(String pseudo, String email) {
        boolean isValid = true ;
        List<Utilisateur> lesUtilisateurs = utilisateurDAO.selectAll();

    // TODO : ajouter un regex pour vérifier la validité de l'email

        // Uniquement des caractères alphanumériques
        String regexPatternPseudo = "^[a-zA-Z0-9]*$";
        if (!pseudo.matches(regexPatternPseudo)) {
            isValid = false ;
        }
        // pseudo + email uniques
        for (Utilisateur unUtilisateur : lesUtilisateurs) {
            if (unUtilisateur.getPseudo() == pseudo || unUtilisateur.getEmail() == email) {
                isValid = false ;
                break;
            }
        }

        return isValid;
    }

    public boolean isPasswordValid(String mdp, String mdpConf) {
        boolean isValid = true ;
        String regexPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,12}$";

        if (!mdp.matches(regexPattern)) {
            isValid = false ;
        }
        if (!mdp.equals(mdpConf)) {
            isValid = false ;
        }

        return isValid;
    }

    public void updateUserData(int userId, String pseudo, String nom, String prenom, String email, String telephone, String rue, String codePostal, String ville, String motDePasse, String motDePasseConfirmation) throws Exception {
        if (isUserInfoValid(pseudo, email, motDePasse, motDePasseConfirmation)) {
            Utilisateur lUtilisateur = new Utilisateur(userId, pseudo, nom, prenom, email, telephone, rue, codePostal, ville, motDePasse);
            utilisateurDAO.updateUserData(lUtilisateur);
        } else {
            System.out.println("Erreur lors de la màj de l'utilisateur");
            throw new Exception();
        }
    }

    public void updateUserAccountStatus(int idUser) {
        utilisateurDAO.updateUserAccountStatus(idUser);
    }

    public void updateUserCredit(int creditSpent, int idUser) throws Exception {
        if ((utilisateurDAO.selectById(idUser).getCredit() - creditSpent) < 0) {
            throw new Exception();
        } else {
            utilisateurDAO.updateUserCredit(utilisateurDAO.selectById(idUser).getCredit() - creditSpent, idUser);
        }
    }

    public void deleteUser(int idUser) {
        utilisateurDAO.deleteUser(idUser);
    }

    public boolean authenticateUser(String login, String mdp) {
        boolean isAuthenticated = false ;
        List<Utilisateur> lesUtilisateurs = utilisateurDAO.selectAll();
        for (Utilisateur unUtilisateur : lesUtilisateurs) {
            if (unUtilisateur.getPseudo().equals(login) || unUtilisateur.getEmail().equals(login)) {
                isAuthenticated = (unUtilisateur.getMotDePasse().equals(mdp));
            }
        }
        return isAuthenticated;
    }
}
