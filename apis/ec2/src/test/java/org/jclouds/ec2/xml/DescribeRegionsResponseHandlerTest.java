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
import java.net.URI;
import java.util.Map;

import org.jclouds.aws.domain.Region;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Provider;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code DescribeRegionsResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeRegionsResponseHandlerTest")
public class DescribeRegionsResponseHandlerTest extends BaseHandlerTest {
   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<URI>>(){}).annotatedWith(Provider.class).toInstance(
                  Suppliers.ofInstance(URI.create("https://booya")));
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream(
            "/regionEndpoints.xml");

      Map<String, URI> expected = ImmutableMap
            .<String, URI> of(Region.EU_WEST_1, URI
                  .create("https://ec2.eu-west-1.amazonaws.com"),
                  Region.US_EAST_1, URI
                        .create("https://ec2.us-east-1.amazonaws.com"),
                  Region.US_WEST_1, URI
                        .create("https://ec2.us-west-1.amazonaws.com"));

      Map<String, URI> result = factory.create(
            injector.getInstance(DescribeRegionsResponseHandler.class)).parse(
            is);

      assertEquals(result, expected);
   }

   public void testEuc() {

      InputStream is = Strings2
            .toInputStream("<DescribeRegionsResponse xmlns=\"http://ec2.amazonaws.com/doc/2010-06-15/\"><requestId>6a3b36f9-9ff4-47cf-87e3-285b08fbe5e5</requestId><regionInfo><item><regionName>Eucalyptus</regionName><regionEndpoint>http://173.205.188.130:8773/services/Eucalyptus</regionEndpoint></item><item><regionName>Walrus</regionName><regionEndpoint>http://173.205.188.130:8773/services/Walrus</regionEndpoint></item></regionInfo></DescribeRegionsResponse>");

      Map<String, URI> expected = ImmutableMap.<String, URI> of("Eucalyptus",
            URI.create("http://173.205.188.130:8773/services/Eucalyptus"));

      Map<String, URI> result = factory.create(
            injector.getInstance(DescribeRegionsResponseHandler.class)).parse(
            is);

      assertEquals(result, expected);
   }
   
   public void testEuc2() {

      InputStream is = Strings2
            .toInputStream("<?xml version=\"1.0\" ?><DescribeRegionsResponse xmlns=\"http://ec2.amazonaws.com/doc/2009-11-30/\"><requestId>1LAQRTCLTLPS6CEIC627</requestId><regionInfo><item><regionUrl>http://10.255.255.1:8773/services/Cloud</regionUrl><regionName>nova</regionName></item></regionInfo></DescribeRegionsResponse>");

      Map<String, URI> expected = ImmutableMap.<String, URI> of("nova",
            URI.create("http://10.255.255.1:8773/services/Cloud"));

      Map<String, URI> result = factory.create(
            injector.getInstance(DescribeRegionsResponseHandler.class)).parse(
            is);

      assertEquals(result, expected);
   }

   public void testUnsupportedAdditionalRegionDoesntBreak() {

      InputStream is = getClass().getResourceAsStream(
            "/regionEndpoints-additional.xml");

      Map<String, URI> expected = ImmutableMap
            .<String, URI> of("jp-west-1", URI
                  .create("https://ec2.jp-west-1.amazonaws.com"),
                  Region.EU_WEST_1, URI
                        .create("https://ec2.eu-west-1.amazonaws.com"),
                  Region.US_EAST_1, URI
                        .create("https://ec2.us-east-1.amazonaws.com"),
                  Region.US_WEST_1, URI
                        .create("https://ec2.us-west-1.amazonaws.com"));

      Map<String, URI> result = factory.create(
            injector.getInstance(DescribeRegionsResponseHandler.class)).parse(
            is);

      assertEquals(result, expected);
   }
}
