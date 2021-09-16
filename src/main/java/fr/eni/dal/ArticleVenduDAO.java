package fr.eni.dal;

import fr.eni.bll.BLLException;
import fr.eni.bo.ArticleVendu;

import java.util.List;

public interface ArticleVenduDAO {
    List<ArticleVendu> selectAll();
    List<ArticleVendu> selectAllByUserId(int idUser);
    ArticleVendu selectArticleById(int idArticle);
    ArticleVendu createArticle(ArticleVendu lArticle) throws BLLException;
    void updateBidOnArticle(int bid, int idArticle) throws BLLException;
    void updateAuctionStatus(String newStatus, int idArticle) throws BLLException;
    void updateArticle(ArticleVendu lArticle);
    void deleteAllArticlesByUserId(int idUser);
    void deleteArticle(int id);
}
