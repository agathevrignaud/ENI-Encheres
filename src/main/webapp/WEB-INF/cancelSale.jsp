<%--
  Created by IntelliJ IDEA.
  User: rsaintalme2021
  Date: 07/09/2021
  Time: 11:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>
        Vendre objet aux enchères
    </title>
</head>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-F3w7mX95PdgyTmZZMECAngseQB83DfGTowi0iMjiWaeVhAn4FJkqJByhZMI3AhiU" crossorigin="anonymous">
<header>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <h2>ENI-Enchères</h2>
    </nav>
</header>
<body>
<div class="col-md-6 d-flex justify-content-center">
    <card>
        <div class="card-header" style="background-color: #ffff">
            <h2>Nouvelle vente</h2>
        </div>
        <div class="card-body">
            <form action="CancelSale" method="post">

                <label for="nomArticle">Article : </label>
                <input type="text" id="nomArticle" class="form-control"/><br/>

                <label for="descArticle">Description : </label>
                <input type="text" id="descArticle" class="form-control"/><br/>

                <label for="catArticle">Catégorie : </label>
                <select id="catArticle" name="categories" class="form-control">
                </select>
                <br/>
                <label for="photoArticle">Photo de l'article : </label>
                <input type="button" id="photoArticle" value="UPLOADER" class="form-control"><br/>

                <label for="prixArticle">Mise à prix : </label>
                <input type="number" id="prixArticle" min="1" step="any" class="form-control"/><br/>

                <label for="debutEnchere">Debut de l'enchère : </label>
                <input type="date" id="debutEnchere" class="form-control"><br/>

                <label for="finEnchere">Fin de l'enchère : </label>
                <input type="date" id="finEnchere" class="form-control">
                <br/>
                <fieldset>
                    <legend>Retrait</legend>
                    <hr>
                    <label for="rue">Rue : </label>
                    <input type="text" id="rue" class="form-control"/><br/>

                    <label for="cp">Code postal : </label>
                    <input type="text" id="cp" minlength="5" maxlength="5" class="form-control"/>
                    <br/>

                    <label for="ville">Ville : </label>
                    <input type="text" id="ville" class="form-control"/>
                    <br/>
                </fieldset>

                <input type="submit" value="Enregistrer" class="btn btn-outline-primary">
                <button value="" class="btn btn-outline-danger">Annuler</button>
                <button value="" class="btn btn-outline-danger">Annuler la vente</button>
            </form>
        </div>
    </card>
</div>
<div>
    <%= request.getParameter("listArticle") %>
</div>
</body>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"
        integrity="sha384-W8fXfP3gkOKtndU4JGtKDvXbO53Wy8SZCQHczT5FMiiqmQfUpWbYdTil/SxwZgAN"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.min.js"
        integrity="sha384-skAcpIdS7UcVUC05LJ9Dxay8AXcDYfBJqt1CJ85S/CFujBsIzCIv+l9liuYLaMQ/"
        crossorigin="anonymous"></script>
</html>

