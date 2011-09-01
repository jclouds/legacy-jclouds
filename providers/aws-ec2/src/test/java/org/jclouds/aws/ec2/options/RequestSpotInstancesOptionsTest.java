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
package org.jclouds.aws.ec2.options;

import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.availabilityZoneGroup;
import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.launchGroup;
import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.type;
import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.validFrom;
import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.validUntil;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of RequestSpotInstancesOptions and RequestSpotInstancesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class RequestSpotInstancesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(RequestSpotInstancesOptions.class);
      assert !String.class.isAssignableFrom(RequestSpotInstancesOptions.class);
   }

   @Test
   public void testAvailabilityZoneGroup() {
      RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
      options.availabilityZoneGroup("test");
      assertEquals(options.buildFormParameters().get("AvailabilityZoneGroup"), Collections.singletonList("test"));
   }

   @Test
   public void testAvailabilityZoneGroupStatic() {
      RequestSpotInstancesOptions options = availabilityZoneGroup("test");
      assertEquals(options.buildFormParameters().get("AvailabilityZoneGroup"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAvailabilityZoneGroupNPE() {
      availabilityZoneGroup(null);
   }

   @Test
   public void testLaunchGroup() {
      RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
      options.launchGroup("test");
      assertEquals(options.buildFormParameters().get("LaunchGroup"), Collections.singletonList("test"));
   }

   @Test
   public void testLaunchGroupStatic() {
      RequestSpotInstancesOptions options = launchGroup("test");
      assertEquals(options.buildFormParameters().get("LaunchGroup"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testLaunchGroupNPE() {
      launchGroup(null);
   }

   @Test
   public void testInstanceType() {
      RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
      options.type(SpotInstanceRequest.Type.PERSISTENT);
      assertEquals(options.buildFormParameters().get("Type"), Collections.singletonList("persistent"));
   }

   @Test
   public void testInstanceTypeStatic() {
      RequestSpotInstancesOptions options = type(SpotInstanceRequest.Type.PERSISTENT);
      assertEquals(options.buildFormParameters().get("Type"), Collections.singletonList("persistent"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInstanceTypeNPE() {
      type(null);
   }

   @Test
   public void testFrom() {
      RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
      options.validFrom(test);
      assertEquals(options.buildFormParameters().get("ValidFrom"),
            Collections.singletonList("1970-05-23T21:21:18Z"));
   }

   Date test = new Date(12345678910l);

   @Test
   public void testFromStatic() {
      RequestSpotInstancesOptions options = validFrom(test);
      assertEquals(options.buildFormParameters().get("ValidFrom"),
            Collections.singletonList("1970-05-23T21:21:18Z"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testFromNPE() {
      validFrom(null);
   }

   @Test
   public void testTo() {
      RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
      options.validUntil(test);
      assertEquals(options.buildFormParameters().get("ValidUntil"),
            Collections.singletonList("1970-05-23T21:21:18Z"));
   }

   @Test
   public void testToStatic() {
      RequestSpotInstancesOptions options = validUntil(test);
      assertEquals(options.buildFormParameters().get("ValidUntil"),
            Collections.singletonList("1970-05-23T21:21:18Z"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testToNPE() {
      validUntil(null);
   }

}
