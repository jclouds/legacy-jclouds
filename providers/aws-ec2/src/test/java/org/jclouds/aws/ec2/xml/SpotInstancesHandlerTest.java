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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.date.DateService;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SpotInstancesHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "SpotInstancesHandlerTest")
public class SpotInstancesHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>(){}).annotatedWith(Region.class).toInstance(Suppliers.ofInstance("us-east-1"));
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testDescribe() {

      InputStream is = getClass().getResourceAsStream("/describe_spot_instance_requests.xml");
      SpotInstancesHandler handler = injector.getInstance(SpotInstancesHandler.class);
      addDefaultRegionToHandler(handler);
      Set<SpotInstanceRequest> result = factory.create(handler).parse(is);
      assertEquals(result.size(), 18);
   }

   public void testRequest() {
      InputStream is = getClass().getResourceAsStream("/request_spot_instances.xml");
      SpotInstancesHandler handler = injector.getInstance(SpotInstancesHandler.class);
      addDefaultRegionToHandler(handler);
      Set<SpotInstanceRequest> result = factory.create(handler).parse(is);
      assertEquals(result.size(), 3);
   }

   public void testParseTags() {
       Set<SpotInstanceRequest> result = factory.create(injector.getInstance(SpotInstancesHandler.class)).parse(
            getClass().getResourceAsStream("/describe_spot_instance_tags.xml"));
       Iterable<Map<String, String>> tags = Iterables.transform(result, new Function<SpotInstanceRequest, Map<String, String>>() {
	       @Override
	       public Map<String, String> apply(SpotInstanceRequest input) {
		       return input.getTags();
	       }
       });

       assertEquals(
               Iterables.get(tags, 0),
               ImmutableMap.of("One", "one", "Two", "one"));
       assertEquals(
               Iterables.get(tags, 1),
               ImmutableMap.of("One", "two", "Two", "two"));
       assertEquals(
               Iterables.get(tags, 2),
               ImmutableMap.of("Two", "three", "Three", "three", "Four", ""));
   }

   public void testParseNoNPE() {
      factory.create(injector.getInstance(SpotInstancesHandler.class)).parse(
            getClass().getResourceAsStream("/describe_spot_instances_1.xml"));
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
