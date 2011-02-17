/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.terremark.handlers;

import java.net.URI;

import org.jclouds.http.BaseHttpErrorHandlerTest;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseTerremarkVCloudErrorFromHttpResponseTest extends BaseHttpErrorHandlerTest {
   @Test
   public void testGet400SetsIllegalArgumentException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 400,
               "HTTP/1.1 400 Service name is required.", "", IllegalArgumentException.class);
   }

   @Test
   public void testGet403SetsResourceNotFoundException() {
      assertCodeMakes(
               "GET",
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
               403,
               "HTTP/1.1 403 Internet Service does not exist in the system. Internet Service was probably deleted by another user. Please refresh and retry the operation",
               "", ResourceNotFoundException.class);
   }

   @Test
   public void testDelete403SetsResourceNotFoundException() {
      assertCodeMakes(
               "DELETE",
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vapp/361849"),
               403,
               "HTTP/1.1 403 Server does not exist in the system. Server was probably deleted by another user. Please refresh and retry the operation",
               "", ResourceNotFoundException.class);
   }

   @Test
   public void testGet404SetsResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 404,
               "", "", ResourceNotFoundException.class);
   }

   @Test
   public void test401SetsAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 401,
               "", "", AuthorizationException.class);
   }

   @Test
   public void test403SetsInsufficientResourcesException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 403,
               "There are no additional Public IPs available", "", InsufficientResourcesException.class);
   }

   @Test
   public void test501SetsNotImplementedMakesUnsupportedOperationException() {
      assertCodeMakes("POST", URI
               .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/vapp/49373/action/undeploy"),
               501, "HTTP/1.1 501 NotImplemented", "", UnsupportedOperationException.class);
   }

   @Test
   public void testbecause_there_is_a_pending_task_runningSetsIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"), 500,
               "because there is a pending task running", "because there is a pending task running",
               IllegalStateException.class);
   }

   @Test
   public void testKeyAlreadyExistsSetsIllegalStateException() {
      assertCodeMakes("POST", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/org/48/keys"), 400,
               "Security key with name livetest exists.", "Security key with name livetest exists.",
               IllegalStateException.class);
   }

   @Override
   protected Class<? extends HttpErrorHandler> getHandlerClass() {
      return ParseTerremarkVCloudErrorFromHttpResponse.class;
   }

}