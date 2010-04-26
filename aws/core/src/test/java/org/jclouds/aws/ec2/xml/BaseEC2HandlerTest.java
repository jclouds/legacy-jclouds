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
package org.jclouds.aws.ec2.xml;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.BeforeTest;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class BaseEC2HandlerTest extends BaseHandlerTest {
   protected Region defaultRegion = Region.US_EAST_1;

   public BaseEC2HandlerTest() {
      super();
   }

   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {

         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @EC2
         Region provideDefaultRegion() {
            return defaultRegion;
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         Map<AvailabilityZone, Region> provideAvailabilityZoneRegionMap() {
            return ImmutableMap.<AvailabilityZone, Region> of(AvailabilityZone.US_EAST_1A,
                     Region.US_EAST_1);
         }
      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

}