/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link User} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "SystemPropertiesLiveApiTest")
public class SystemPropertiesLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testUpdate() {
      SystemProperty prop = env.administrationService.getSystemProperty("client.dashboard.showStartUpAlert");

      String value = prop.getValue();
      prop.setValue("0");
      prop.update();

      // Recover the updated datacenter
      SystemProperty updated = env.administrationService.getSystemProperty("client.dashboard.showStartUpAlert");

      assertEquals(updated.getValue(), "0");

      prop.setValue(value);
      prop.update();
   }
}
