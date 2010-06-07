/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs2.config;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSAsyncClient;
import org.jclouds.mezeo.pcs2.PCSClient;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.internal.StubPCSAsyncClient;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.inject.TypeLiteral;

/**
 * adds a stub alternative to invoking PCSBlob
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class PCSStubClientModule extends RestClientModule<PCSClient, PCSAsyncClient> {
   static final ConcurrentHashMap<String, Map<String, PCSFile>> map = new ConcurrentHashMap<String, Map<String, PCSFile>>();

   public PCSStubClientModule() {
      super(PCSClient.class, PCSAsyncClient.class);
   }

   protected void configure() {
      super.configure();
      install(new PCSObjectModule());
      bind(new TypeLiteral<Map<String, Map<String, PCSFile>>>() {
      }).toInstance(map);
      bind(URI.class).annotatedWith(PCS.class).toInstance(URI.create("https://localhost/pcsblob"));
      bind(URI.class).annotatedWith(RootContainer.class).toInstance(
               URI.create("http://localhost/root"));
   }

   @Override
   protected void bindAsyncClient() {
      bind(PCSAsyncClient.class).to(StubPCSAsyncClient.class).asEagerSingleton();
   }
}