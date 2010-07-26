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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.config.DateSecondsAdapter;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "gogrid.ParseCredentialsFromJsonResponseTest")
public class ParseCredentialsFromJsonResponseTest {

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailWhenTooManyPasswords() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream(
            "/test_credentials_list.json");

      HttpResponse response = new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is));

      ParseCredentialsFromJsonResponse parser = i
            .getInstance(ParseCredentialsFromJsonResponse.class);
      parser.apply(response);
   }

   @Test
   public void testValid() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream(
            "/test_credential.json");

      HttpResponse response = new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is));

      ParseCredentialsFromJsonResponse parser = i
            .getInstance(ParseCredentialsFromJsonResponse.class);
      Credentials creds = parser.apply(response);
      assertEquals(creds.identity, "root");
      assertEquals(creds.credential, "dig44sos");

   }

   Injector i = Guice.createInjector(new GsonModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(DateSecondsAdapter.class);
         super.configure();
      }

      @Provides
      @Singleton
      @SuppressWarnings("unused")
      @com.google.inject.name.Named(Constants.PROPERTY_GSON_ADAPTERS)
      public Map<Type, Object> provideCustomAdapterBindings() {
         Map<Type, Object> bindings = Maps.newHashMap();
         bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
         bindings.put(ServerImageType.class,
               new CustomDeserializers.ServerImageTypeAdapter());
         bindings.put(ServerImageState.class,
               new CustomDeserializers.ServerImageStateAdapter());
         bindings.put(ServerImageState.class,
               new CustomDeserializers.ServerImageStateAdapter());
         return bindings;
      }
   });

}
