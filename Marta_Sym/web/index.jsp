<%--
  Created by IntelliJ IDEA.
  User: xinyuzhang
  Date: 4/6/18
  Time: 3:22 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>hello world</title>
</head>
<body>
<form action = "command" method = "post">
    Upload from Database : <input type="submit" value="ok">
</form>
<form action>
    Add Bus: <input type="text" name="dlat" value="33.750292">
</form>
<div id="print-content">
    <form>

        <input type="button" onclick="printDiv('print-content')" value="print a div!"/>
    </form>
</div>
<script type="text/javascript">

    function printDiv(divName) {

        var printContents = document.getElementById(divName).innerHTML;
        w=window.open();
        w.document.write(printContents);
        w.print();
        w.close();
    }
</script>
</body>
</html>