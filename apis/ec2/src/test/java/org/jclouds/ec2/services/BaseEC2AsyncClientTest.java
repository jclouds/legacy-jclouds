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
package org.jclouds.ec2.services;

import static com.google.common.collect.Maps.transformValues;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.compute.domain.Image;
import org.jclouds.date.DateService;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.util.Suppliers2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseEC2AsyncClientTest<T> extends BaseAsyncClientTest<T> {
   @ConfiguresRestClient
   protected static class StubEC2RestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {

      @Provides
      @Singleton
      LoadingCache<RegionAndName, Image> provide(){
         return CacheBuilder.newBuilder().build(new CacheLoader<RegionAndName, Image>() {

            @Override
            public Image load(RegionAndName key) throws Exception {
               return null;
            }

         });
      }
      
      @Override
      protected String provideTimeStamp(DateService dateService) {
         return "2009-11-08T15:54:08.897Z";
      }

      static class Zones implements javax.inject.Provider<Map<String, String>> {
         @Override
         public Map<String, String> get() {
            return ImmutableMap.<String, String> of("us-east-1a", "us-east-1");
         }
      }

      @Override
      protected void installLocations() {
         install(new LocationModule());
         bind(RegionIdToURISupplier.class).toInstance(new RegionIdToURISupplier() {

            @Override
            public Map<String, Supplier<URI>> get() {
               return transformValues(ImmutableMap.<String, URI> of(Region.EU_WEST_1, URI
                        .create("https://ec2.eu-west-1.amazonaws.com"), Region.US_EAST_1, URI
                        .create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
                        .create("https://ec2.us-west-1.amazonaws.com")), Suppliers2.<URI> ofInstanceFunction());
            }

         });
         bind(RegionIdToZoneIdsSupplier.class).toInstance(new RegionIdToZoneIdsSupplier() {

            @Override
            public Map<String, Supplier<Set<String>>> get() {
               return transformValues(ImmutableMap.<String, Set<String>> of("us-east-1", ImmutableSet.of(
                        "us-east-1a", "us-east-1b", "us-east-1c", "us-east-1b")), Suppliers2
                        .<Set<String>> ofInstanceFunction());
            }

         });
      }
   }

   protected FormSigner filter;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   @BeforeTest
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.filter = injector.getInstance(FormSigner.class);
   }

   @Override
   protected Module createModule() {
      return new StubEC2RestClientModule();
   }
   
   @Override
   protected ApiMetadata createApiMetadata() {
      return new EC2ApiMetadata();
   }
}
