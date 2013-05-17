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
package org.jclouds.cloudwatch.internal;

import static com.google.common.collect.Maps.transformValues;

import java.net.URI;
import java.util.Map;

import org.jclouds.aws.domain.Region;
import org.jclouds.cloudwatch.config.CloudWatchRestClientModule;
import org.jclouds.date.DateService;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseCloudWatchExpectTest<T> extends BaseRestApiExpectTest<T> {

   public BaseCloudWatchExpectTest() {
      provider = "cloudwatch";
   }
   
   @ConfiguresRestClient
   private static final class TestMonitoringRestClientModule extends CloudWatchRestClientModule {

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
      }

      @Override
      protected String provideTimeStamp(final DateService dateService) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestMonitoringRestClientModule();
   }
}
