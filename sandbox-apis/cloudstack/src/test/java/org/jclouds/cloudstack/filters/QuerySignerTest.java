/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.filters;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.PropertiesBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * Tests behavior of {@code QuerySigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "QuerySignerTest")
public class QuerySignerTest {
   @SuppressWarnings("unchecked")
   public static final RestContextSpec<Map, List> DUMMY_SPEC = new RestContextSpec<Map, List>("cloudstack",
            "http://localhost:8080/client/api", "2.2", "", "apiKey", "secretKey", Map.class, List.class,
            PropertiesBuilder.class, (Class) RestContextBuilder.class, ImmutableList.<Module> of(new MockModule(),
                     new NullLoggingModule(), new AbstractModule() {
                        @Override
                        protected void configure() {
                           bind(RequestSigner.class).to(QuerySigner.class);
                        }

                     }));

   @Test
   void testCreateStringToSign() {
      QuerySigner filter = RestContextFactory.createContextBuilder(DUMMY_SPEC).buildInjector().getInstance(
               QuerySigner.class);

      assertEquals(filter.createStringToSign(HttpRequest.builder().method("GET").endpoint(
               URI.create("http://localhost:8080/client/api?command=listZones")).build()),
               "apikey=apikey&command=listzones");
   }

   @Test
   void testFilter() {
      QuerySigner filter = RestContextFactory.createContextBuilder(DUMMY_SPEC).buildInjector().getInstance(
               QuerySigner.class);

      assertEquals(
               filter.filter(
                        HttpRequest.builder().method("GET").endpoint(
                                 URI.create("http://localhost:8080/client/api?command=listZones")).build())
                        .getRequestLine(),
               "GET http://localhost:8080/client/api?command=listZones&apiKey=apiKey&signature=2UG8AcnMaozL3BINdjgkJ%2BRzjEY%3D HTTP/1.1");
   }

   @Test
   void testFilterTwice() {
      QuerySigner filter = RestContextFactory.createContextBuilder(DUMMY_SPEC).buildInjector().getInstance(
               QuerySigner.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(
               URI.create("http://localhost:8080/client/api?command=listZones")).build();
      for (int i = 0; i < 2; i++) {
         request = filter.filter(request);
         assertEquals(
                  request.getRequestLine(),
                  "GET http://localhost:8080/client/api?command=listZones&apiKey=apiKey&signature=2UG8AcnMaozL3BINdjgkJ%2BRzjEY%3D HTTP/1.1");
      }
   }
}