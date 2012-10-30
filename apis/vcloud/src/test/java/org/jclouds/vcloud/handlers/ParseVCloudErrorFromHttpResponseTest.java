/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.handlers;

import java.net.URI;

import org.jclouds.http.BaseHttpErrorHandlerTest;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.VCloudMediaType;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseVCloudErrorFromHttpResponseTest extends BaseHttpErrorHandlerTest {

   @Test
   public void testGet404SetsResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 404,
               "", "", ResourceNotFoundException.class);
   }

   @Test
   public void testGet403NoAcessToEntitySetsResourceNotFoundException() {
      assertCodeMakes(
               "GET",
               URI.create("https://zone01.bluelock.com/api/v1.0/vApp/vapp-1535788985"),
               403,
               "HTTP/1.1 403",
               VCloudMediaType.ERROR_XML,
               "<Error xmlns=\"http://www.vmware.com/vcloud/v1\" minorErrorCode=\"ACCESS_TO_RESOURCE_IS_FORBIDDEN\" message=\"No access to entity &quot;(com.vmware.vcloud.entity.vapp:1535788985)&quot;.\" majorErrorCode=\"403\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/v1 http://vcenterprise.bluelock.com/api/v1.0/schema/master.xsd\"></Error>\n",
               ResourceNotFoundException.class);
   }

   @Test
   public void testGet403NoAcessToEntitySetsResourceNotFoundExceptionOnAPI1_0AgainstVCD1_5() {
      assertCodeMakes(
               "GET",
               URI.create("https://mycloud.greenhousedata.com/api/v1.0/vApp/vapp-d3a1f2cd-d07b-4ddc-bf7b-fb7468b4d95a"),
               403,
               "HTTP/1.1 403",
               // NOTE VCD 1.5 appends the api version to the media type
               VCloudMediaType.ERROR_XML + ";1.0",
               "<Error xmlns=\"http://www.vmware.com/vcloud/v1\" minorErrorCode=\"ACCESS_TO_RESOURCE_IS_FORBIDDEN\" message=\"No access to entity &quot;(com.vmware.vcloud.entity.vapp:d3a1f2cd-d07b-4ddc-bf7b-fb7468b4d95a)&quot;.\" majorErrorCode=\"403\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/v1 http://mycloud.greenhousedata.com/api/v1.0/schema/master.xsd\"></Error>",
               ResourceNotFoundException.class);
   }

   @Test
   public void testDelete404SetsHttpResponseException() {
      assertCodeMakes("DELETE", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
               404, "", "", HttpResponseException.class);
   }

   @Test
   public void testPOSTNotRunningSetsIllegalStateException() {
      assertCodeMakes(
               "POST",
               URI.create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vapp-138351019/action/undeploy"),
               400,
               "HTTP/1.1 400 Bad Request",
               VCloudMediaType.ERROR_XML,
               "<Error xmlns=\"http://www.vmware.com/vcloud/v1\" minorErrorCode=\"BAD_REQUEST\" message=\"The requested operation could not be executed since vApp &quot;adriancolecap-78c&quot; is not running&quot;\" majorErrorCode=\"400\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/v1 http://vcenterprise.bluelock.com/api/v1.0/schema/master.xsd\"></Error>\n",
               IllegalStateException.class);
   }

   @Test
   public void test401SetsAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 401,
               "", "", AuthorizationException.class);
   }

   @Override
   protected Class<? extends HttpErrorHandler> getHandlerClass() {
      return ParseVCloudErrorFromHttpResponse.class;
   }

}
