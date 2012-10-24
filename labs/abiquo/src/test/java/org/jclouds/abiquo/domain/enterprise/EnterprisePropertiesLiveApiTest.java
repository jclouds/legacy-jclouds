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

package org.jclouds.abiquo.domain.enterprise;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;

/**
 * Live integration tests for the {@link Enterprise} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "EnterprisePropertiesLiveApiTest")
public class EnterprisePropertiesLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      EnterpriseProperties properties = env.administrationService.getEnterpriseProperties(env.enterprise);

      Integer size = properties.getProperties().size();
      properties.getProperties().put("Prop", "Value");
      properties.update();

      // Recover the updated properties
      EnterprisePropertiesDto updated = env.enterpriseApi.getEnterpriseProperties(env.enterprise.unwrap());

      assertEquals(updated.getProperties().size(), size + 1);
      assertTrue(updated.getProperties().containsKey("Prop"));
      assertTrue(updated.getProperties().containsValue("Value"));
   }
}
