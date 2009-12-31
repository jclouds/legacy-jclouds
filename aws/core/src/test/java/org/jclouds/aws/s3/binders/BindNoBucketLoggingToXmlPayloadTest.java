/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindNoBucketLoggingToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.BindNoBucketLoggingToXmlPayloadTest")
public class BindNoBucketLoggingToXmlPayloadTest extends BaseHandlerTest {

   public void testApplyInputStream() throws IOException {

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindNoBucketLoggingToXmlPayload binder = injector
               .getInstance(BindNoBucketLoggingToXmlPayload.class);

      binder.bindToRequest(request, null);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "text/xml");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), "70");
      assertEquals(request.getPayload().getRawContent(),
               "<BucketLoggingStatus xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"/>");

   }
}
