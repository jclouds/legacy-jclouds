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

import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest.State;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest.Type;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SpotInstanceHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "SpotInstanceHandlerTest")
public class SpotInstanceHandlerTest extends BaseEC2HandlerTest {

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

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/request_spot_instances-ebs.xml");

      SpotInstanceRequest expected = SpotInstanceRequest
            .builder()
            .region("us-east-1")
            .id("sir-228e6406")
            .spotPrice(0.001f)
            .type(Type.ONE_TIME)
            .state(State.OPEN)
            .rawState("open")
            .launchSpecification(
                  LaunchSpecification.builder().imageId("ami-595a0a1c").securityGroupIdToName("sg-83e1c4ea", "default")
                        .instanceType("m1.large").mapNewVolumeToDevice("/dev/sda1", 1, true)
                        .mapEBSSnapshotToDevice("/dev/sda2", "snap-1ea27576", 1, true)
                        .mapEphemeralDeviceToDevice("/dev/sda3", "vre1").monitoringEnabled(false).build())
            .createTime(new SimpleDateFormatDateService().iso8601DateParse("2011-03-08T03:30:36.000Z"))
            .productDescription("Linux/UNIX").build();
      SpotInstanceHandler handler = injector.getInstance(SpotInstanceHandler.class);
      addDefaultRegionToHandler(handler);
      SpotInstanceRequest result = factory.create(handler).parse(is);
      assertEquals(result.toString(), expected.toString());
      assertEquals(result.getState(), State.OPEN);
      assertEquals(result.getRawState(), "open");

   }

   public void testApplyInputStream1() {

      InputStream is = getClass().getResourceAsStream("/describe_spot_instance.xml");

      SpotInstanceRequest expected = SpotInstanceRequest
            .builder()
            .region("us-east-1")
            .id("sir-1ede0012")
            .instanceId("i-ef308e8e")
            .spotPrice(0.300000f)
            .type(Type.ONE_TIME)
            .state(State.ACTIVE)
            .rawState("active")
            .launchedAvailabilityZone("us-east-1b")
            .launchSpecification(
                  LaunchSpecification.builder().imageId("ami-8e1fece7")
                        .securityGroupIdToName("sg-83e1c4eb", "jclouds#adriancole-ec2unssh#us-east-1")
                        .instanceType("t1.micro").monitoringEnabled(false).keyName("jclouds#adriancole-ec2unssh")
                        .build())
            .createTime(new SimpleDateFormatDateService().iso8601DateParse("2011-07-29T05:27:39.000Z"))
            .productDescription("Linux/UNIX")
            .tag("Name", "ec2-o")
            .tag("Spot", "spot-value")
            .tag("Empty", "")
            .build();
      SpotInstanceHandler handler = injector.getInstance(SpotInstanceHandler.class);
      addDefaultRegionToHandler(handler);
      SpotInstanceRequest result = factory.create(handler).parse(is);
      assertEquals(result.toString(), expected.toString());
      assertEquals(result.getState(), State.ACTIVE);
      assertEquals(result.getRawState(), "active");
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
