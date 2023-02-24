<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="slide ${(slide.presentation.presentationType eq 'UITF_PRESENTATION' or slide.syncFromPresentationType eq 'UITF_PRESENTATION') ? 'uitf-slide' : ''} ${(slide.presentation.presentationType eq 'LO_PRESENTATION' or slide.syncFromPresentationType eq 'LO_PRESENTATION') ? 'lerf-slide' : ''} ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-sync-presentationid="${slide.syncFromPresentationId}">
    <div class="title-container">
        <div class="input-wrapper">
            <div class="placeholder">Click to add title</div>                        
            <div class="title editable noformat">${slide.title}</div>
        </div>
    </div>
    <div class="content-container">
        <div class="content">
            <div class="left-column-container">
                <div class="input-wrapper">
                    <div class="placeholder">
                        <ul>
                            <li>Click to add text</li>
                        </ul>
                    </div>
                    <div class="left-column editable body">${slide.body}</div>
                </div>
            </div>
            <div class="right-column-container">
                <div class="right-column image-container">
                    <div class="input-wrapper input-image-wrapper">
                        <div class="placeholder image-placeholder">Click to add image</div> 
                        <div class="img-div" style="background-image: url('${slide.imageUrl}');"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>