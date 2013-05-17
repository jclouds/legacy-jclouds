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
package org.jclouds.cloudsigma.binders;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindCloneDriveOptionsToPlainTextStringTest {

   private static final BindCloneDriveOptionsToPlainTextString binder = Guice.createInjector().getInstance(
         BindCloneDriveOptionsToPlainTextString.class);

   public void testDefault() throws IOException {
      String expected = "name newdrive";
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of());

      Map<String, Object> map = ImmutableMap.<String, Object> of("name", "newdrive");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testWithSize() throws IOException {
      String expected = "name newdrive\nsize 1024";
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(new CloneDriveOptions().size(1024)));

      Map<String, Object> map = ImmutableMap.<String, Object> of("name", "newdrive");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   protected GeneratedHttpRequest requestForArgs(List<Object> args) {
      try {
         Invocation invocation = Invocation.create(method(String.class, "toString"), args);
         return GeneratedHttpRequest.builder().method("POST").endpoint(URI.create("http://localhost/key"))
               .invocation(invocation).build();
      } catch (SecurityException e) {
         throw Throwables.propagate(e);
      }
   }
}
