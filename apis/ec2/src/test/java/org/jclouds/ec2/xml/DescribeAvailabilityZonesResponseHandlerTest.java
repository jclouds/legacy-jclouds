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
package org.jclouds.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code DescribeAvailabilityZonesResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeAvailabilityZonesResponseHandlerTest")
public class DescribeAvailabilityZonesResponseHandlerTest extends BaseHandlerTest {

   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule() {
         
         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @org.jclouds.location.Region
         Supplier<String> provideDefaultRegion() {
            return Suppliers.ofInstance("SHOULDNTSEETHISASXMLHASREGIONDATA");
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/availabilityZones.xml");

      Set<AvailabilityZoneInfo> expected = ImmutableSet.<AvailabilityZoneInfo> of(

      new AvailabilityZoneInfo("us-east-1a", "available", "us-east-1", ImmutableSet.<String> of()),
               new AvailabilityZoneInfo("us-east-1b", "available", "us-east-1", ImmutableSet
                        .<String> of()),

               new AvailabilityZoneInfo("us-east-1c", "available", "us-east-1", ImmutableSet
                        .<String> of("our service is awesome")),

               new AvailabilityZoneInfo("us-east-1d", "downlikeaclown", "us-east-1", ImmutableSet
                        .<String> of()));
      Set<AvailabilityZoneInfo> result = factory.create(
               injector.getInstance(DescribeAvailabilityZonesResponseHandler.class)).parse(is);

      assertEquals(result, expected);
   }

}
