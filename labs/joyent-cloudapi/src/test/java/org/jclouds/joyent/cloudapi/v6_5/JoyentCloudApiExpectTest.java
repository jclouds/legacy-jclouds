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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.joyent.cloudapi.v6_5;

import static org.testng.Assert.assertEquals;

import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApi;
import org.jclouds.joyent.cloudapi.v6_5.internal.BaseJoyentCloudApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JoyentCloudApiExpectTest")
public class JoyentCloudApiExpectTest extends BaseJoyentCloudApiExpectTest {

   public void testGetConfiguredDatacenters() {

      JoyentCloudApi apiWhenDatacentersExists = requestSendsResponse(getDatacenters, getDatacentersResponse);

      assertEquals(
            apiWhenDatacentersExists.getConfiguredDatacenters(),
            ImmutableSet.<String> builder()
                  .add("us-east-1")
                  .add("us-west-1")
                  .add("us-sw-1")
                  .add("eu-ams-1").build());
   }

}
