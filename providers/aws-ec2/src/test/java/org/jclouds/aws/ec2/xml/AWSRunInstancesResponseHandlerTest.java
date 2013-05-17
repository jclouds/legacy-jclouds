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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code RunInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "RunInstancesResponseHandlerTest")
public class AWSRunInstancesResponseHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>(){}).annotatedWith(Region.class).toInstance(Suppliers.ofInstance("us-east-1"));
            bind(RunningInstance.Builder.class).to(AWSRunningInstance.Builder.class);
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/run_instances.xml");

      Reservation<? extends AWSRunningInstance> expected = Reservation.<AWSRunningInstance>builder()
               .region(defaultRegion)
               .instance(AWSRunningInstance.builder().region(defaultRegion).groupName("default").amiLaunchIndex("0").imageId(
                        "ami-60a54009").instanceId("i-2ba64342").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("example-key-name").launchTime(
                        dateService.iso8601DateParse("2007-08-07T11:51:50.000Z")).hypervisor(Hypervisor.XEN)
                        .monitoringState(MonitoringState.ENABLED).availabilityZone("us-east-1b").build())
               .instance(AWSRunningInstance.builder().region(defaultRegion).groupName("default").amiLaunchIndex("1").imageId(
                        "ami-60a54009").instanceId("i-2bc64242").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("example-key-name").launchTime(
                        dateService.iso8601DateParse("2007-08-07T11:51:50.000Z")).hypervisor(Hypervisor.XEN)
                        .monitoringState(MonitoringState.ENABLED).availabilityZone("us-east-1b").build())
               .instance(AWSRunningInstance.builder().region(defaultRegion).groupName("default").amiLaunchIndex("2").imageId(
                        "ami-60a54009").instanceId("i-2be64332").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("example-key-name").launchTime(
                        dateService.iso8601DateParse("2007-08-07T11:51:50.000Z")).hypervisor(Hypervisor.XEN)
                        .monitoringState(MonitoringState.ENABLED).availabilityZone("us-east-1b").build())
               .ownerId("AIDADH4IGTRXXKCD")
               .reservationId("r-47a5402e").build();

      AWSRunInstancesResponseHandler handler = injector.getInstance(AWSRunInstancesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Reservation<? extends RunningInstance> result = factory.create(handler).parse(is);
      assertEquals(result.toString(), expected.toString());
   }

   public void testApplyInputStreamDoesntNPE() {

      InputStream is = getClass().getResourceAsStream("/run_instances_1.xml");
      AWSRunInstancesResponseHandler handler = injector.getInstance(AWSRunInstancesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      factory.create(handler).parse(is);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      handler.setContext(request);
   }
}
