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
    private static final String SELECT_ALL_ARTICLES = "SELECT U.*, A.*, C.*, R.* " +
            "FROM ARTICLES_VENDUS A " +
            "INNER JOIN UTILISATEURS U " +
            "ON A.no_utilisateur = U.no_utilisateur " +
            "INNER JOIN CATEGORIES C " +
            "ON A.no_categorie = C.no_categorie " +
            "INNER JOIN RETRAITS R " +
            "ON A.no_article = R.no_article";
    private static final String SELECT_ARTICLE_BY_ID = "SELECT U.*, A.*, C.*, R.* " +
            "FROM ARTICLES_VENDUS A " +
            "INNER JOIN UTILISATEURS U " +
            "ON A.no_utilisateur = U.no_utilisateur " +
            "INNER JOIN CATEGORIES C " +
            "ON A.no_categorie = C.no_categorie " +
            "INNER JOIN RETRAITS R " +
            "ON A.no_article = R.no_article " +
            "WHERE A.no_article=?";
    private static final String INSERT_ARTICLE = "INSERT INTO ARTICLES_VENDUS(nom_article,description,date_debut_encheres, " +
            "date_fin_encheres, prix_initial, etat_vente, no_utilisateur, no_categorie) VALUES (?,?,?,?,?,?,?,?)";
    private static final String UPDATE_ARTICLE_BID = "UPDATE ARTICLES_VENDUS SET prix_vente=? WHERE no_article=?";
    private static final String DELETE_ALL_ARTICLES_BY_USER_ID="DELETE FROM ARTICLES_VENDUS WHERE no_utilisateur=?";
    private static final String DELETE_ARTICLE = "DELETE FROM ARTICLES_VENDUS WHERE no_article = ?";
    private static final String UPDATE_ARTICLE_VENDU = "UPDATE ARTICLES_VENDUS SET nom_article=?, description=?, date_debut_encheres=?, date_fin_encheres=?, prix_initial=?, etat_vente=?, no_utilisateur=?, no_categorie=? WHERE no_article=?";

    @Override
    public List<ArticleVendu> selectAll() {
        List<ArticleVendu> lesArticles = new ArrayList<>();

        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_ARTICLES);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Utilisateur lUtilisateur = new Utilisateur(
                        rs.getInt("no_utilisateur"),
                        rs.getString("pseudo"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("rue"),
                        rs.getString("code_postal"),
                        rs.getString("ville")
                );
                Retrait lieuRetrait = new Retrait(
                    rs.getString("rue"),
                    rs.getString("code_postal"),
                    rs.getString("ville")
                );
                Categorie laCategorie = new Categorie(
                        rs.getInt("no_categorie"),
                        rs.getString("libelle")
                );
                ArticleVendu lArticle = new ArticleVendu(
                        rs.getInt("no_article"),
                        rs.getString("nom_article"),
                        rs.getString("description"),
                        rs.getDate("date_debut_encheres").toLocalDate(),
                        rs.getDate("date_fin_encheres").toLocalDate(),
                        rs.getInt("prix_initial"),
                        rs.getInt("prix_vente"),
                        rs.getString("etat_vente"),
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
                        rs.getInt("no_utilisateur"),
                        rs.getString("pseudo"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("rue"),
                        rs.getString("code_postal"),
                        rs.getString("ville")
                );
                Retrait lieuRetrait = new Retrait(
                        rs.getString("rue"),
                        rs.getString("code_postal"),
                        rs.getString("ville")
                );
                Categorie laCategorie = new Categorie(
                        rs.getInt("no_categorie"),
                        rs.getString("libelle")
                );
                lArticle = new ArticleVendu(
                        rs.getInt("no_article"),
                        rs.getString("nom_article"),
                        rs.getString("description"),
                        rs.getDate("date_debut_encheres").toLocalDate(),
                        rs.getDate("date_fin_encheres").toLocalDate(),
                        rs.getInt("prix_initial"),
                        rs.getInt("prix_vente"),
                        rs.getString("etat_vente"),
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
            pstmt.setString(6, lArticle.getEtatVente());
            pstmt.setInt(7, lArticle.getlUtilisateur().getNumUtilisateur());
            pstmt.setInt(8, lArticle.getLaCategorie().getNumCategorie());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                lArticle.setNumArticle(rs.getInt(1));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(ArticleVendu articleVendu) {
        try(Connection cnx = ConnectionProvider.getConnection()){
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_ARTICLE_VENDU);

            pstmt.setString(1, articleVendu.getNomArticle());
            pstmt.setString(2, articleVendu.getDescription());
            pstmt.setDate(3, Date.valueOf(articleVendu.getDateDebutEnchere()));
            pstmt.setDate(4, Date.valueOf(articleVendu.getDateFinEnchere()));
            pstmt.setInt(5, articleVendu.getMiseAPrix());
            pstmt.setString(6, articleVendu.getEtatVente());
            pstmt.setInt(7, articleVendu.getlUtilisateur().getNumUtilisateur());
            pstmt.setInt(8, articleVendu.getLaCategorie().getNumCategorie());
            pstmt.setInt(9, articleVendu.getNumArticle());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllArticlesByUserId(int idUser) {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(DELETE_ALL_ARTICLES_BY_USER_ID);
            pstmt.setInt(1, idUser);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticleById(int id) {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(DELETE_ARTICLE);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
