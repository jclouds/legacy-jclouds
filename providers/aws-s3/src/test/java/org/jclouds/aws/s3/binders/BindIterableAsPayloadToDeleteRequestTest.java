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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Savu
 */
public class BindIterableAsPayloadToDeleteRequestTest {

   private final BindIterableAsPayloadToDeleteRequest binder = new BindIterableAsPayloadToDeleteRequest();
   private final HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost/").build();

   @Test
   public void testWithASmallSet() {
      HttpRequest result = binder.bindToRequest(request, ImmutableSet.of("key1", "key2"));

      Payload payload = Payloads
         .newStringPayload("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Delete>" +
            "<Object><Key>key1</Key></Object><Object><Key>key2</Key></Object></Delete>");
      payload.getContentMetadata().setContentType(MediaType.TEXT_XML);

      assertEquals(result.getPayload(), payload);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEmptySetThrowsException() {
      binder.bindToRequest(request, ImmutableSet.of());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testFailsOnNullSet() {
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testExpectedASetInstance() {
      binder.bindToRequest(request, ImmutableList.of());
   }
}
