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
package org.jclouds.rest.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.inject.Provider;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * Tests behavior of {@code BindMapToMatrixParams}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindMapToMatrixParamsTest {

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      BindMapToMatrixParams binder = new BindMapToMatrixParams(new Provider<UriBuilder>() {

         @Override
         public UriBuilder get() {
            return new UriBuilderImpl();
         }

      });

      assertEquals(binder.bindToRequest(request, ImmutableMap.of("imageName", "foo", "serverId", "2")), HttpRequest
            .builder().method("GET").endpoint(URI.create("http://momma/;imageName=foo;serverId=2")).build());

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeMap() {
      BindMapToMatrixParams binder = new BindMapToMatrixParams(null);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindMapToMatrixParams binder = new BindMapToMatrixParams(null);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }
}
