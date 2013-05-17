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
package org.jclouds.ec2.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code BindTagsToIndexedFormParams}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit")
public class BindTagsToIndexedFormParamsTest {
   BindTagsToIndexedFormParams binder = new BindTagsToIndexedFormParams();

   public void test() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();
      request = binder.bindToRequest(request, ImmutableMap.of("one", "alpha", "two", "beta"));
      assertEquals(request.getPayload().getRawContent(),
            "Tag.1.Key=one&Tag.1.Value=alpha&Tag.2.Key=two&Tag.2.Value=beta");
   }

   public void testEmpty() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();
      request = binder.bindToRequest(request, ImmutableSet.of("empty"));
      assertEquals(request.getPayload().getRawContent(), "Tag.1.Key=empty&Tag.1.Value=");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeArray() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }
}
