<%--
  Created by IntelliJ IDEA.
  User: Mian
  Date: 2016/3/25
  Time: 11:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <title>注册</title>
</head>
<body>
<br/>
    <form action="${pageContext.request.contextPath }/register" method="post">
        <font size="24" color="red">用户注册</font><br/>
        昵称:<input type="text" name="userName" /><br/>
        邮箱:<input type="text" name="email" /><br/>
        密码:<input type="text" name="password" /><br/>
        <input type="submit" value="submit" /><br/>
    </form>
</body>
</html>
