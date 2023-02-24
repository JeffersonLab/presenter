<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="p" uri="http://jlab.org/presenter/functions"%>
<c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['javax.servlet.forward.request_uri']}${requestScope['javax.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['javax.servlet.forward.query_string']) : ''}"/>
<!DOCTYPE html>
<html class="${initParam.notification ne null ? 'special-notification' : ''}">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Presenter - <c:out value="${show.name}"/></title>
        <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/img/favicon.ico"/>
        <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jquery-ui/1.10.3/theme/smoothness/jquery-ui.min.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/css/presentation.css" type="text/css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/css/slides.css" type="text/css">
        <link rel="stylesheet" type="text/css" href="${cdnContextPath}/aloha-editor/1.4.1/css/aloha.css"/>
    </head>
    <body class="${show.editable ? 'writable' : 'read-only'} ${fn:toLowerCase(presentation.presentationType).replaceAll('_', '-')}">
        <c:if test="${initParam.notification ne null}">
            <div id="notification-bar"><c:out value="${initParam.notification}"/></div>
        </c:if>        
        <img class="indicator16x16" alt="Saving..." src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/img/indicator16x16.gif"/>
        <div id="nav">
            <button type="button" id="laser-pointer-button" class="styled-button">Laser On</button>
            <button type="button" id="send-to-elog-button" class="styled-button">Send to ELog</button>
            <form id="menu-form" class="nav-form" action="${pageContext.request.contextPath}/${fn:escapeXml(show.menuUrl)}" method="get">
                <button type="submit" id="main-menu-button" class="styled-button" value="Menu">Menu</button>
            </form>
            <button type="button" id="full-screen-button" class="styled-button" value="Full Screen">Full Screen</button>
            <button type="button" id="previous-button" class="styled-button" value="Previous">Previous</button>
            <button type="button" id="next-button" class="styled-button" value="Next">Next</button>
            <span id="iframe-message">Click here to resume presentation</span>
            <span id="out-of-bounds-message">Slide Overflowed: add new slide then copy &amp; paste content</span>
            <div class="change-warning-panel-wrap">
                <div class="change-warning-panel" style="display: none;">
                    <button class="close-change-warning">X</button>
                    <div class="change-warning-title">Remote Changes Detected!</div>
                    <span class="change-warning-user">Anonymous</span>
                    <span>made changes to</span>
                    <div class="change-warning-description">Crew Chief Shift Log 2015-08-29 OWL</div>
                    <span>at</span>
                    <span class="change-warning-last-modified">2015-08-29 12:45:03</span>
                    <div>
                        <a class="modified-reload" href="#">Reload</a>
                        <a class="modified-cc-reload" href="#">Reload and Re-sync</a>
                        <a class="modified-pd-reload" href="#">Reload and Re-sync</a>
                    </div>
                </div>
            </div>
        </div>
        <div id="slide-list-wrapper">
            <ul id="slide-list">                
                <c:forEach items="${presentation.slideList}" var="slide">
                    <li>
                        <c:set var="slide" value="${slide}" scope="request"/>
                        <jsp:include page="${finder.getPath(slide)}"/>
                        <div class="slide-cover"></div>
                        <span class="slide-number"><c:out value="${slide.orderId}"/></span>
                        <c:if test="${slide.syncFromSlideId ne null}">
                            <img class="sync-indicator" src="${pageContext.request.contextPath}/resources/img/30px-U1F501.png" alt="Synced" title="Synced"/>
                        </c:if>
                        <c:if test="${show.editable}">
                            <img class="order-indicator" src="${pageContext.request.contextPath}/resources/img/22px-U21C5.png" alt="U/D" title="Drag and Drop to Order"/>
                            <c:if test="${slide.syncFromSlideId eq null || param.force eq 'Y'}">
                                <a class="delete styled-button" title="Delete" href="#"><span>X</span></a>
                            </c:if>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div id="slide-list-buttons">
            <c:if test="${show.editable}">
                <button type="button" id="new-slide-button" class="styled-button" value="New Slide">New Slide</button>
            </c:if>
            <c:if test="${show.editable and show.shiftImport}">
                <form id="import-shift-logs-page-form" class="nav-form" action="${pageContext.request.contextPath}/import-shift-logs" method="post">
                    <button type="button" id="import-shift-logs-button" class="styled-button" value="Import Shift Logs">Select / Refresh Shift Logs</button>
                    <input type="hidden" name="includeGraphs" value="${show.shiftImportIncludeGraphs ? 'Y' : 'N'}"/>
                    <input type="hidden" name="presentationId" value="${presentation.presentationId}"/>
                </form>
            </c:if>
            <c:if test="${show.summaryImport}">
                <form id="import-summary-form" class="nav-form" action="${pageContext.request.contextPath}/import-summary" method="post">
                    <button type="submit" id="import-summary-button" class="styled-button" value="Import Summary">Refresh Oncoming PD Summary</button>
                    <input type="hidden" name="presentationId" value="${presentation.presentationId}"/>
                </form>
            </c:if>                
            <c:if test="${!show.editable}">
                <c:choose>
                    <c:when test="${pageContext.request.userPrincipal eq null}">
                        <span id="need-to-login-message">Login to edit</span>   
                    </c:when>
                    <c:when test="${presentation.presentationType eq 'PD_PRESENTATION' and not pageContext.request.isUserInRole('pd')}">
                        <span id="need-to-login-message">You are not an authorized PD</span>    
                    </c:when>
                    <c:when test="${presentation.presentationType eq 'CC_PRESENTATION' and not pageContext.request.isUserInRole('cc')}">
                        <span id="need-to-login-message">You are not an authorized CC</span>    
                    </c:when>
                    <c:otherwise>
                        <span id="need-to-login-message">You are not authorized to edit</span> 
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
        <div id="viewport">            
        </div>
        <div id="slide-toolbar">
            <c:if test="${show.editable}">
                <fmt:formatDate value="${presentation.lastModified}" pattern="dd-MMM-yyyy HH:mm:ss" var="fmtLastModified"/>
                <button type="button" id="save-button" class="styled-button" disabled="disabled" value="Saved" title="Last Modified ${fmtLastModified} by ${p:formatStaff(presentation.lastModifiedBy)}">Saved</button>
                <button type="button" id="update-shift-log-button" class="styled-button" value="Update Shift Info">Update Shift Info</button>
                <button type="button" id="update-week-log-button" class="styled-button" value="Update Week Info">Update Week Info</button>
                <button type="button" id="update-week-accesses-button" class="styled-button" value="Update Week Accesses">Update Week Accesses</button>
                <button type="button" id="add-row-button" class="styled-button" value="Add Row">Add Row</button>
                <button type="button" id="remove-row-button" class="styled-button" value="Remove Row">Remove Row</button>
                <button type="button" id="iframe-chooser-button" class="styled-button" value="Choose URL">Choose URL</button>
                <button type="button" id="image-chooser-button" class="styled-button" value="Choose Image">Choose Image</button>
            </c:if>
            <button type="button" id="iframe-opener-button" class="styled-button" value="Open In New Window">Open In New Window</button>
            <input type="file" id="fileInput" accept="image/*"/>
            <div id="right-button-cluster">
                <div id="auth">
                    <c:choose>
                        <c:when test="${publicProxy}">

                        </c:when>
                        <c:when test="${pageContext.request.userPrincipal ne null}">
                            <div id="username-container">
                                <c:out value="${pageContext.request.userPrincipal.name.split(':')[2]}"/>
                            </div>
                            <form id="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
                                <button class="styled-button" type="submit" value="Logout">Logout</button>
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
                            <form id="login-form" method="get" action="${pageContext.request.contextPath}/login">
                                <a id="login-link" href="${loginUrl}" class="styled-button">Login</a> <a id="su-link" href="${suUrl}" class="styled-button" href="#">SU</a>  
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
                <button type="button" id="help-button" class="styled-button">?</button>
            </div>
        </div>    
        <c:if test="${show.editable}">
            <div id="new-slide-dialog" class="dialog" title="Choose New Slide Layout">
                <ul id="template-list">
                    <c:forEach items="${show.templateList}" var="slide">
                        <li>
                            <c:set var="slide" value="${slide}" scope="request"/>
                            <c:set var="lazyLoadFrame" value="${true}" scope="request"/>
                            <jsp:include page="${finder.getPath(slide)}"/>
                            <div class="slide-cover"></div>
                            <div class="slide-label"><c:out value="${slide.label}"/></div>
                            <span class="slide-number"></span>
                            <c:if test="${show.editable}">
                                <img class="order-indicator" src="${pageContext.request.contextPath}/resources/img/22px-U21C5.png" alt="U/D" title="Drag and Drop to Order"/>
                                <a class="delete styled-button" title="Delete" href="#"><span>X</span></a>
                            </c:if>
                        </li>
                    </c:forEach>                           
                </ul>  
            </div>
        </c:if>
        <div id="import-shift-logs-dialog" class="dialog" title="Select / Refresh Shift Logs">
            <form id="import-shift-logs-dialog-form" class="nav-form" action="${pageContext.request.contextPath}/import-shift-logs" method="post">
                <input type="hidden" name="presentationId" value="${presentation.presentationId}"/>
                <input type="hidden" name="includeGraphs" value="${show.shiftImportIncludeGraphs ? 'Y' : 'N'}"/>
                <h3><label for="preceding-day-count">Choose how many preceding days of shift logs</label></h3> 
                <input type="text" class="spinner" id="preceding-day-count" name="preceding-day-count" value="${show.shiftLogDays}"/>
                <div class="button-panel">
                    <button id="submit-save-refresh-shift-logs" class="styled-button" type="submit">Save and Refresh</button>
                </div>
            </form>
        </div> 
        <div id="helpDialog" class="dialog" title="Help">
            <div class="dialog-panel">
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
                <a target="_blank" href="${initParam.documentationUrl}">Examples</a>        
            </div>
        </div>
        <div id="success-dialog" class="dialog" title="Elog Sent Successfully">
            <div class="dialog-panel">
                <span class="logentry-success">Log entry: <a id="new-entry-url" href="#"></a></span>    
            </div>
        </div>
        <div id="datepicker-dialog" class="dialog" title="Choose a Date">
            <div class="dialog-panel">
                <div class="datepicker"></div>    
            </div>
        </div>
        <div id="laser-pointer-overlay" style="display: none;">        
            <svg id="laser-pointer-surface" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 150 120" pointer-events="none">
               <circle id="laser-spot" cx="75" cy="60" r="0.5" fill="red"/>
            </svg>
        </div>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/editor-config.js"></script>
        <script type="text/javascript" src="${cdnContextPath}/aloha-editor/1.4.1/lib/aloha-full.min.js" data-aloha-plugins="common/ui,common/format,common/list,common/contenthandler,common/paste,common/link,common/undo,extra/formatlesspaste"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/editor-ready.js"></script>  
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/presenter.js"></script> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/presentation.js"></script>        
        <script type="text/javascript">

            var jlab = jlab || {};
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


            var presenter = presenter || {};
            presenter.ctx = "${pageContext.request.contextPath}";
            presenter.logbookhost = "${show.getLogbookHostname()}";
            presenter.presentationId = ${presentation.presentationId};
            <c:choose>
                <c:when test="${presentation.presentationType eq 'PD_PRESENTATION'}">
            presenter.presentationSubType = "${presentation.pdPresentationType}";
                </c:when>
                <c:otherwise>
            presenter.presentationSubType = "NONE";
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${show.summaryImport}">
            presenter.sync = "PD";
                </c:when>
                <c:when test="${show.shiftImport}">
            presenter.sync = "CC";
                </c:when>
                <c:otherwise>
            presenter.sync = "NONE"
                </c:otherwise>
            </c:choose>
            presenter.auditMap = {};
            <c:forEach items="${auditList}" var="p">
            presenter.auditMap[${p.presentationId}] = ${p.lastModifiedMillis};
            </c:forEach>
            presenter.editable = ${show.editable};
            /*Same-origin policy applies*/
            presenter.resizeUrl = window.location.protocol + '//' + window.location.host + '/image-magic/convert';
            <c:if test="${presentation.presentationType == 'PD_PRESENTATION'}">
                <fmt:formatDate value="${presentation.deliveryYmd}" var="deliveryDate" pattern="yyyy-MM-dd"/>
            presenter.deliveryDate = "${deliveryDate}";
            </c:if>
        </script>
    </body>
</html>
