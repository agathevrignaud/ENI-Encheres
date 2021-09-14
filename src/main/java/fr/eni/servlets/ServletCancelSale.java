package fr.eni.servlets;

import fr.eni.bll.ArticleVenduManager;
import fr.eni.bll.RetraitManager;
import fr.eni.bo.ArticleVendu;
import fr.eni.bo.Retrait;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;


@WebServlet(name = "ServletCancelSale", value = "/cancelsale")
public class ServletCancelSale extends HttpServlet {
    private static final ArticleVenduManager articleVenduManager = new ArticleVenduManager();
    private static final RetraitManager retraitManager = new RetraitManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/cancelSale.jsp");

        ArticleVenduManager articleVenduManager = new ArticleVenduManager();
        RetraitManager retraitManager = new RetraitManager();

        ArticleVendu articleVendu = articleVenduManager.selectArticleVendu(10);
        Retrait retrait = retraitManager.getRetraitById(articleVendu.getNo_article());

        request.setAttribute("articleVenduManager",articleVenduManager);
        request.setAttribute("articleVendu", articleVendu);
        request.setAttribute("retraitManager", retraitManager);
        request.setAttribute("retrait", retrait);

        rd.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        switch (request.getParameter("btnPressed")) {
            case "delete":
                ArticleVendu articleVendu = articleVenduManager.selectArticleVendu(10);
                request.setAttribute("articleVendu", articleVendu);
                Retrait retrait = retraitManager.getRetraitById(articleVendu.getNo_article());
                try{
                    retraitManager.deleteRetrait(retrait.getNo_article());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    articleVenduManager.deleteArticle(articleVendu.getNo_article());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestDispatcher rd = request.getRequestDispatcher("/home.jsp");
                rd.forward(request, response);
                break;
            case "save":
        }

    }
}
