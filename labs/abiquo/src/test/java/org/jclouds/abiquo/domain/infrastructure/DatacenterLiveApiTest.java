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

package org.jclouds.abiquo.domain.infrastructure;

import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.enterprise.Limits;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.infrastructure.Datacenter.Builder;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * Live integration tests for the {@link Datacenter} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "DatacenterLiveApiTest")
public class DatacenterLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      env.datacenter.setLocation("New York");
      env.datacenter.update();

      // Recover the updated datacenter
      DatacenterDto updated = env.infrastructureApi.getDatacenter(env.datacenter.getId());

      assertEquals(updated.getLocation(), "New York");
   }

   public void testCheckHypervisorType() {
      HypervisorType type = env.datacenter.getHypervisorType(env.machine.getIp());

      assertEquals(env.machine.getType(), type);
   }

   public void testCreateRepeated() {
      Datacenter repeated = Builder.fromDatacenter(env.datacenter).build();

      try {
         repeated.save();
         fail("Should not be able to create datacenters with the same name");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "DC-3");
      }
   }

   public void testListLimits() {
      List<Limits> limits = env.datacenter.listLimits();
      assertNotNull(limits);
      assertTrue(limits.size() > 0);
   }

}
