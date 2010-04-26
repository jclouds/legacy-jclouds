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
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.EC2PropertiesBuilder;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.TimeStamp;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeTest;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * 
 * 
 * @author Adrian Cole
 */
public abstract class BaseEC2AsyncClientTest<T> extends RestClientTest<T> {

   protected FormSigner filter;

   @Override
   protected void checkFilters(GeneratedHttpRequest<T> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   public BaseEC2AsyncClientTest() {
      super();
   }

   @Override
   @BeforeTest
   protected void setupFactory() {
      super.setupFactory();
      this.filter = injector.getInstance(FormSigner.class);
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), checkNotNull(new EC2PropertiesBuilder("user", "key")
                     .build(), "properties"));
            bind(URI.class).annotatedWith(EC2.class).toInstance(
                     URI.create("https://ec2.amazonaws.com"));
            bind(Region.class).annotatedWith(EC2.class).toInstance(Region.US_EAST_1);
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         String provide() {
            return "2009-11-08T15:54:08.897Z";
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @EC2
         Map<Region, URI> provideMap() {
            return ImmutableMap.<Region, URI> of(Region.EU_WEST_1, URI
                     .create("https://ec2.eu-west-1.amazonaws.com"), Region.US_EAST_1, URI
                     .create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
                     .create("https://ec2.us-west-1.amazonaws.com"));
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         Map<AvailabilityZone, Region> provideAvailabilityZoneRegionMap() {
            return ImmutableMap.<AvailabilityZone, Region> of(AvailabilityZone.US_EAST_1A,
                     Region.US_EAST_1);
         }
      };
   }

}