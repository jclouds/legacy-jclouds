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
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Tests behavior of {@code BindToJsonPayloadWrappedWith}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindToJsonPayloadWrappedWithTest {

   Injector injector = Guice.createInjector(new GsonModule(), new FactoryModuleBuilder()
            .build(BindToJsonPayloadWrappedWith.Factory.class));

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {
      BindToJsonPayloadWrappedWith binder = new BindToJsonPayloadWrappedWith(injector
               .getInstance(BindToJsonPayload.class), "envelope");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      request = binder.bindToRequest(request, ImmutableMap.of("imageName", "foo", "serverId", "2"));
      assertEquals(request.getPayload().getRawContent(), "{\"envelope\":{\"imageName\":\"foo\",\"serverId\":\"2\"}}");

   }

   @Test
   public void testFactoryCorrect() throws SecurityException, NoSuchMethodException {
      BindToJsonPayloadWrappedWith binder = injector.getInstance(BindToJsonPayloadWrappedWith.Factory.class).create(
               "envelope");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      request = binder.bindToRequest(request, ImmutableMap.of("imageName", "foo", "serverId", "2"));
      assertEquals(request.getPayload().getRawContent(), "{\"envelope\":{\"imageName\":\"foo\",\"serverId\":\"2\"}}");

   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      BindToJsonPayloadWrappedWith binder = new BindToJsonPayloadWrappedWith(injector
               .getInstance(BindToJsonPayload.class), "envelope");
      binder.bindToRequest(HttpRequest.builder().method("GET").endpoint("http://momma").build(), null);
   }
}
