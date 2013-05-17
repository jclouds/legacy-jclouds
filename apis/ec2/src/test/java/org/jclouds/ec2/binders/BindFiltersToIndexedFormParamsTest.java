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

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindFiltersToIndexedFormParams}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindFiltersToIndexedFormParamsTest {
   Injector injector = Guice.createInjector();
   BindFiltersToIndexedFormParams binder = injector.getInstance(BindFiltersToIndexedFormParams.class);

   public void test() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();
      request = binder.bindToRequest(request, ImmutableMultimap.<String, String> builder()
                                                               .put("resource-type", "instance")
                                                               .put("key", "stack")
                                                               .putAll("value", "Test", "Production")
                                                               .build());
      assertEquals(
            request.getPayload().getRawContent(),
            "Filter.1.Name=resource-type&Filter.1.Value.1=instance&Filter.2.Name=key&Filter.2.Value.1=stack&Filter.3.Name=value&Filter.3.Value.1=Test&Filter.3.Value.2=Production");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeMultimap() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();;
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }
}
