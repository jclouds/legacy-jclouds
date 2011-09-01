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
package org.jclouds.ec2.options;

import static org.jclouds.ec2.options.DetachVolumeOptions.Builder.fromDevice;
import static org.jclouds.ec2.options.DetachVolumeOptions.Builder.fromInstance;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of DetachVolumeOptions and DetachVolumeOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class DetachVolumeOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(DetachVolumeOptions.class);
      assert !String.class.isAssignableFrom(DetachVolumeOptions.class);
   }

   @Test
   public void testFromDevice() {
      DetachVolumeOptions options = new DetachVolumeOptions();
      options.fromDevice("test");
      assertEquals(options.buildFormParameters().get("Device"), Collections.singletonList("test"));
   }

   @Test
   public void testNullFromDevice() {
      DetachVolumeOptions options = new DetachVolumeOptions();
      assertEquals(options.buildFormParameters().get("Device"), Collections.EMPTY_LIST);
   }

   @Test
   public void testFromDeviceStatic() {
      DetachVolumeOptions options = fromDevice("test");
      assertEquals(options.buildFormParameters().get("Device"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testFromDeviceNPE() {
      fromDevice(null);
   }

   @Test
   public void testFromInstance() {
      DetachVolumeOptions options = new DetachVolumeOptions();
      options.fromInstance("test");
      assertEquals(options.buildFormParameters().get("InstanceId"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullFromInstance() {
      DetachVolumeOptions options = new DetachVolumeOptions();
      assertEquals(options.buildFormParameters().get("InstanceId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testFromInstanceStatic() {
      DetachVolumeOptions options = fromInstance("test");
      assertEquals(options.buildFormParameters().get("InstanceId"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testFromInstanceNPE() {
      fromInstance(null);
   }

}
