<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="slide ${slide.syncFromSlideId ne null ? 'synced-slide' : ''}" data-slideid="${fn:escapeXml(slide.slideId)}" data-type="${slide.slideType}" data-subtype="${slide.shiftSlideType}" data-sync-presentationid="${slide.syncFromPresentationId}">
    <div class="title-container">
        <div class="title">CEBAF SHIFT LOG</div>
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
                <div class="left-footer-column">
                    <ul class="kv-list">
                        <li>
                            <fmt:formatDate value="${slide.ymd}" pattern="yyyy-MM-dd" var="ymd"/>
                            <span class="kv-key">DATE:</span>
                            <span class="one-liner noformat date-variable" data-date="${ymd}"><fmt:formatDate value="${slide.ymd}" pattern="EEEE MMMM d, yyyy"/></span>
                        </li>
                        <li>
                            <span class="kv-key">SHIFT:</span>
                            <span class="one-liner noformat shift-variable" data-shift="${slide.shift}"><c:out value="${slide.shift.label}"/></span>
                        </li>
                        <li>
                            <span class="kv-key">TEAM:</span>
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
                <div class="right-footer-column">
                    <fmt:formatDate value="${slide.crewChiefStartDayAndHourInclusive}" pattern="dd-MMM-yyyy HH:mm" var="start"/>
                    <fmt:formatDate value="${slide.crewChiefEndDayAndHourExclusive}" pattern="dd-MMM-yyyy HH:mm" var="end"/>
                    <c:url context="/" value="/btm/reports/physics-summary" var="url">
                        <c:param name="physics-data" value="table"/>
                        <c:param name="print" value="Y"/>
                        <c:param name="fullscreen" value="Y"/>
                        <c:param name="start" value="${start}"/>
                        <c:param name="end" value="${end}"/>
                    </c:url>
                    <table class="bta-table">
                        <thead>
                            <tr>
                                <th></th>
                                <th>Sched.</th>
                                <th>Actual<a title="Physics: UP + BNR | Internal: Restore, ACC, SAD, or Studies (Click for details...)" href="${url}" target="_blank">*</a></th>
                                <th>ABU</th>
                                <th>BANU</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${slide.btaRecordList}" var="record">
                                <tr>
                                    <td><span class="table-field editable noformat"><c:out value="${record.hallProgram}"/></span></td>
                                    <td><span class="table-field editable noformat"><fmt:formatNumber value="${record.scheduled}" pattern="0.0"/></span></td>
                                    <td><span class="table-field editable noformat"><fmt:formatNumber value="${record.actual}" pattern="0.0"/></span></td>
                                    <td><span class="table-field editable noformat"><fmt:formatNumber value="${record.abu}" pattern="0.0"/></span></td>
                                    <td><span class="table-field editable noformat"><fmt:formatNumber value="${record.banu}" pattern="0.0"/></span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>