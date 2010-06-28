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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.config.EC2RestClientModule;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
public abstract class BaseEC2AsyncClientTest<T> extends RestClientTest<T> {
   @RequiresHttp
   @ConfiguresRestClient
   protected static class StubEC2RestClientModule extends EC2RestClientModule {

      @Override
      protected String provideTimeStamp(final DateService dateService,
               @Named(Constants.PROPERTY_SESSION_INTERVAL) final int expiration) {
         return "2009-11-08T15:54:08.897Z";
      }

      @Override
      protected Map<String, URI> provideRegions(Injector client) {
         return ImmutableMap.<String, URI> of(Region.EU_WEST_1, URI
                  .create("https://ec2.eu-west-1.amazonaws.com"), Region.US_EAST_1, URI
                  .create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
                  .create("https://ec2.us-west-1.amazonaws.com"));
      }

      @Override
      protected Map<String, String> provideAvailabilityZoneToRegions(EC2Client client,
               @org.jclouds.aws.Region Map<String, URI> regions) {
         return ImmutableMap.<String, String> of(AvailabilityZone.US_EAST_1A, Region.US_EAST_1);
      }
   }

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
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.filter = injector.getInstance(FormSigner.class);
   }

   @Override
   protected Module createModule() {
      return new StubEC2RestClientModule();
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("ec2", "identity", "credential",
               new Properties());
   }

}