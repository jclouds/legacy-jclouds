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

package org.jclouds.abiquo.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.compute.options.AbiquoTemplateOptions;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplateInVirtualDatacenter;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.VirtualMachineMonitor;
import org.jclouds.abiquo.predicates.cloud.VirtualAppliancePredicates;
import org.jclouds.abiquo.predicates.cloud.VirtualMachineTemplatePredicates;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Defines the connection between the {@link AbiquoApi} implementation and the
 * jclouds {@link ComputeService}.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AbiquoComputeServiceAdapter
      implements
      ComputeServiceAdapter<VirtualMachine, VirtualMachineTemplateInVirtualDatacenter, VirtualMachineTemplate, VirtualDatacenter> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final RestContext<AbiquoApi, AbiquoAsyncApi> context;

   private final AdministrationService adminService;

   private final CloudService cloudService;

   private final MonitoringService monitoringService;

   private final FindCompatibleVirtualDatacenters compatibleVirtualDatacenters;

   private final Supplier<Map<Integer, Datacenter>> regionMap;

   @Inject
   public AbiquoComputeServiceAdapter(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         final AdministrationService adminService, final CloudService cloudService,
         final MonitoringService monitoringService,
         final FindCompatibleVirtualDatacenters compatibleVirtualDatacenters,
         @Memoized final Supplier<Map<Integer, Datacenter>> regionMap) {
      this.context = checkNotNull(context, "context");
      this.adminService = checkNotNull(adminService, "adminService");
      this.cloudService = checkNotNull(cloudService, "cloudService");
      this.monitoringService = checkNotNull(monitoringService, "monitoringService");
      this.compatibleVirtualDatacenters = checkNotNull(compatibleVirtualDatacenters, "compatibleVirtualDatacenters");
      this.regionMap = checkNotNull(regionMap, "regionMap");
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(final String tag,
         final String name, final Template template) {
      AbiquoTemplateOptions options = template.getOptions().as(AbiquoTemplateOptions.class);
      Enterprise enterprise = adminService.getCurrentEnterprise();

      // Get the region where the template is available
      Datacenter datacenter = regionMap.get().get(Integer.valueOf(template.getImage().getLocation().getId()));

      // Load the template
      VirtualMachineTemplate virtualMachineTemplate = enterprise.getTemplateInRepository(datacenter,
            Integer.valueOf(template.getImage().getId()));

      // Get the zone where the template will be deployed
      VirtualDatacenter vdc = cloudService.getVirtualDatacenter(Integer.valueOf(template.getHardware().getLocation()
            .getId()));

      // Load the virtual appliance or create it if it does not exist
      VirtualAppliance vapp = vdc.findVirtualAppliance(VirtualAppliancePredicates.name(tag));
      if (vapp == null) {
         vapp = VirtualAppliance.builder(context, vdc).name(tag).build();
         vapp.save();
      }

      Integer overrideCores = options.getOverrideCores();
      Integer overrideRam = options.getOverrideRam();

      VirtualMachine vm = VirtualMachine.builder(context, vapp, virtualMachineTemplate) //
            .nameLabel(name) //
            .cpu(overrideCores != null ? overrideCores : totalCores(template.getHardware())) //
            .ram(overrideRam != null ? overrideRam : template.getHardware().getRam()) //
            .password(options.getVncPassword()) // Can be null
            .build();

      vm.save();

      // Once the virtual machine is created, override the default network
      // settings if needed
      // If no public ip is available in the virtual datacenter, the virtual
      // machine will be assigned by default an ip address in the default
      // private VLAN for the virtual datacenter
      PublicIp publicIp = vdc.findPurchasedPublicIp(IpPredicates.<PublicIp> notUsed());
      if (publicIp != null) {
         List<PublicIp> ips = Lists.newArrayList();
         ips.add(publicIp);
         vm.setNics(ips);
      }

      // This is an async operation, but jclouds already waits until the node is
      // RUNNING, so there is no need to block here
      vm.deploy();

      return new NodeAndInitialCredentials<VirtualMachine>(vm, vm.getId().toString(), null);
   }

   @Override
   public Iterable<VirtualMachineTemplateInVirtualDatacenter> listHardwareProfiles() {
      // In Abiquo, images are scoped to a region (physical datacenter), and
      // hardware profiles are scoped to a zone (a virtual datacenter in the
      // region, with a concrete virtualization technology)

      return concat(transform(listImages(),
            new Function<VirtualMachineTemplate, Iterable<VirtualMachineTemplateInVirtualDatacenter>>() {
               @Override
               public Iterable<VirtualMachineTemplateInVirtualDatacenter> apply(final VirtualMachineTemplate template) {
                  Iterable<VirtualDatacenter> compatibleZones = compatibleVirtualDatacenters.execute(template);

                  return transform(compatibleZones,
                        new Function<VirtualDatacenter, VirtualMachineTemplateInVirtualDatacenter>() {
                           @Override
                           public VirtualMachineTemplateInVirtualDatacenter apply(final VirtualDatacenter vdc) {
                              return new VirtualMachineTemplateInVirtualDatacenter(template, vdc);
                           }
                        });
               }
            }));
   }

   @Override
   public Iterable<VirtualMachineTemplate> listImages() {
      Enterprise enterprise = adminService.getCurrentEnterprise();
      return enterprise.listTemplates();
   }

   @Override
   public VirtualMachineTemplate getImage(final String id) {
      Enterprise enterprise = adminService.getCurrentEnterprise();
      return enterprise.findTemplate(VirtualMachineTemplatePredicates.id(Integer.valueOf(id)));
   }

   @Override
   public Iterable<VirtualDatacenter> listLocations() {
      return cloudService.listVirtualDatacenters();
   }

   @Override
   public VirtualMachine getNode(final String id) {
      return cloudService.findVirtualMachine(vmId(id));
   }

   @Override
   public void destroyNode(final String id) {
      VirtualMachine vm = getNode(id);
      vm.delete();
   }

   @Override
   public void rebootNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.reboot();
      monitor.awaitState(VirtualMachineState.ON, vm);
   }

   @Override
   public void resumeNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.changeState(VirtualMachineState.ON);
      monitor.awaitState(VirtualMachineState.ON, vm);
   }

   @Override
   public void suspendNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.changeState(VirtualMachineState.PAUSED);
      monitor.awaitState(VirtualMachineState.PAUSED, vm);
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      return cloudService.listVirtualMachines();
   }

   private static Predicate<VirtualMachine> vmId(final String id) {
      return new Predicate<VirtualMachine>() {
         @Override
         public boolean apply(final VirtualMachine input) {
            return Integer.valueOf(id).equals(input.getId());
         }
      };
   }

   private static int totalCores(final Hardware hardware) {
      double cores = 0;
      for (Processor processor : hardware.getProcessors()) {
         cores += processor.getCores();
      }
      return Double.valueOf(cores).intValue();
   }

}
