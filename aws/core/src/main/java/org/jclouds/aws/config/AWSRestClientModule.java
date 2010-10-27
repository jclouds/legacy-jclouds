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

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.Region;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Configures the S3 connection, including logging and http transport.
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

   @Provides
   @Singleton
   @Region
   protected Map<String, URI> provideRegions(Injector injector) {
      String regionString = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGIONS)));
      Map<String, URI> regions = newLinkedHashMap();
      for (String region : Splitter.on(',').split(regionString)) {
         regions.put(
               region,
               URI.create(injector.getInstance(Key.get(String.class,
                     Names.named(Constants.PROPERTY_ENDPOINT + "." + region)))));
      }
      return regions;
   }

   @Provides
   @Singleton
   @Region
   protected Set<String> provideRegions(@Region Map<String, URI> map) {
      return map.keySet();
   }

   @Provides
   @Singleton
   @Region
   protected String getDefaultRegion(@Provider final URI uri, @Region Map<String, URI> map, LoggerFactory logFactory) {
      try {
         return find(map.entrySet(), new Predicate<Entry<String, URI>>() {

            @Override
            public boolean apply(Entry<String, URI> input) {
               return input.getValue().equals(uri);
            }

         }).getKey();
      } catch (NoSuchElementException e) {
         String region = get(map.keySet(), 0);
         logFactory.getLogger("jclouds.compute").warn(
               "failed to find region for current endpoint %s in %s; choosing first: %s", uri, map, region);
         return region;
      }
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

}