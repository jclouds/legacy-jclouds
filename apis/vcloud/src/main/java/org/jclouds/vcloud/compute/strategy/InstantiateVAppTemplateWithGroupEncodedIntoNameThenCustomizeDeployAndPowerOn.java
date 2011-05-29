/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.compute.strategy;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.NetworkConnectionSection.Builder;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn implements
         CreateNodeWithGroupEncodedIntoName {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final VCloudClient client;
   protected final GetNodeMetadataStrategy getNode;
   protected final Predicate<URI> successTester;

   @Inject
   protected InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn(Predicate<URI> successTester,
            VCloudClient client, GetNodeMetadataStrategy getNode) {
      this.client = client;
      this.successTester = successTester;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();

      // TODO make disk size specifiable
      // disk((long) ((template.getHardware().getVolumes().get(0).getSize()) *
      // 1024 * 1024l));

      String customizationScript = VCloudTemplateOptions.class.cast(template.getOptions()).getCustomizationScript();
      IpAddressAllocationMode ipAddressAllocationMode = VCloudTemplateOptions.class.cast(template.getOptions())
               .getIpAddressAllocationMode();

      options.description(VCloudTemplateOptions.class.cast(template.getOptions()).getDescription());
      options.customizeOnInstantiate(false);
      options.deploy(false);
      options.powerOn(false);

      if (!template.getOptions().shouldBlockUntilRunning())
         options.block(false);

      URI VDC = URI.create(template.getLocation().getId());
      URI templateId = URI.create(template.getImage().getId());

      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VApp vAppResponse = client.getVAppTemplateClient().createVAppInVDCByInstantiatingTemplate(name, VDC, templateId,
               options);
      waitForTask(vAppResponse.getTasks().get(0), vAppResponse);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());

      // note customization is a serial concern at the moment
      Vm vm = Iterables.get(client.getVAppClient().getVApp(vAppResponse.getHref()).getChildren(), 0);
      if (customizationScript != null) {
         logger.trace(">> updating customization vm(%s) ", vm.getName());
         waitForTask(updateVmWithCustomizationScript(vm, customizationScript), vAppResponse);
         logger.trace("<< updated customization vm(%s) ", vm.getName());
      }
      if (ipAddressAllocationMode != null) {
         logger.trace(">> updating ipAddressAllocationMode(%s) vm(%s) ", ipAddressAllocationMode, vm.getName());
         waitForTask(updateVmWithIpAddressAllocationMode(vm, ipAddressAllocationMode), vAppResponse);
         logger.trace("<< updated ipAddressAllocationMode vm(%s) ", vm.getName());
      }
      int cpuCount = new Double(getCores(template.getHardware())).intValue();
      logger.trace(">> updating cpuCount(%d) vm(%s) ", cpuCount, vm.getName());
      waitForTask(updateCPUCountOfVm(vm, cpuCount), vAppResponse);
      logger.trace("<< updated cpuCount vm(%s) ", vm.getName());
      int memoryMB = template.getHardware().getRam();
      logger.trace(">> updating memoryMB(%d) vm(%s) ", memoryMB, vm.getName());
      waitForTask(updateMemoryMBOfVm(vm, memoryMB), vAppResponse);
      logger.trace("<< updated memoryMB vm(%s) ", vm.getName());
      logger.trace(">> deploying and powering on vApp(%s) ", vAppResponse.getName());
      return blockOnDeployAndPowerOnIfConfigured(options, vAppResponse, client.getVAppClient().deployAndPowerOnVApp(
               vAppResponse.getHref()));

   }

   public void waitForTask(Task task, VApp vAppResponse) {
      if (!successTester.apply(task.getHref())) {
         throw new RuntimeException(String.format("failed to %s %s: %s", task.getName(), vAppResponse.getName(), task));
      }
   }

   public Task updateVmWithCustomizationScript(Vm vm, String customizationScript) {
      GuestCustomizationSection guestConfiguration = vm.getGuestCustomizationSection();
      // TODO: determine if the server version is beyond 1.0.0, and if so append
      // to, but
      // not overwrite the customization script. In version 1.0.0, the api
      // returns a script that
      // loses newlines.
      guestConfiguration.setCustomizationScript(customizationScript);
      return client.getVmClient().updateGuestCustomizationOfVm(guestConfiguration, vm.getHref());
   }

   public Task updateVmWithIpAddressAllocationMode(Vm vm, final IpAddressAllocationMode ipAddressAllocationMode) {
      NetworkConnectionSection net = vm.getNetworkConnectionSection();
      Builder builder = net.toBuilder();
      builder.connections(Iterables.transform(net.getConnections(),
               new Function<NetworkConnection, NetworkConnection>() {

                  @Override
                  public NetworkConnection apply(NetworkConnection arg0) {
                     return arg0.toBuilder().connected(true).ipAddressAllocationMode(ipAddressAllocationMode).build();
                  }

               }));
      return client.getVmClient().updateNetworkConnectionOfVm(builder.build(), vm.getHref());
   }

   public Task updateCPUCountOfVm(Vm vm, int cpuCount) {
      return client.getVmClient().updateCPUCountOfVm(cpuCount, vm.getHref());
   }

   public Task updateMemoryMBOfVm(Vm vm, int memoryInMB) {
      return client.getVmClient().updateMemoryMBOfVm(memoryInMB, vm.getHref());
   }

   private NodeMetadata blockOnDeployAndPowerOnIfConfigured(InstantiateVAppTemplateOptions options, VApp vAppResponse,
            Task task) {
      if (options.shouldBlock()) {
         waitForTask(task, vAppResponse);
         logger.debug("<< ready vApp(%s)", vAppResponse.getName());
      }
      return getNode.getNode(vAppResponse.getHref().toASCIIString());
   }
}