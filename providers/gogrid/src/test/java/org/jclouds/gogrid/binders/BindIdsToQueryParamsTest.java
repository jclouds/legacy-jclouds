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
package org.jclouds.gogrid.binders;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Tests that id bindings are proper for request
 * 
 * @author Oleksiy Yarmula
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindIdsToQueryParamsTest")
public class BindIdsToQueryParamsTest {

   @Test
   public void testWithView() throws SecurityException, NoSuchMethodException {

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma/").build();

      BindIdsToQueryParams binder = new BindIdsToQueryParams();

      request = binder.bindToRequest(request, new Long[] { 123L, 456L });

      assertEquals(request.getRequestLine(), "GET http://momma/?id=123&id=456 HTTP/1.1");
   }

   @Test
   public void testWithPrimitive() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma/").build();

      BindIdsToQueryParams binder = new BindIdsToQueryParams();

      request = binder.bindToRequest(request, new long[] { 123L, 456L });

      assertEquals(request.getRequestLine(), "GET http://momma/?id=123&id=456 HTTP/1.1");
   }
}
