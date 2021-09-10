package fr.eni.servlets;

import fr.eni.bll.UtilisateurManager;
import fr.eni.bo.EmailMessage;
import fr.eni.bo.Utilisateur;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(value = "/forgottenPwd")
public class ServletForgottenPwd extends HttpServlet {
    private static final UtilisateurManager utilisateurManager = new UtilisateurManager();
    private String email_msg = "Hello %s,%n Vous avez demandé la réinitialisation de votre mot de passe. Votre nouveau mot de passe est : %s" +
            "%n%n Ceci est un message généré automatiquement, merci de ne pas y répondre.";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/forgottenPwd.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utilisateur lUtilisateur = utilisateurManager.checkIfUserExists(
                request.getParameter("username"),
                request.getParameter("email")
        );

        if (lUtilisateur != null) {
            request.setAttribute("isUserInDb", true);
            //Send email with the new password TODO : find a way to send a link instead ahaha
            EmailMessage lEmail = new EmailMessage();
            lEmail.setDestinataire(lUtilisateur.getEmail());
            String leMessage = String.format(email_msg, lUtilisateur.getPrenom(), "Testing456!");
            lEmail.setMsg(leMessage);

            // to pursue

        } else {
            request.setAttribute("isUserInDb", false);
        }

        doGet(request, response);
    }
}
