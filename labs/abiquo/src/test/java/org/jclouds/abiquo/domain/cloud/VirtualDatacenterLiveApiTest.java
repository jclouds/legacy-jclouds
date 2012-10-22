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

package org.jclouds.abiquo.domain.cloud;

import static com.google.common.collect.Iterables.size;
import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter.Builder;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.cloud.VirtualMachineTemplatePredicates;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;

/**
 * Live integration tests for the {@link VirtualDatacenter} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "VirtualDatacenterLiveApiTest")
public class VirtualDatacenterLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private VirtualMachineTemplate template;

   public void testUpdate() {
      env.virtualDatacenter.setName("Aloha updated");
      env.virtualDatacenter.update();

      // Recover the updated virtual datacenter
      VirtualDatacenterDto updated = env.cloudApi.getVirtualDatacenter(env.virtualDatacenter.getId());

      assertEquals(updated.getName(), "Aloha updated");
   }

   public void testCreateRepeated() {
      PrivateNetwork newnet = PrivateNetwork.builder(env.context.getApiContext()).name("Newnet").gateway("10.0.0.1")
            .address("10.0.0.0").mask(24).build();

      VirtualDatacenter repeated = Builder.fromVirtualDatacenter(env.virtualDatacenter).network(newnet).build();

      repeated.save();

      List<VirtualDatacenterDto> virtualDatacenters = env.cloudApi.listVirtualDatacenters(
            VirtualDatacenterOptions.builder().build()).getCollection();

      assertEquals(virtualDatacenters.size(), 2);
      assertEquals(virtualDatacenters.get(0).getName(), virtualDatacenters.get(1).getName());
      repeated.delete();
   }

   public void testCreateFromEnterprise() {
      Enterprise enterprise = env.enterpriseAdminContext.getAdministrationService().getCurrentUser().getEnterprise();
      assertNotNull(enterprise);

      List<Datacenter> datacenters = enterprise.listAllowedDatacenters();
      assertNotNull(datacenters);
      assertTrue(size(datacenters) > 0);

      Datacenter datacenter = datacenters.get(0);

      List<HypervisorType> hypervisors = datacenter.listAvailableHypervisors();
      assertNotNull(datacenters);
      assertTrue(size(datacenters) > 0);

      HypervisorType hypervisor = hypervisors.get(0);

      PrivateNetwork network = PrivateNetwork.builder(env.enterpriseAdminContext.getApiContext())
            .name("DefaultNetwork").gateway("192.168.1.1").address("192.168.1.0").mask(24).build();

      VirtualDatacenter virtualDatacenter = VirtualDatacenter
            .builder(env.enterpriseAdminContext.getApiContext(), datacenters.get(0), enterprise)
            .name(PREFIX + "Plain Virtual Aloha from ENT").cpuCountLimits(18, 20).hdLimitsInMb(279172872, 279172872)
            .publicIpsLimits(2, 2).ramLimits(19456, 20480).storageLimits(289910292, 322122547).vlansLimits(1, 2)
            .hypervisorType(hypervisor).network(network).build();

      virtualDatacenter.save();
      assertNotNull(virtualDatacenter.getId());

      virtualDatacenter.delete();
   }

   public void testCreateFromVirtualDatacenter() {
      HypervisorType hypervisor = env.virtualDatacenter.getHypervisorType();

      Enterprise enterprise = env.user.getEnterprise();
      assertNotNull(enterprise);

      Datacenter datacenter = env.virtualDatacenter.getDatacenter();
      assertNotNull(datacenter);

      PrivateNetwork network = PrivateNetwork.builder(env.plainUserContext.getApiContext()).name("DefaultNetwork")
            .gateway("192.168.1.1").address("192.168.1.0").mask(24).build();

      VirtualDatacenter virtualDatacenter = VirtualDatacenter
            .builder(env.context.getApiContext(), datacenter, enterprise).name(PREFIX + "Plain Virtual Aloha from VDC")
            .cpuCountLimits(18, 20).hdLimitsInMb(279172872, 279172872).publicIpsLimits(2, 2).ramLimits(19456, 20480)
            .storageLimits(289910292, 322122547).vlansLimits(1, 2).hypervisorType(hypervisor).network(network).build();

      virtualDatacenter.save();
      assertNotNull(virtualDatacenter.getId());

      virtualDatacenter.delete();
   }

   public void testPurchaseIp() {
      PublicIp publicIp = env.virtualDatacenter.listAvailablePublicIps().get(0);
      assertNotNull(publicIp);
      env.virtualDatacenter.purchasePublicIp(publicIp);

      PublicIp apiIp = env.virtualDatacenter.findPurchasedPublicIp(IpPredicates.<PublicIp> address(publicIp.getIp()));
      assertNotNull(apiIp);

      env.virtualDatacenter.releaseePublicIp(apiIp);
      apiIp = env.virtualDatacenter.findPurchasedPublicIp(IpPredicates.<PublicIp> address(publicIp.getIp()));
      assertNull(apiIp);
   }

   public void testGetDefaultNetwork() {
      PrivateNetwork network = env.virtualDatacenter.getDefaultNetwork().toPrivateNetwork();

      assertNotNull(network);
      assertEquals(network.getName(), env.privateNetwork.getName());
      assertEquals(network.getType(), env.privateNetwork.getType());
   }

   public void testGetAvailableTemplates() {
      List<VirtualMachineTemplate> templates = env.virtualDatacenter.listAvailableTemplates();
      assertNotNull(templates);
      assertFalse(templates.isEmpty());

      template = templates.get(0);
   }

   @Test(dependsOnMethods = "testGetAvailableTemplates")
   public void testFindAvailableTemplate() {
      VirtualMachineTemplate templateFound = env.virtualDatacenter
            .findAvailableTemplate(VirtualMachineTemplatePredicates.id(template.getId()));

      assertNotNull(template);
      assertNotNull(templateFound);
      assertEquals(templateFound.getId(), template.getId());
   }

   @Test(dependsOnMethods = "testGetAvailableTemplates")
   public void testGetAvailableTemplate() {
      VirtualMachineTemplate templateFound = env.virtualDatacenter.getAvailableTemplate(template.getId());
      assertNotNull(templateFound);
      assertEquals(templateFound.getId(), template.getId());
   }

}
