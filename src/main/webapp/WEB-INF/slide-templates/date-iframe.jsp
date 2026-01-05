<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-subtype="${slide.dateSlideType}">
    <div class="input-wrapper">
        <div class="placeholder iframe-placeholder-cover"><div class="iframe-placeholder">Click to add url</div></div>
        <iframe src="${fn:escapeXml(slide.iframeUrl)}">This application requires iframes and your browser does not support them.</iframe>
    </div>
</div>