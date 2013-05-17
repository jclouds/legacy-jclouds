/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.s3.binders;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BindPartIdsAndETagsToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindPartIdsAndETagsToRequestTest")
public class BindPartIdsAndETagsToRequestTest {
   BindPartIdsAndETagsToRequest binder = new BindPartIdsAndETagsToRequest();

   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {
      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      Payload payload = Payloads
               .newStringPayload("<CompleteMultipartUpload><Part><PartNumber>1</PartNumber><ETag>\"a54357aff0632cce46d942af68356b38\"</ETag></Part></CompleteMultipartUpload>");
      payload.getContentMetadata().setContentType(MediaType.TEXT_XML);
      request = binder.bindToRequest(request, ImmutableMap.<Integer, String> of(1,
               "\"a54357aff0632cce46d942af68356b38\""));
      assertEquals(request.getPayload().getRawContent(), payload.getRawContent());
      assertEquals(request, HttpRequest.builder().method("PUT").endpoint("http://localhost").payload(
               payload).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEmptyIsBad() {

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      binder.bindToRequest(request, ImmutableMap.<Integer, String> of());
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindMapToHeadersWithPrefix binder = new BindMapToHeadersWithPrefix("prefix:");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }
}
