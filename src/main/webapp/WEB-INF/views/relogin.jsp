<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><c:out value="${initParam.appShortName}"/> - ${title}</title>
</head>
<body>
        <section>
            <div style="text-align: center;">Your session has been restored.  You may close this dialog and retry to save your work or execute any prior actions.</div>
        </section>
</body>
</html>
