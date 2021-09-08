package fr.eni.dal;

import fr.eni.bo.Categorie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAOJdbc implements CategorieDAO {
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM CATEGOERIES";
    private static final String INSERT_NEW_CATEGORY = "INSERT INTO CATEGORIES VALUES (?)";
    private static final String UPDATE_CATEGORY = "UPDATE CATEGORIES SET libelle=? WHERE no_categorie=?";
    // TODO : vérifier le fonctionnement attendu d'une suppression de catégorie
    private static final String DELETE_CATEGORY = "DELETE FROM CATEGORIES WHERE no_categorie=?";

    @Override
    public List<Categorie> selectAll() {
        List<Categorie> lesCategories = new ArrayList<Categorie>();
        Categorie laCategorie = new Categorie();

        try(Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(SELECT_ALL_CATEGORIES);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                laCategorie.setNo_categorie(rs.getInt("no_categorie"));
                laCategorie.setLibelle(rs.getString("libelle"));
            }
            lesCategories.add(laCategorie);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return lesCategories;
    }

    @Override
    public void createCategory(Categorie laCategorie) {
        if(laCategorie==null) {
            //throw exception
        }

        // TODO : Revoir l'histoire des Generated_Keys (récupérer le no_categorie après création ?)
        try(Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(INSERT_NEW_CATEGORY);
            pstmt.setString(1,laCategorie.getLibelle());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCategory(Categorie laCategorie) {
        if(laCategorie==null) {
            //throw exception
        }

        try(Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(UPDATE_CATEGORY);
            pstmt.setString(1,laCategorie.getLibelle());
            pstmt.setInt(2, laCategorie.getNo_categorie());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCategory(int idCategory) {
        try(Connection cnx = ConnectionProvider.getConnection()) {
            PreparedStatement pstmt = cnx.prepareStatement(DELETE_CATEGORY);
            pstmt.setInt(1, idCategory);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}