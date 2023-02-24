<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-subtype="${slide.bodySlideType}" data-sync-presentationid="${slide.syncFromPresentationId}">
    <div class="title-container">                       
        <div class="title">CEBAF SHIFT LOG</div>
    </div>
    <div class="content-container">
        <div class="content">
            <div class="flow-container">    
                <div class="input-wrapper">
                    <div class="placeholder dynamic-two-columns">
                        <p>Click to add text</p>
                    </div>                         
                    <div class="dynamic-two-columns editable body">${slide.body}</div>
                </div>  
            </div>
        </div>
    </div>
</div>