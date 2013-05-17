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
package org.jclouds.cloudfiles.binders;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.swift.CommonSwiftClientTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code BindIterableToHeadersWithPurgeCDNObjectEmail}
 * 
 * @author Everett Toews
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindIterableToHeadersWithPurgeCDNObjectEmailTest")
public class BindIterableToHeadersWithPurgeCDNObjectEmailTest extends CommonSwiftClientTest {

   @Test
   public void testEmailBind() {
      List<String> emails = ImmutableList.of("foo@bar.com", "bar@foo.com");

      HttpRequest request = HttpRequest.builder().method("DELETE").endpoint("http://localhost").build();
      BindIterableToHeadersWithPurgeCDNObjectEmail binder = 
         injector.getInstance(BindIterableToHeadersWithPurgeCDNObjectEmail.class);
      
      HttpRequest actualRequest = binder.bindToRequest(request, emails);
      HttpRequest expectedRequest = HttpRequest.builder()
         .method("DELETE")
         .endpoint("http://localhost")
         .addHeader(CloudFilesHeaders.CDN_CONTAINER_PURGE_OBJECT_EMAIL, "foo@bar.com, bar@foo.com")
         .build(); 

      assertEquals(actualRequest, expectedRequest);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullListIsBad() {
      HttpRequest request = HttpRequest.builder().method("DELETE").endpoint("http://localhost").build();
      BindIterableToHeadersWithPurgeCDNObjectEmail binder = 
         injector.getInstance(BindIterableToHeadersWithPurgeCDNObjectEmail.class);
      
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullRequestIsBad() {
      List<String> emails = ImmutableList.of("foo@bar.com", "bar@foo.com");
      BindIterableToHeadersWithPurgeCDNObjectEmail binder = 
         injector.getInstance(BindIterableToHeadersWithPurgeCDNObjectEmail.class);
      
      binder.bindToRequest(null, emails);
   }
}
