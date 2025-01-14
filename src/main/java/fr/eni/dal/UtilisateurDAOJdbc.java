package fr.eni.dal;

import fr.eni.bll.BLLException;
import fr.eni.bo.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilisateurDAOJdbc implements UtilisateurDAO {
    private static final Logger myLogger = Logger.getLogger("LogsDAL_Utilisateur");

    private static final String SELECT_ALL_USERS = "SELECT * FROM UTILISATEURS";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM UTILISATEURS WHERE NO_UTILISATEUR=?";
    private static final String INSERT_USER = "INSERT INTO UTILISATEURS VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_USER_DATA = "UPDATE UTILISATEURS SET " +
            "pseudo=?, nom=?, prenom=?, email=?, telephone=?, rue=?, code_postal=?, ville=?, mot_de_passe=? " +
            "WHERE no_utilisateur=?";
    private static final String UPDATE_USER_ACCOUNT_STATUS = "UPDATE UTILISATEURS SET compteActif=? WHERE no_utilisateur=?";
    private static final String UPDATE_USER_CREDIT = "UPDATE UTILISATEURS SET credit=? WHERE no_utilisateur=?";
    private static final String DELETE_USER = "DELETE FROM UTILISATEURS WHERE no_utilisateur=?";
    private static final String CHECK_USER_EXISTENCE = "SELECT * FROM UTILISATEURS WHERE pseudo=? AND email=?";
    private static final String RESET_PASSWORD = "UPDATE UTILISATEURS SET mot_de_passe=? WHERE no_utilisateur=?";

    @Override
    public List<Utilisateur> selectAll() throws BLLException {
        List<Utilisateur> lesUtilisateurs = new ArrayList<>();
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_USERS);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Utilisateur lUtilisateur = getUtilisateur(rs);
                lesUtilisateurs.add(lUtilisateur);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_SELECT_ALL_USERS);
            myLogger.log(Level.WARNING,"Erreur lors de la sélection des utilisateurs", bllException);
            throw bllException;
        }
        return lesUtilisateurs;
    }

    @Override
    public Utilisateur selectById(int idUser) throws BLLException {
        Utilisateur lUtilisateur = null ;
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_USER_BY_ID);
            pstmt.setInt(1, idUser);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                lUtilisateur = getUtilisateur(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_SELECT_USER_BY_ID);
            myLogger.log(Level.WARNING,"Erreur lors de la sélection de l'utilisateur (idUser : " + idUser +")", bllException);
            throw bllException;
        }
        return lUtilisateur;
    }

    @Override
    public Utilisateur createUser(Utilisateur lUtilisateur) throws BLLException {
        BLLException bllException = new BLLException();
        if (lUtilisateur == null) {
            bllException.ajouterErreur(CodesResultatDAL.ERROR_CREATE_USER);
            throw bllException;
        } else {
            try (Connection cnx = ConnectionProvider.getConnection()) {
                PreparedStatement pstmt = cnx.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, lUtilisateur.getPseudo());
                pstmt.setString(2, lUtilisateur.getNom());
                pstmt.setString(3, lUtilisateur.getPrenom());
                pstmt.setString(4, lUtilisateur.getEmail());
                pstmt.setString(5, lUtilisateur.getTelephone());
                pstmt.setString(6, lUtilisateur.getRue());
                pstmt.setString(7, lUtilisateur.getCodePostal());
                pstmt.setString(8, lUtilisateur.getVille());
                pstmt.setString(9, lUtilisateur.getMotDePasse());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    lUtilisateur.setNumUtilisateur(rs.getInt("no_utilisateur"));
                }
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
                bllException.ajouterErreur(CodesResultatDAL.ERROR_CREATE_USER);
                myLogger.log(Level.WARNING,"Erreur lors de la création d'un nouvel utilisateur", bllException);
                throw bllException;
            }
        }
        return lUtilisateur;
    }

    @Override
    public void updateUserData(Utilisateur lUtilisateur) throws BLLException {
        BLLException bllException = new BLLException();
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_USER_DATA);
            pstmt.setString(1, lUtilisateur.getPseudo());
            pstmt.setString(2, lUtilisateur.getNom());
            pstmt.setString(3, lUtilisateur.getPrenom());
            pstmt.setString(4, lUtilisateur.getEmail());
            pstmt.setString(5, lUtilisateur.getTelephone());
            pstmt.setString(6, lUtilisateur.getRue());
            pstmt.setString(7, lUtilisateur.getCodePostal());
            pstmt.setString(8, lUtilisateur.getVille());
            pstmt.setString(9, lUtilisateur.getMotDePasse());
            pstmt.setInt(10, lUtilisateur.getNumUtilisateur());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_UPDATE_USER_DATA);
            myLogger.log(Level.WARNING,"Erreur lors de la mise à jour des informations de l'utilisateur (idUser : " + lUtilisateur.getNumUtilisateur()  + ")", bllException);
            throw bllException;
        }
    }

    @Override
    public void updateUserAccountStatus(int idUser) throws BLLException {
        Utilisateur lUtilisateur = this.selectById(idUser);
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_USER_ACCOUNT_STATUS);
            pstmt.setBoolean(1, !lUtilisateur.isCompteActif());
            pstmt.setInt(2, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_UPDATE_USER_ACCOUNT_STATUS);
            myLogger.log(Level.WARNING,"Erreur lors de la mise à jour du statut du compte de l'utilisateur (idUser : " + idUser  + ")", bllException);
            throw bllException;
        }
    }

    @Override
    public int updateUserCredit(int newCredit, int idUser) throws BLLException {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_USER_CREDIT);
            pstmt.setInt(1, newCredit);
            pstmt.setInt(2, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_UPDATE_USER_CREDIT);
            myLogger.log(Level.WARNING,"Erreur lors de la mise à jour des crédits de l'utilisateur (idUser : " + idUser  + ")", bllException);
            throw bllException;
        }
        return newCredit;
    }

    @Override
    public void deleteUser(int idUser) throws BLLException {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(DELETE_USER);
            pstmt.setInt(1, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_DELETE_USER);
            myLogger.log(Level.WARNING,"Erreur lors de la suppression de l'utilisateur (idUser : " + idUser  + ")", bllException);
            throw bllException;
        }
    }

    @Override
    public Utilisateur checkIfUserExists(String username, String email) throws BLLException {
        Utilisateur lUtilisateur = null;
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(CHECK_USER_EXISTENCE);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                lUtilisateur = getUtilisateur(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_CHECK_USER_EXISTENCE);
            myLogger.log(Level.WARNING,"Erreur lors de la vérification de l'utilisateur supposé " + username + "/" + email, bllException);
            throw bllException;
        }
        return lUtilisateur;
    }

    @Override
    public void resetPwd(int idUser, String newPwd) throws BLLException {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(RESET_PASSWORD);
            pstmt.setString(1, newPwd);
            pstmt.setInt(2, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            BLLException bllException = new BLLException();
            bllException.ajouterErreur(CodesResultatDAL.ERROR_RESET_PWD);
            myLogger.log(Level.WARNING,"Erreur lors de la réinitialisation du mdp de l'utilisateur (idUser : " + idUser  + ")", bllException);
            throw bllException;
        }
    }

    private Utilisateur getUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur lUtilisateur = new Utilisateur(
                rs.getInt("no_utilisateur"),
                rs.getString("pseudo"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("telephone"),
                rs.getString("rue"),
                rs.getString("code_postal"),
                rs.getString("ville"),
                rs.getString("mot_de_passe"),
                rs.getInt("credit"),
                rs.getBoolean("administrateur"),
                rs.getBoolean("compteActif")
        );
        return lUtilisateur;
    }
}
