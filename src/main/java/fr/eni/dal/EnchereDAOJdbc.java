package fr.eni.dal;

import fr.eni.bo.ArticleVendu;
import fr.eni.bo.Enchere;
import fr.eni.bo.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EnchereDAOJdbc implements EnchereDAO {
    private static final String SELECT_ALL_BIDS_BY_ARTICLE = "SELECT A.*, U.*, E.date_enchere, E.montant_enchere " +
            "FROM ENCHERES E " +
            "INNER JOIN UTILISATEURS U " +
            "ON E.no_utilisateur = U.no_utilisateur " +
            "WHERE E.no_article=? " +
            "ORDER BY E.montant_enchere DESC";
    private static final String SELECT_HIGHEST_BID_BY_ARTICLE = "SELECT E.no_utilisateur, U.nom, E.no_article, E.date_enchere, E.montant_enchere " +
            "FROM ENCHERES E " +
            "INNER JOIN UTILISATEURS U " +
            "ON E.no_utilisateur = U.no_utilisateur " +
            "WHERE E.no_article=? " +
            "AND E.montant_enchere=(SELECT MAX(montant_enchere) FROM ENCHERES) " +
            "ORDER BY E.date_enchere DESC";
    public static final String INSERT_BID = "INSERT INTO ENCHERES VALUES(?,?,?,?)";

    @Override
    public List<Enchere> selectBidByIdArticle(int idArticle) {
        List<Enchere> lesEncheres = new ArrayList<>();
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_BIDS_BY_ARTICLE);
            pstmt.setInt(1, idArticle);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Utilisateur lUtilisateur = new Utilisateur(
                        rs.getInt("U.no_utilisateur"),
                        rs.getString("U.pseudo"),
                        rs.getString("U.nom"),
                        rs.getString("U.prenom"),
                        rs.getString("U.email"),
                        rs.getString("U.telephone"),
                        rs.getString("U.rue"),
                        rs.getString("U.codePostal"),
                        rs.getString("U.ville")
                );
                ArticleVendu lArticle = new ArticleVendu(
                        rs.getInt("A.no_article"),
                        rs.getString("A.nom_article"),
                        rs.getString("A.description"),
                        rs.getDate("A.date_debut_encheres").toLocalDate(),
                        rs.getDate("A.date_fin_encheres").toLocalDate(),
                        rs.getInt("A.prix_initial"),
                        rs.getInt("A.prix_vente"),
                        lUtilisateur
                );
                Enchere lEnchere = new Enchere(
                        lUtilisateur,
                        lArticle,
                        rs.getTimestamp("E.date_enchere").toLocalDateTime(),
                        rs.getInt("E.montant_enchere")
                );
                lesEncheres.add(lEnchere);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lesEncheres;
    }

    @Override
    public Enchere selectHighestBidByIdArticle(int idArticle) {
        Enchere lEnchere = null;
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_HIGHEST_BID_BY_ARTICLE);
            pstmt.setInt(1, idArticle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Utilisateur lUtilisateur = new Utilisateur(
                        rs.getInt("U.no_utilisateur"),
                        rs.getString("U.pseudo"),
                        rs.getString("U.nom"),
                        rs.getString("U.prenom"),
                        rs.getString("U.email"),
                        rs.getString("U.telephone"),
                        rs.getString("U.rue"),
                        rs.getString("U.codePostal"),
                        rs.getString("U.ville")
                );
                ArticleVendu lArticle = new ArticleVendu(
                        rs.getInt("A.no_article"),
                        rs.getString("A.nom_article"),
                        rs.getString("A.description"),
                        rs.getDate("A.date_debut_encheres").toLocalDate(),
                        rs.getDate("A.date_fin_encheres").toLocalDate(),
                        rs.getInt("A.prix_initial"),
                        rs.getInt("A.prix_vente"),
                        lUtilisateur
                );
                lEnchere = new Enchere(
                        lUtilisateur,
                        lArticle,
                        rs.getTimestamp("E.date_enchere").toLocalDateTime(),
                        rs.getInt("E.montant_enchere")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lEnchere;
    }

    @Override
    public Enchere createEnchere(Enchere lEnchere) {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(INSERT_BID);
            pstmt.setInt(1, lEnchere.getlUtilisateur().getNumUtilisateur());
            pstmt.setInt(2, lEnchere.getlArticle().getNumArticle());
            pstmt.setTimestamp(3, Timestamp.valueOf(lEnchere.getDateEnchere()));
            pstmt.setInt(4, lEnchere.getMontantEnchere());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'insertion de l'enchère en base");
            e.printStackTrace();
        }
        return lEnchere;
    }
}
