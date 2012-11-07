/*
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
package org.jclouds.oauth.v2.config;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.functions.BuildTokenRequest;
import org.jclouds.oauth.v2.functions.FetchToken;
import org.jclouds.oauth.v2.functions.OAuthCredentialsSupplier;
import org.jclouds.oauth.v2.functions.SignOrProduceMacForToken;
import org.jclouds.oauth.v2.json.ClaimSetTypeAdapter;
import org.jclouds.oauth.v2.json.HeaderTypeAdapter;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

/**
 * Base OAuth module
 *
 * @author David Alves
 */
public class OAuthModule extends AbstractModule {


   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<byte[], byte[]>>() {}).to(SignOrProduceMacForToken.class);
      bind(new TypeLiteral<Map<Type, Object>>() {}).toInstance(ImmutableMap.<Type, Object>of(
              Header.class, new HeaderTypeAdapter(),
              ClaimSet.class, new ClaimSetTypeAdapter()));
      bind(new TypeLiteral<Supplier<OAuthCredentials>>() {}).to(OAuthCredentialsSupplier.class);
      bind(new TypeLiteral<Function<GeneratedHttpRequest, TokenRequest>>() {}).to(BuildTokenRequest.class);
      bind(new TypeLiteral<Function<TokenRequest, Token>>() {}).to(FetchToken.class);
   }

   /**
    * Provides a cache for tokens. Cache is time based and expires after 59 minutes (the maximum time a token is
    * valid is 60 minutes)
    */
   @Provides
   @Singleton
   public LoadingCache<TokenRequest, Token> provideAccessCache(Function<TokenRequest, Token> getAccess,
                                                               @Named(PROPERTY_SESSION_INTERVAL) long
                                                                       sessionIntervalInSeconds) {
      // since the session interval is also the token expiration time requested to the server make the token expire a
      // bit before the deadline to make sure there aren't session expiration exceptions
      sessionIntervalInSeconds = sessionIntervalInSeconds > 30 ? sessionIntervalInSeconds - 30 :
              sessionIntervalInSeconds;
      return CacheBuilder.newBuilder().expireAfterWrite(sessionIntervalInSeconds, TimeUnit.MINUTES).build(CacheLoader
              .from(getAccess));
   }

}
