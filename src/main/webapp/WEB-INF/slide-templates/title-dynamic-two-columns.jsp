<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="slide ${(slide.presentation.presentationType eq 'UITF_PRESENTATION' or slide.syncFromPresentationType eq 'UITF_PRESENTATION') ? 'uitf-slide' : ''} ${(slide.presentation.presentationType eq 'LO_PRESENTATION' or slide.syncFromPresentationType eq 'LO_PRESENTATION') ? 'lerf-slide' : ''} ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-subtype="${slide.titleBodySlideType}" data-sync-presentationid="${slide.syncFromPresentationId}">
    <div class="title-container">
        <div class="input-wrapper">
            <div class="placeholder">Click to add title</div>                         
            <div class="title editable noformat">${slide.title}</div>
        </div>
    </div>
    <div class="content-container">
        <div class="input-wrapper">
            <div class="placeholder dynamic-two-columns">
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>                                
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
            </div>                         
            <div class="content dynamic-two-columns editable body">${slide.body}</div>
        </div>  
    </div>
</div>