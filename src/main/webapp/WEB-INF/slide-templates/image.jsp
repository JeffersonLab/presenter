<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-sync-presentationid="${slide.syncFromPresentationId}">
    <div class="content image-container">
        <div class="input-wrapper input-image-wrapper">
            <div class="placeholder image-placeholder">Click to add image</div> 
            <div class="img-div" style="background-image: url('${slide.imageUrl}');"></div>
        </div>
    </div>
</div>