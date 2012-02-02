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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.domain.Spot;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.location.Region;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code DescribeSpotPriceHistoryResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeSpotPriceHistoryResponseHandlerTest")
public class DescribeSpotPriceHistoryResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/describe_spot_price_history.xml");

      Set<Spot> expected = ImmutableSet.of(
            Spot.builder().region("us-west-1").instanceType("t1.micro").productDescription("SUSE Linux").spotPrice(0.013f)
                  .timestamp(new SimpleDateFormatDateService().iso8601DateParse("2011-03-07T12:17:19.000Z")).build(),
            Spot.builder().region("us-west-1").instanceType("m1.large").productDescription("Linux/UNIX").spotPrice(0.119f)
                  .timestamp(new SimpleDateFormatDateService().iso8601DateParse("2011-03-07T16:29:16.000Z")).build(),
            Spot.builder().region("us-west-1").instanceType("t1.micro").productDescription("Windows").spotPrice(0.013f)
                  .timestamp(new SimpleDateFormatDateService().iso8601DateParse("2011-03-07T17:56:54.000Z")).build()

      );

      Set<Spot> result = factory.create(injector.createChildInjector(new AbstractModule(){

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>(){}).annotatedWith(Region.class).toInstance(Suppliers.ofInstance("us-west-1"));
         }
         
      }).getInstance(DescribeSpotPriceHistoryResponseHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}
