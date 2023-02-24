<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="hco" uri="http://jlab.org/presenter/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-subtype="${slide.shiftSlideType}">
    <div class="title-container">
        <div class="title">PD SUMMARY</div>
    </div>
    <div class="content-container">                       
        <div class="content">
            <div class="flow-container">
                <div class="input-wrapper">
                    <div class="placeholder dynamic-two-columns">Click to add text</div>  
                    <div class="dynamic-two-columns editable body">${slide.body}</div>
                </div>
            </div>
            <div class="flow-container">
                <hr class="divider"/>
                <ul class="kv-list">
                    <li>
                        <span class="kv-key">DATE:</span>
                        <span class="date-variable" style="display: none;"><fmt:formatDate pattern="EEEE MMMM d, yyyy" value="${slide.ymd}"/></span>
                        <span class="one-liner noformat">
                            <c:choose>
                                <c:when test="${hco:isFriday(slide.ymd)}">
                                    <fmt:formatDate pattern="EEEE+ MMMM d, yyyy" value="${slide.ymd}"/>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate pattern="EEEE MMMM d, yyyy" value="${slide.ymd}"/>                                    
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </li>
                    <li>
                        <span class="kv-key">SHIFT:</span>
                        <span class="one-liner noformat shift-variable"><c:out value="${slide.shift.label}"/></span>
                    </li>
                    <li>
                        <span class="kv-key">PD:</span>
                        <div class="input-wrapper">
                            <div class="placeholder">Click to add text</div>
                            <span class="one-liner editable noformat team-variable"><c:out value="${slide.team}"/></span>
                        </div>
                    </li>
                    <li>
                        <span class="kv-key">PROGRAM:</span>
                        <div class="input-wrapper">
                            <div class="placeholder">Click to add text</div>
                            <span class="one-liner editable noformat program-variable"><c:out value="${slide.program}"/></span>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>