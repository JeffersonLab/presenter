<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-subtype="${slide.pdInfoSlideType}">
    <div class="title-container">
        <div class="title">PD WEEKLY SUMMARY</div>
    </div>
    <div class="content-container">                       
        <div class="content">
            <div class="flow-container">
                <ul class="kv-list">
                    <li>
                        <span class="kv-key">PROGRAM DEPUTY:</span>
                        <div class="input-wrapper">
                            <div class="placeholder">Click to add text</div>
                            <span class="one-liner editable noformat team-variable"><c:out value="${slide.pd}"/></span>
                        </div>
                    </li>
                    <li>
                        <span class="kv-key">DATE (from):</span>
                        <span class="one-liner datepicker-target noformat from-variable"><fmt:formatDate pattern="EEEE MMMM d, yyyy" value="${slide.fromDate}"/></span>
                    </li>
                    <li>
                        <span class="kv-key">DATE (to):</span>
                        <span class="one-liner datepicker-target noformat to-variable"><fmt:formatDate pattern="EEEE MMMM d, yyyy" value="${slide.toDate}"/></span>
                    </li>
                </ul>            
            </div>
            <div class="flow-container">
                <div class="input-wrapper">
                    <div class="placeholder">Click to add text</div>  
                    <div class="editable body">${slide.body}</div>
                </div>
            </div>
        </div>
    </div>
</div>