/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.handlers;

import java.net.URI;

import org.jclouds.http.BaseHttpErrorHandlerTest;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseVCloudErrorFromHttpResponseTest extends BaseHttpErrorHandlerTest {

   @Test
   public void testGet404SetsResourceNotFoundException() {
      assertCodeMakes("GET", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
               404, "", "", ResourceNotFoundException.class);
   }

   @Test
   public void testDelete404SetsHttpResponseException() {
      assertCodeMakes("DELETE", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
               404, "", "", HttpResponseException.class);
   }

   @Test
   public void test401SetsAuthorizationException() {
      assertCodeMakes("GET", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
               401, "", "", AuthorizationException.class);
   }

   @Override
   protected Class<? extends HttpErrorHandler> getHandlerClass() {
      return ParseVCloudErrorFromHttpResponse.class;
   }

}