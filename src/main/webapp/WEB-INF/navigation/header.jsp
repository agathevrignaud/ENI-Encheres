<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="messages"/>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/utils/css/all.css">
    </head>
    <body>
        <ul>
            <c:choose>
                <c:when test="${isUserLoggedIn}">
                    <%@include file="/WEB-INF/navigation/sessionTimeOut.jsp"%>
                    <li>
                        <a href="${pageContext.request.contextPath}/home">
                            <i class="fas fa-shopping-cart" title="<fmt:message key="header.homepage"/>"></i>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/myProfile?idUser=${userInfo.no_utilisateur}">
                            <fmt:message key="header.myProfile"/>
                        </a>
                    </li>
                    <c:if test="${userInfo.administrateur}">
                        <li>
                            <a href="${pageContext.request.contextPath}/adminTools">
                                <fmt:message key="header.adminTools"/>
                            </a>
                        </li>
                    </c:if>
                    <li>
                        <a href="${pageContext.request.contextPath}/logout">
                            <fmt:message key="header.logout"/>
                        </a>
                    </li
                </c:when>
                <c:otherwise>
                    <li>
                            <i class="fas fa-shopping-cart" title="<fmt:message key="header.homepage"/>"></i>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/signUp">
                            <fmt:message key="header.signUp"/>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/login">
                            <fmt:message key="header.signIn"/>
                        </a>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </body>
</html>
