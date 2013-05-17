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
package org.jclouds.json;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.TransformerForRequest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseParserTest<T, G> {

   @Retention(value = RetentionPolicy.RUNTIME)
   @Target(value = { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
   @Qualifier
   public @interface Nested {

   }

   @SuppressWarnings("unchecked")
   protected Function<HttpResponse, T> parser(Injector i) {
      try {
         return (Function<HttpResponse, T>) i
               .createChildInjector(new SaxParserModule())
               .getInstance(TransformerForRequest.class)
               .getTransformerForMethod(Invocation.create(method(getClass(), "expected"), ImmutableList.of()), i);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @Test
   public void test() {
      T expects = expected();
      Function<HttpResponse, T> parser = parser(injector());
      T response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(payload()).build());
      compare(expects, response);
   }

   protected Payload payload() {
      return Payloads.newInputStreamPayload(getClass().getResourceAsStream(resource()));
   }

   public void compare(T expects, T response) {
      assertEquals(response.toString(), expects.toString());
   }

   protected Injector injector() {
      return Guice.createInjector(new GsonModule() {
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }
      });

   }

   protected String resource() {
      throw new IllegalStateException("please define resource such as \"/testaddresses.json\"");
   }

   public abstract T expected();
}
