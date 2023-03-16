<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Error"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            #notification-bar {
                position: fixed;
                background-color: orange;
                color: white;
                width: 100%;
                text-align: center;
                font-size: 24px;
                box-shadow: 0 8px 8px #979797;
                z-index: 1000;    
                font-family: "Verdana",sans-serif;
            }     
        </style>        
    </jsp:attribute>
    <jsp:attribute name="scripts">    
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/login.js"></script>
    </jsp:attribute>            
    <jsp:body>
        <section>
            <div class="error-message message-box"><c:out value="${message}"/></div>
        </section>
    </jsp:body>         
</t:page>
