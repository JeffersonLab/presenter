<%@tag description="The Site Page Template Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['javax.servlet.forward.request_uri']}${requestScope['javax.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['javax.servlet.forward.query_string']) : ''}"/>
<c:set var="currentPath" scope="request" value="${requestScope['javax.servlet.forward.servlet_path']}"/>
<!DOCTYPE html>
<html>
    <head>        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value="${initParam.appShortName}"/> - ${title}</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
        <c:choose>
            <c:when test="${'CDN' eq resourceLocation}">
                <link type="text/css" rel="stylesheet" href="${cdnContextPath}/jquery-ui/1.13.2/theme/atlis/jquery-ui.min.css"/>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/jquery-ui/1.13.2/theme/atlis/jquery-ui.min.css"/>
            </c:otherwise>
        </c:choose>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/dialog.css"/>
        <jsp:invoke fragment="stylesheets"/>
    </head>
    <body>
        <c:if test="${initParam.notification ne null}">
            <div id="notification-bar"><c:out value="${initParam.notification}"/></div>
        </c:if>         
        <div id="page"> 
            <header>
                <div id="left-header">
                    <h1><c:out value="${initParam.appName}"/></h1>
                    <div id="auth">
                        <c:choose>
                            <c:when test="${fn:startsWith(currentPath, '/login')}">
                                <%-- Don't show login/logout when on login page itself! --%>
                            </c:when>
                            <c:when test="${publicProxy}">

                            </c:when>
                            <c:when test="${pageContext.request.userPrincipal ne null}">
                                <div id="username-container">
                                    <c:out value="${pageContext.request.userPrincipal.name.split(':')[2]}"/>
                                </div>
                                <form id="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
                                    <button type="submit" value="Logout">Logout</button>
                                    <input type="hidden" name="returnUrl" value="${fn:escapeXml(domainRelativeReturnUrl)}"/>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <c:set var="absHostUrl" value="${'https://'.concat(pageContext.request.getServerName())}"/>
                                <c:url value="/sso" var="loginUrl">
                                    <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                                </c:url>
                                <c:url value="/sso" var="suUrl">
                                    <c:param name="kc_idp_hint" value="ace-su-keycloak-oidc"/>
                                    <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                                </c:url>
                                <a id="login-link" href="${loginUrl}" class="styled-button">Login</a> <a id="su-link" href="${suUrl}" class="styled-button" href="#">SU</a> 
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div id="center-header">
                    <h2><c:out value="${title}"/></h2>
                    <nav id="top-nav">
                        <ul>
                            <li><a id="openOpener" href="#">Presentations</a></li>
                            <li><a id="helpOpener" href="#">Help</a></li>
                        </ul>     
                    </nav>
                </div>
                <div id="right-header"></div>
            </header>
            <div id="content">            
                <jsp:doBody/>
            </div>
            <footer></footer>
        </div>
        <c:choose>
            <c:when test="${'CDN' eq resourceLocation}">
                <script type="text/javascript" src="${cdnContextPath}/jquery/1.10.2.min.js"></script>
                <script type="text/javascript" src="${cdnContextPath}/jquery-ui/1.10.3/jquery-ui.min.js"></script>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <script type="text/javascript" src="${pageContext.request.contextPath}/resources/jquery/1.10.2.min.js"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/resources/jquery-ui/1.10.3/jquery-ui.min.js"></script>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/presenter.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/dialog.js"></script>
        <script type="text/javascript">
            jlab.contextPath = '${pageContext.request.contextPath}';
            jlab.keycloakHostname = '${env["KEYCLOAK_HOSTNAME"]}';
            jlab.clientId = '${env["KEYCLOAK_CLIENT_ID_PRESENTER"]}';
            <c:url var="url" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/protocol/openid-connect/auth">
            <c:param name="client_id" value="account"/>
            <c:param name="kc_idp_hint" value="cue-keycloak-oidc"/>
            <c:param name="response_type" value="code"/>
            <c:param name="redirect_uri" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/account/"/>
            </c:url>
            jlab.loginUrl = '${url}';
            <c:url var="url" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/protocol/openid-connect/logout">
            <c:param name="redirect_uri" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/account/"/>
            </c:url>
            jlab.logoutUrl = '${url}';
        </script>
        <jsp:invoke fragment="scripts"/>
        <div id="nav-dialogs">
            <div id="helpDialog" class="dialog" title="Help">
                <h3>Presenter</h3>
                <dl class="min-fields">
                    <dt>Version</dt>
                    <dd>${initParam.releaseNumber}</dd>
                    <dt>Release Date</dt>
                    <dd>${initParam.releaseDate}</dd>        
                    <dt>Content Contact</dt>
                    <dd>${initParam.contentContact}</dd>
                    <dt>Technical Contact</dt>
                    <dd>${initParam.technicalContact}</dd>
                </dl>
                <div class="message-box"><a target="_blank" href="${initParam.documentationUrl}">Examples</a></div>
                <div class="dialog-button-panel">
                    <button class="dialog-close-button">Close</button>
                </div>
            </div>     
            <div id="openDialog" class="dialog" title="Presentations">
                <ul>
                    <li><a href="cc-menu">Crew Chief (CC)</a></li>
                    <li><a href="lo-menu">LERF Operator (LO)</a></li>
                    <li><a href="pd-menu">Program Deputy (PD)</a></li>
                    <li><a href="laso-menu">Lead Accelerator Scientist On-shift (LASO)</a></li>
                    <li><a href="uitf-menu">UITF Operator</a></li>
                </ul>
                <div class="dialog-button-panel">
                    <button class="dialog-close-button">Close</button>
                </div>
            </div>             
        </div>
    </body>
</html>