/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rest.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.TransformingMap;
import org.jclouds.domain.Credentials;
import org.jclouds.io.CopyInputStreamInputSupplierMap;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ConfiguresCredentialStore;
import org.jclouds.util.Strings2;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.io.InputSupplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Beta
@ConfiguresCredentialStore
public class CredentialStoreModule extends AbstractModule {
   private static final Map<String, InputSupplier<InputStream>> BACKING = new ConcurrentHashMap<String, InputSupplier<InputStream>>();
   private final Map<String, InputStream> backing;

   public CredentialStoreModule(Map<String, InputStream> backing) {
      this.backing = backing;
   }

   public CredentialStoreModule() {
      this(null);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Credentials, InputStream>>() {
      }).to(CredentialsToJsonInputStream.class);
      bind(new TypeLiteral<Function<InputStream, Credentials>>() {
      }).to(CredentialsFromJsonInputStream.class);
      if (backing != null) {
         bind(new TypeLiteral<Map<String, InputStream>>() {
         }).toInstance(backing);
      } else {
         bind(new TypeLiteral<Map<String, InputSupplier<InputStream>>>() {
         }).toInstance(BACKING);
         bind(new TypeLiteral<Map<String, InputStream>>() {
         }).to(new TypeLiteral<CopyInputStreamInputSupplierMap>() {
         });
      }
   }

   @Singleton
   public static class CredentialsToJsonInputStream implements Function<Credentials, InputStream> {
      private final Json json;

      @Inject
      CredentialsToJsonInputStream(Json json) {
         this.json = json;
      }

      @Override
      public InputStream apply(Credentials from) {
         return Strings2.toInputStream(json.toJson(checkNotNull(from)));
      }
   }

   @Singleton
   public static class CredentialsFromJsonInputStream implements Function<InputStream, Credentials> {
      @Resource
      protected Logger logger = Logger.NULL;

      private final Json json;

      @Inject
      CredentialsFromJsonInputStream(Json json) {
         this.json = json;
      }

      private static class PrivateCredentials {
         String identity;
         String credential;
      }

      @Override
      public Credentials apply(InputStream from) {
         try {
            PrivateCredentials credentials = json.fromJson(Strings2.toStringAndClose(checkNotNull(from)),
                  PrivateCredentials.class);
            return new Credentials(credentials.identity, credentials.credential);
         } catch (Exception e) {
            logger.warn(e, "ignoring problem retrieving credentials");
            return null;
         }
      }
   }

   @Provides
   @Singleton
   protected Map<String, Credentials> provideCredentialStore(Map<String, InputStream> backing,
         Function<Credentials, InputStream> credentialsSerializer,
         Function<InputStream, Credentials> credentialsDeserializer) {
      return new TransformingMap<String, InputStream, Credentials>(backing, credentialsDeserializer,
            credentialsSerializer);
   }
}