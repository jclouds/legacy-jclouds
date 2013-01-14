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
package org.jclouds.savvis.vpdc.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.jclouds.savvis.vpdc.options.GetVMOptions.Builder.withPowerState;
import static org.jclouds.savvis.vpdc.reference.VPDCConstants.PROPERTY_VPDC_VDC_EMAIL;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.savvis.vpdc.VPDCApi;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.savvis.vpdc.predicates.TaskSuccess;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

;

/**
 * defines the connection between the {@link VPDCApi} implementation and the
 * jclouds {@link ComputeService}
 * 
 */
@Singleton
public class VPDCComputeServiceAdapter implements ComputeServiceAdapter<VM, VMSpec, CIMOperatingSystem, Network> {
   private final VPDCApi api;
   private final Predicate<String> taskTester;
   @Inject(optional = true)
   @Named(PROPERTY_VPDC_VDC_EMAIL)
   String email;

   @Inject
   public VPDCComputeServiceAdapter(VPDCApi api, TaskSuccess taskSuccess) {
      this.api = checkNotNull(api, "api");
      // TODO: parameterize
      this.taskTester = retry(checkNotNull(taskSuccess, "taskSuccess"), 650, 10, SECONDS);
   }

   @Override
   public NodeAndInitialCredentials<VM> createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {
      String networkTierName = template.getLocation().getId();
      String vpdcId = template.getLocation().getParent().getId();
      String billingSiteId = template.getLocation().getParent().getParent().getId();

      VMSpec.Builder specBuilder = VMSpec.builder();
      specBuilder.name(name);
      specBuilder.networkTierName(networkTierName);
      specBuilder.operatingSystem(CIMOperatingSystem.class.cast(template.getImage().getOperatingSystem()));
      specBuilder.processorCount(template.getHardware().getProcessors().size());
      specBuilder.memoryInGig(template.getHardware().getRam() / 1024);

      for (Volume volume : template.getHardware().getVolumes()) {
         if (volume.isBootDevice())
            specBuilder.bootDeviceName(volume.getDevice()).bootDiskSize(volume.getSize().intValue());
         else
            specBuilder.addDataDrive(volume.getDevice(), volume.getSize().intValue());
      }

      Task task = api.getVMApi().addVMIntoVDC(billingSiteId, vpdcId, specBuilder.build());
      // make sure there's no error
      if (task.getError() != null)
         throw new RuntimeException("cloud not add vm: " + task.getError().toString());

      if (taskTester.apply(task.getId())) {
         try {
            VM returnVal = this.getNode(task.getResult().getHref().toASCIIString());
            return new NodeAndInitialCredentials<VM>(returnVal, returnVal.getId(), null);
         } finally {
            // TODO: get the credentials relevant to the billingSiteId/Org
            // credentialStore.put(id, new Credentials(orgId, orgUser));
         }
      } else {
         throw new RuntimeException("task timed out: " + task);
      }
   }

   @Override
   public Iterable<VMSpec> listHardwareProfiles() {
      // TODO don't depend on OS
      return ImmutableSet.of(VMSpec.builder().operatingSystem(Iterables.get(listImages(), 0)).memoryInGig(2)
            .addDataDrive("/data01", 25).build());
   }

   @Override
   public Iterable<CIMOperatingSystem> listImages() {
      return api.listPredefinedOperatingSystems();
   }

   // cheat until we have a getImage command
   @Override
   public CIMOperatingSystem getImage(final String id) {
      return Iterables.find(listImages(), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem input) {
            return (input.getOsType().getCode() + "").equals(id);
         }

      }, null);
   }
   
   @Override
   public Iterable<VM> listNodes() {
      Builder<VM> builder = ImmutableSet.builder();
      for (Resource org1 : api.listOrgs()) {
         Org org = api.getBrowsingApi().getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC VDC = api.getBrowsingApi().getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vApp : Iterables.filter(VDC.getResourceEntities(), new Predicate<Resource>() {

               @Override
               public boolean apply(Resource arg0) {
                  return VCloudMediaType.VAPP_XML.equals(arg0.getType());
               }

            })) {
               builder.add(api.getBrowsingApi().getVMInVDC(org.getId(), vdc.getId(), vApp.getId(),
                     withPowerState()));
            }
         }
      }
      return builder.build();
   }

   @Override
   public Iterable<Network> listLocations() {
      Builder<Network> builder = ImmutableSet.builder();
      for (Resource org1 : api.listOrgs()) {
         Org org = api.getBrowsingApi().getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC VDC = api.getBrowsingApi().getVDCInOrg(org.getId(), vdc.getId());
            // optionally constrain locations
            if (email != null && VDC.getDescription().indexOf(email) != -1)
               continue;
            for (Resource network : VDC.getAvailableNetworks()) {
               builder.add(api.getBrowsingApi().getNetworkInVDC(org.getId(), vdc.getId(), network.getId()));
            }
         }
      }
      return builder.build();
   }

   @Override
   public VM getNode(String id) {
      return api.getBrowsingApi().getVM(URI.create(checkNotNull(id, "id")), withPowerState());
   }

   @Override
   public void destroyNode(String id) {
      taskTester.apply(api.getVMApi().removeVM(URI.create(checkNotNull(id, "id"))).getId());
   }

   @Override
   public void rebootNode(String id) {
      // there is no support for restart in savvis yet
      suspendNode(id);
      resumeNode(id);
   }

   @Override
   public void resumeNode(String id) {
      taskTester.apply(api.getServiceManagementApi().powerOnVM(URI.create(checkNotNull(id, "id"))).getId());
   }

   @Override
   public void suspendNode(String id) {
      taskTester.apply(api.getServiceManagementApi().powerOffVM(URI.create(checkNotNull(id, "id"))).getId());
   }
}
