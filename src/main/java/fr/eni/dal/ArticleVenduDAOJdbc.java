package fr.eni.dal;

import fr.eni.bo.ArticleVendu;
import fr.eni.bo.Categorie;
import fr.eni.bo.Retrait;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ArticleVenduDAOJdbc implements ArticleVenduDAO {

    private static final String SELECT_ALL_ARTICLES = "SELECT A.no_article, A.nom_article, A.description, " +
            "A.date_debut_encheres, A.date_fin_encheres, A.prix_initial, A.prix_vente, A.etat_vente, A.no_utilisateur, C.*, " +
            "R.rue, R.ville, R.code_postal " +
            "FROM ARTICLES_VENDUS A " +
            "INNER JOIN UTILISATEURS U " +
            "ON A.no_utilisateur = U.no_utilisateur " +
            "INNER JOIN CATEGORIES C " +
            "ON A.no_categorie = C.no_categorie" +
            "INNER JOIN RETRAITS R " +
            "ON A.no_article = R.no_article";
    private static final String INSERT_ARTICLE = "INSERT INTO ARTICLES_VENDUS(no_article, nom_article,description,date_debut_encheres, " +
            "date_fin_encheres, prix_initial, etat_vente, no_utilisateur, no_categorie) VALUES (?,?,?,?,?,?,?,?,?)";


    // TODO : fournir List<ArticleVendu> avec toutes les infos, le tri se fera côté front ?

    @Override
    public List<ArticleVendu> selectAll() {
        List<ArticleVendu> lesArticles = new ArrayList<>();

        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_ARTICLES);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ArticleVendu lArticle = new ArticleVendu();
                Retrait lieuRetrait = new Retrait();
                Categorie laCategorie = new Categorie();

                lArticle.setNo_article(rs.getInt("A.no_article"));
                lArticle.setNomArticle(rs.getString("A.nom_article"));
                lArticle.setDescription(rs.getString("A.description"));
                lArticle.setDateDebutEnchere(rs.getDate("A.date_debut_encheres").toLocalDate());
                lArticle.setDateFinEnchere(rs.getDate("A.date_fin_encheres").toLocalDate());

                // TODO : check si prix non nuls
                lArticle.setMiseAPrix(rs.getInt("A.prix_initial"));
                lArticle.setPrixVente(rs.getInt("A.prix_vente"));
                lArticle.setEtatVente(rs.getString("A.etat_vente"));

                lieuRetrait.setNo_article(rs.getInt("A.no_article"));
                lieuRetrait.setRue(rs.getString("R.rue"));
                lieuRetrait.setCodePostal(rs.getString("R.code_postal"));
                lieuRetrait.setVille(rs.getString("R.ville"));
                lArticle.setLieuRetrait(lieuRetrait);

                laCategorie.setNo_categorie(rs.getInt("C.no_categorie"));
                laCategorie.setLibelle(rs.getString("C.libelle"));
                lArticle.setLaCategorie(laCategorie);

                lArticle.setNo_utilisateur(rs.getInt("A.no_utilisateur"));

                lesArticles.add(lArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lesArticles;
    }

    @Override
    public void createArticle(ArticleVendu lArticle) {
        if (lArticle == null) {
            //throw exception
        }

        // TODO : Revoir l'histoire des Generated_Keys (récupérer le no_article après création ?)
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(INSERT_ARTICLE);

            pstmt.setInt(1, lArticle.getNo_article());
            pstmt.setString(2, lArticle.getNomArticle());
            pstmt.setString(3, lArticle.getDescription());
            pstmt.setDate(4, Date.valueOf(lArticle.getDateDebutEnchere()));
            pstmt.setDate(5, Date.valueOf(lArticle.getDateFinEnchere()));
            pstmt.setInt(6, lArticle.getMiseAPrix());
            pstmt.setString(7, lArticle.getEtatVente());
            pstmt.setInt(8, lArticle.getNo_utilisateur());
            pstmt.setInt(9, lArticle.getLaCategorie().getNo_categorie());

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
