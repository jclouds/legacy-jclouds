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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code RegionEndpointHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.RegionEndpointHandlerTest")
public class DescribeRegionsResponseHandlerTest extends BaseHandlerTest {
   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(EC2.class).toInstance(URI.create("https://booya"));
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/ec2/regionEndpoints.xml");

      Map<Region, URI> expected = ImmutableMap.<Region, URI> of(Region.EU_WEST_1, URI
               .create("https://ec2.eu-west-1.amazonaws.com"), Region.US_EAST_1, URI
               .create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
               .create("https://ec2.us-west-1.amazonaws.com"));

      Map<Region, URI> result = factory.create(
               injector.getInstance(DescribeRegionsResponseHandler.class)).parse(is);

      assertEquals(result, expected);
   }

   public void testUnsupportedAdditionalRegionDoesntBreak() {

      InputStream is = getClass().getResourceAsStream("/ec2/regionEndpoints-additional.xml");

      Map<Region, URI> expected = ImmutableMap.<Region, URI> of(Region.UNKNOWN, URI
               .create("https://ec2.jp-west-1.amazonaws.com"), Region.EU_WEST_1, URI
               .create("https://ec2.eu-west-1.amazonaws.com"), Region.US_EAST_1, URI
               .create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1, URI
               .create("https://ec2.us-west-1.amazonaws.com"));

      Map<Region, URI> result = factory.create(
               injector.getInstance(DescribeRegionsResponseHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}
