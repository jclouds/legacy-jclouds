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
package org.jclouds.openstack.swift.binders;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.swift.CommonSwiftClientTest;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code BindIterableToHeadersWithContainerDeleteMetadataPrefix}
 * 
 * @author Everett Toews
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindIterableToHeadersWithContainerDeleteMetadataPrefixTest")
public class BindIterableToHeadersWithContainerDeleteMetadataPrefixTest extends CommonSwiftClientTest {

   @Test
   public void testMetadataKeysBind() {
      List<String> metadataKeys = ImmutableList.of("foo", "bar");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      BindIterableToHeadersWithContainerDeleteMetadataPrefix binder = 
         injector.getInstance(BindIterableToHeadersWithContainerDeleteMetadataPrefix.class);
      
      HttpRequest actualRequest = binder.bindToRequest(request, metadataKeys);
      HttpRequest expectedRequest = HttpRequest.builder()
         .method("PUT")
         .endpoint("http://localhost")
         .addHeader(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "foo", "")
         .addHeader(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "bar", "")
         .build(); 

      assertEquals(actualRequest, expectedRequest);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullListIsBad() {
      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      BindIterableToHeadersWithContainerDeleteMetadataPrefix binder = 
         injector.getInstance(BindIterableToHeadersWithContainerDeleteMetadataPrefix.class);
      
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullRequestIsBad() {
      List<String> metadataKeys = ImmutableList.of("foo", "bar");
      BindIterableToHeadersWithContainerDeleteMetadataPrefix binder = 
         injector.getInstance(BindIterableToHeadersWithContainerDeleteMetadataPrefix.class);
      
      binder.bindToRequest(null, metadataKeys);
   }
}
