<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="p" uri="http://jlab.org/presenter/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}">
    <div class="title-container">
        <div class="title">PD WEEKLY SUMMARY</div>
    </div>
    <div class="content-container">                       
        <div class="content">
            <div class="flow-container">
                <div class="input-wrapper">
                    <div class="placeholder">Click to add text</div>
                    <div class="editable body">${slide.body}</div>
                </div>
            </div>        
        </div>
    </div>
</div>