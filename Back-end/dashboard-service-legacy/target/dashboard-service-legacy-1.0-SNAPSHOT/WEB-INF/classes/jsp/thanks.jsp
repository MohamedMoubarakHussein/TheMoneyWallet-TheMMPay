<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <jsp:useBean id="myBean" class="com.themoneywallet.bean.UserBean" scope = "session"/>
    <jsp:setProperty name = "myBean" property = "*"/>
        <h1>Ruler     ${applicationScope.de}  </h1>
    <h1>Hello   ,  My master     <jsp:getProperty name="myBean" property="username" />
<jsp:getProperty name="myBean" property="password" /> 
 </h1>
<h1>
    <%
            application.setAttribute("dxe", "test-de");

    %>
</h1>

<h1>Test: ${dezzz}</h1>

</body>
</html>