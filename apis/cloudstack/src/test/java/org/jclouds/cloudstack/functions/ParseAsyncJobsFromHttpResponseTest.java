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
package org.jclouds.cloudstack.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseAsyncJobsFromHttpResponseTest {

   Injector injector = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void testCanParse() {
      InputStream is = getClass().getResourceAsStream("/listasyncjobsresponse.json");

      ParseAsyncJobsFromHttpResponse parser = injector.getInstance(ParseAsyncJobsFromHttpResponse.class);
      Set<AsyncJob<?>> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response.size(), 77);
   }

}
