/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.http.functions;

import static com.google.common.base.Throwables.propagate;

import java.util.List;

import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.google.common.collect.ImmutableList;
import org.jclouds.reflect.Invokable;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
public class BaseHandlerTest {

   protected Injector injector = null;
   protected ParseSax.Factory factory;
   protected GeneratedHttpRequest request;
   private Invocation toString;

   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   @BeforeTest
   protected void setUpRequest() {
      try {
         toString = Invocation.create(Invokable.from(String.class.getDeclaredMethod("toString")), ImmutableList.of());
      } catch (SecurityException e) {
         throw propagate(e);
      } catch (NoSuchMethodException e) {
         throw propagate(e);
      }
      request = GeneratedHttpRequest.builder().method("POST").endpoint("http://localhost/key").invocation(toString)
            .build();
   }

   @AfterTest
   protected void tearDownInjector() {
      factory = null;
      injector = null;
   }

   protected GeneratedHttpRequest requestForArgs(List<Object> args) {
      return GeneratedHttpRequest.builder().method("POST").endpoint("http://localhost/key")
            .invocation(Invocation.create(toString.getInvokable(), args)).build();
   }
}
