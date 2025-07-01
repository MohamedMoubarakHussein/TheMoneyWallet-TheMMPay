<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    ${5*5}
    <h1>
    <%
            application.setAttribute("dxe", "test-de");

    %>
</h1>

<h1>Test: ${applicationScope.dxe}</h1>
    <form action="thanks.jsp" method="post">
        Name : <input type="text" name="username" >
        password : <input type="password" name="password" id="">
        <input type="submit" value="">
    </form>
</body>
</html>