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
package org.jclouds.rest.binders;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindAsHostPrefix}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindAsHostPrefixTest")
public class BindAsHostPrefixIfConfiguredTest {

   public void testPrefixValid() {

      BindAsHostPrefix binder = new BindAsHostPrefix();

      HttpRequest request = binder.bindToRequest(HttpRequest.builder().method("GET").endpoint("https://s3.amazonaws.com").build(),
            "bucket");
      assertEquals(request.getRequestLine(), "GET https://bucket.s3.amazonaws.com HTTP/1.1");

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPrefixInvalidHostname() {

      BindAsHostPrefix binder = new BindAsHostPrefix();

      binder.bindToRequest(HttpRequest.builder().method("GET").endpoint("https://s3.amazonaws.com").build(), "b_ucket");

   }
}
