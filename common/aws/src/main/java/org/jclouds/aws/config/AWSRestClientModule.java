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

package org.jclouds.aws.config;

import static com.google.common.collect.Iterables.get;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.config.ProvideRegionToURIViaProperties;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableBiMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class AWSRestClientModule<S, A> extends RestClientModule<S, A> {

   public AWSRestClientModule(Class<S> syncClientType, Class<A> asyncClientType, Map<Class<?>, Class<?>> delegates) {
      super(syncClientType, asyncClientType, delegates);
   }

   public AWSRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAWSErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(AWSRedirectionRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(AWSClientErrorRetryHandler.class);
   }

   @Provides
   @Singleton
   @Nullable
   @Region
   protected String getDefaultRegion(@Provider URI uri, @Region Map<String, URI> map, LoggerFactory logFactory) {
      String region = ImmutableBiMap.copyOf(map).inverse().get(uri);
      if (region == null && map.size() > 0) {
         logFactory.getLogger(getClass().getName()).warn(
                  "failed to find region for current endpoint %s in %s; choosing first: %s", uri, map, region);
         region = get(map.keySet(), 0);
      }
      return region;
   }

   protected void bindRegionsToProvider() {
      bindRegionsToProvider(ProvideRegionToURIViaProperties.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bindRegionsToProvider();
   }

   protected void bindRegionsToProvider(Class<? extends javax.inject.Provider<Map<String, URI>>> providerClass) {
      bind(new TypeLiteral<Map<String, URI>>() {
      }).annotatedWith(Region.class).toProvider(providerClass).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   @Region
   protected Set<String> provideRegions(@Region Map<String, URI> map) {
      return map.keySet();
   }

}