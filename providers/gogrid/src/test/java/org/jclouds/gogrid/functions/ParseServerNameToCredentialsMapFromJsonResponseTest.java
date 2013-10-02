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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Oleksiy Yarmula
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseServerNameToCredentialsMapFromJsonResponseTest")
public class ParseServerNameToCredentialsMapFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_credentials_list.json");

      ParseServerNameToCredentialsMapFromJsonResponse parser = i
            .getInstance(ParseServerNameToCredentialsMapFromJsonResponse.class);
      Map<String, Credentials> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response.size(), 6);
   }

   Injector i = Guice.createInjector(new GsonModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(LongDateAdapter.class);
         super.configure();
      }

      @Provides
      @Singleton
      public Map<Type, Object> provideCustomAdapterBindings() {
         Map<Type, Object> bindings = Maps.newHashMap();
         bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
         bindings.put(ServerImageType.class, new CustomDeserializers.ServerImageTypeAdapter());
         bindings.put(ServerImageState.class, new CustomDeserializers.ServerImageStateAdapter());
         bindings.put(ServerState.class, new CustomDeserializers.ServerStateAdapter());
         return bindings;
      }
   });

}
