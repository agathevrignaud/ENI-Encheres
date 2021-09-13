package fr.eni.dal;

import fr.eni.bo.Categorie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAOJdbc implements CategorieDAO {
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM CATEGORIES";
    private static final String INSERT_NEW_CATEGORY = "INSERT INTO CATEGORIES VALUES (?)";
    private static final String UPDATE_CATEGORY = "UPDATE CATEGORIES SET libelle=? WHERE no_categorie=?";
    private static final String DELETE_CATEGORY = "DELETE FROM CATEGORIES WHERE no_categorie=?";
    private static final String SELECT_BY_ID = "SELECT * FROM CATEGORIES WHERE no_categorie = ?";
    private static final String CHECK_IF_CATEGORY_IS_USED = "SELECT COUNT(no_categorie) as nbrUtilisations FROM ARTICLES_VENDUS WHERE no_categorie=?";

    /**
     * Séléctionne toutes le catégories
     */
    @Override
    public List<Categorie> selectAll() {
        List<Categorie> lesCategories = new ArrayList<Categorie>();

        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_CATEGORIES);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Categorie laCategorie = new Categorie();

                laCategorie.setNo_categorie(rs.getInt("no_categorie"));
                laCategorie.setLibelle(rs.getString("libelle"));

                lesCategories.add(laCategorie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lesCategories;
    }

    /**
     * Créer une catégorie
     */
    @Override
    public void createCategory(Categorie laCategorie) {
        if (laCategorie == null) {
            //throw exception
        }

        // TODO : Revoir l'histoire des Generated_Keys (récupérer le no_categorie après création ?)
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(INSERT_NEW_CATEGORY);
            pstmt.setString(1, laCategorie.getLibelle());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mettre à jour une catégorie
     */
    @Override
    public void updateCategory(Categorie laCategorie) {
        if (laCategorie == null) {
            //throw exception
        }

        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_CATEGORY);
            pstmt.setString(1, laCategorie.getLibelle());
            pstmt.setInt(2, laCategorie.getNo_categorie());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprimer une catégorie
     */
    @Override
    public void deleteCategory(int idCategory) {
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(DELETE_CATEGORY);
            pstmt.setInt(1, idCategory);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAllUses(int idCategory) {
        int numberOfUses = 0;
        try (Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(CHECK_IF_CATEGORY_IS_USED);
            pstmt.setInt(1, idCategory);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                numberOfUses = rs.getInt("nbrUtilisations");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfUses;
    }

    /**
     * Séléctionner une catégorie par son id
     */
    public Categorie selectById(int idCategorie) {
        Categorie categorie = null;
        try(Connection cnx = ConnectionProvider.getConnection()){
            PreparedStatement psmt = cnx.prepareStatement(SELECT_BY_ID);
            psmt.setInt(1, idCategorie);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()){
                categorie = map(rs);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return categorie;
    }

    /**
     * Map une catégorie
     */
    public Categorie map(ResultSet rs) throws SQLException {
        int no_categorie = rs.getInt("no_categorie");
        String libelle = rs.getString("libelle");
        return new Categorie(no_categorie, libelle);
    }
}
