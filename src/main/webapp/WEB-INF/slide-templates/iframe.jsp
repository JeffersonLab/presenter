<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-sync-presentationid="${slide.syncFromPresentationId}">
    <div class="input-wrapper">
        <div class="placeholder iframe-placeholder-cover"><div class="iframe-placeholder">Click to add url</div></div>
        <c:choose>
            <c:when test="${lazyLoadFrame eq true}">
                <div class="lazy-iframe" data-src="${fn:escapeXml(slide.iframeUrl)}"></div>
            </c:when>
            <c:otherwise>
                <iframe src="${fn:escapeXml(slide.iframeUrl)}">This application requires iframes and your browser does not support them.</iframe>
            </c:otherwise>
        </c:choose>
    </div>
</div>