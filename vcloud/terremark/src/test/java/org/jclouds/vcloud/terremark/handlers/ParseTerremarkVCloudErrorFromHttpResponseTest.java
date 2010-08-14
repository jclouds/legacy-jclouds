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
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
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
      assertCodeMakes(
               "GET",
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
               400,
               "HTTP/1.1 400 Service name is required.",
               "", IllegalArgumentException.class);
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
//
//   @Test
//   public void testbecause_there_is_a_pending_task_runningSetsIllegalStateException() {
//      assertCodeMakes("GET", URI
//               .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"),
//               500, "because there is a pending task running",
//               "because there is a pending task running", IllegalStateException.class);
//   }

   // case 401:
   // exception = new AuthorizationException(command.getRequest(), content);
   // break;
   // case 403: // TODO temporary as terremark mistakenly uses this for vApp not found.
   // case 404:
   // if (!command.getRequest().getMethod().equals("DELETE")) {
   // String path = command.getRequest().getEndpoint().getPath();
   // Matcher matcher = RESOURCE_PATTERN.matcher(path);
   // String message;
   // if (matcher.find()) {
   // message = String.format("%s %s not found", matcher.group(1), matcher.group(2));
   // } else {
   // message = path;
   // }
   // exception = new ResourceNotFoundException(message);
   // }
   // break;
   // case 401:
   // exception = new AuthorizationException(command.getRequest(), content);
   // break;
   // case 403: // TODO temporary as terremark mistakenly uses this for vApp not found.
   // case 404:
   // if (!command.getRequest().getMethod().equals("DELETE")) {
   // String path = command.getRequest().getEndpoint().getPath();
   // Matcher matcher = RESOURCE_PATTERN.matcher(path);
   // String message;
   // if (matcher.find()) {
   // message = String.format("%s %s not found", matcher.group(1), matcher.group(2));
   // } else {
   // message = path;
   // }
   // exception = new ResourceNotFoundException(message);
   // }
   // break;
   @Override
   protected Class<? extends HttpErrorHandler> getHandlerClass() {
      return ParseTerremarkVCloudErrorFromHttpResponse.class;
   }

}