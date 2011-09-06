<%--

    Licensed to jclouds, Inc. (jclouds) under one or more
    contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  jclouds licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ page buffer="20kb"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<title>jclouds: anyweight cloudware for java</title>
<style type="text/css">
<!--
table.staticheader {
text-decoration: none;
border: 1px solid #CCC;
width: 98%;
}

table.staticheader th {
padding: 3px 3px 3px 3px !important;
text-align:center;
}

table.staticheader td {
padding: 3px 3px 3px 3px !important;
}

table.staticheader thead tr {
position: relative;
height: 10px;
background-color: #D7E5F3;
}

table.staticheader tbody {
height:800px;
overflow-x:hidden;
overflow-y: auto; 
overflow:scroll;
}

table.staticheader tbody tr {
height: auto;
white-space: nowrap;
}

table.staticheader tbody tr.odd {
        background-color: #eee
}

table.staticheader tbody tr.tableRowEven,tr.even {
        background-color: #ddd
}

table.staticheader tbody tr td:last-child {
padding-right: 20px;
}

table.staticheader tbody td {
padding: 2px 4px 2px 4px !important;
                
}

div.TableContainer {
height: 800px; 
overflow-x:hidden; 
overflow-y:auto;
}
-->
</style>
</head>
<body>
<h2>Tweets in Clouds</h2>
<table width="100%" border="0">
<tr>
  <td>
  <div class="TableContainer">
  <display:table name="tweets" defaultsort="1" cellpadding="5" cellspacing="1" class="staticheader">
    <display:column property="id" title="Tweet ID" />
    <display:column property="from" title="Who Said it" />
    <display:column property="tweet" title="Tweet" />
    <display:column property="service" title="Cloud" />
    <display:column property="host" title="Host" />
    <display:column property="status" title="Status" /> 
  </display:table>
  </div>
  </td>
</tr>
<tr>
  <td><img src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif" 
alt="Powered by Google App Engine" /></td>
</tr>
</table>
</body>
</html>
