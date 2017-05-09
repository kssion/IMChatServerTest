<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="org.apache.catalina.util.RequestUtil" session="false"
         trimDirectiveWhitespaces="true" pageEncoding="UTF8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
 <head>
  <title>出错啦！</title>
  <style type="text/css">
    <!--
    BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;font-size:12px;}
    H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;}
    PRE, TT {border: 1px dotted #525D76}
    A {color : black;}A.name {color : black;}
    .msg{
      margin-top: 50px;
      text-align: center;
      font-size: 20px;
      color: #09f;
    }
    -->
  </style>
 </head>
 <body>
   <h1>404 Not found</h1>
   <p class="msg">
    Sorry! 页面回家过春节了 😂
    <%/*=RequestUtil.filter((String) request.getAttribute("javax.servlet.error.request_uri"))*/%>
   </p>
 </body>
</html>
