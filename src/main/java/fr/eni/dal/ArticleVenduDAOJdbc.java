package fr.eni.dal;

import fr.eni.bll.BLLException;
import fr.eni.bo.ArticleVendu;
import fr.eni.bo.Categorie;
import fr.eni.bo.Retrait;
import fr.eni.bo.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ArticleVenduDAOJdbc implements ArticleVenduDAO {
    private static final String SELECT_ALL_ARTICLES = "SELECT U.*, A.no_article, A.nom_article, A.description, " +
            "A.date_debut_encheres, A.date_fin_encheres, A.prix_initial, A.prix_vente, A.etat_vente, A.no_utilisateur, C.*, " +
            "R.rue, R.ville, R.code_postal " +
            "FROM ARTICLES_VENDUS A " +
            "INNER JOIN UTILISATEURS U " +
            "ON A.no_utilisateur = U.no_utilisateur " +
            "INNER JOIN CATEGORIES C " +
            "ON A.no_categorie = C.no_categorie " +
            "INNER JOIN RETRAITS R " +
            "ON A.no_article = R.no_article";
    private static final String SELECT_ARTICLE_BY_ID = "SELECT U.*, A.no_article, A.nom_article, A.description, " +
            "A.date_debut_encheres, A.date_fin_encheres, A.prix_initial, A.prix_vente, A.etat_vente, A.no_utilisateur, C.*, " +
            "R.rue, R.ville, R.code_postal " +
            "FROM ARTICLES_VENDUS A " +
            "INNER JOIN UTILISATEURS U " +
            "ON A.no_utilisateur = U.no_utilisateur " +
            "INNER JOIN CATEGORIES C " +
            "ON A.no_categorie = C.no_categorie " +
            "INNER JOIN RETRAITS R " +
            "ON A.no_article = R.no_article " +
            "WHERE A.no_article=?";
    private static final String INSERT_ARTICLE = "INSERT INTO ARTICLES_VENDUS(nom_article,description,date_debut_encheres, " +
            "date_fin_encheres, prix_initial, no_utilisateur, no_categorie) VALUES (?,?,?,?,?,?,?)";
    private static final String UPDATE_ARTICLE_BID = "UPDATE ARTICLES_VENDUS SET prix_vente=? WHERE no_article=?";

    @Override
    public List<ArticleVendu> selectAll() {
        List<ArticleVendu> lesArticles = new ArrayList<>();

        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_ARTICLES);
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
                Retrait lieuRetrait = new Retrait(
                    rs.getString("R.rue"),
                    rs.getString("R.code_postal"),
                    rs.getString("R.ville")
                );
                Categorie laCategorie = new Categorie(
                        rs.getInt("C.no_categorie"),
                        rs.getString("C.libelle")
                );
                ArticleVendu lArticle = new ArticleVendu(
                        rs.getInt("A.no_article"),
                        rs.getString("A.nom_article"),
                        rs.getString("A.description"),
                        rs.getDate("A.date_debut_encheres").toLocalDate(),
                        rs.getDate("A.date_fin_encheres").toLocalDate(),
                        rs.getInt("A.prix_initial"),
                        rs.getInt("A.prix_vente"),
                        lieuRetrait,
                        laCategorie,
                        lUtilisateur
                );
                lieuRetrait.setlArticle(lArticle);
                lesArticles.add(lArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lesArticles;
    }

    @Override
    public ArticleVendu selectArticleById(int idArticle) {
        ArticleVendu lArticle = null ;
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ARTICLE_BY_ID);
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
                Retrait lieuRetrait = new Retrait(
                        rs.getString("R.rue"),
                        rs.getString("R.code_postal"),
                        rs.getString("R.ville")
                );
                Categorie laCategorie = new Categorie(
                        rs.getInt("C.no_categorie"),
                        rs.getString("C.libelle")
                );
                lArticle = new ArticleVendu(
                        rs.getInt("A.no_article"),
                        rs.getString("A.nom_article"),
                        rs.getString("A.description"),
                        rs.getDate("A.date_debut_encheres").toLocalDate(),
                        rs.getDate("A.date_fin_encheres").toLocalDate(),
                        rs.getInt("A.prix_initial"),
                        rs.getInt("A.prix_vente"),
                        lieuRetrait,
                        laCategorie,
                        lUtilisateur
                );
                lieuRetrait.setlArticle(lArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lArticle;
    }

    @Override
    public ArticleVendu createArticle(ArticleVendu lArticle) {
        if (lArticle == null) {
            //throw exception
        }
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(INSERT_ARTICLE, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, lArticle.getNomArticle());
            pstmt.setString(2, lArticle.getDescription());
            pstmt.setDate(3, Date.valueOf(lArticle.getDateDebutEnchere()));
            pstmt.setDate(4, Date.valueOf(lArticle.getDateFinEnchere()));
            pstmt.setInt(5, lArticle.getMiseAPrix());
            pstmt.setInt(6, lArticle.getlUtilisateur().getNumUtilisateur());
            pstmt.setInt(7, lArticle.getLaCategorie().getNo_categorie());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                lArticle.setNumArticle(rs.getInt("no_article"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lArticle;
    }

    @Override
    public void updateBidOnArticle(int bid, int idArticle) {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_ARTICLE_BID);
            pstmt.setInt(1, bid);
            pstmt.setInt(2, idArticle);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
