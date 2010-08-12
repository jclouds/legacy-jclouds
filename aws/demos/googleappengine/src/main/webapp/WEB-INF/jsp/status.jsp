<%--


    Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>

    ====================================================================
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ====================================================================

--%>

<%@ page buffer="20kb"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<title>jclouds: multi-cloud framework</title>
</head>
<body>
<h2>Status List</h2>
<display:table name="status"  >
	<display:column property="service"  title="Service" />
	<display:column property="host"  title="Host" />
	<display:column property="name"  title="Item" />
    <display:column property="status"  title="Status" />	
</display:table>
</body>
</html>
