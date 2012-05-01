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
package org.jclouds.vcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getCredentialsFrom;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.ovf.Network;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.vcloud.TaskStillRunningException;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.NetworkConnectionSection.Builder;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final VCloudClient client;
   protected final Predicate<URI> successTester;
   protected final LoadingCache<URI, VAppTemplate> vAppTemplates;
   protected final Supplier<NetworkConfig> defaultNetworkConfig;
   protected final String buildVersion;

   @Inject
   protected InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn(VCloudClient client,
            Predicate<URI> successTester, LoadingCache<URI, VAppTemplate> vAppTemplates,
            Supplier<NetworkConfig> defaultNetworkConfig, @BuildVersion String buildVersion) {
      this.client = client;
      this.successTester = successTester;
      this.vAppTemplates = vAppTemplates;
      this.defaultNetworkConfig = defaultNetworkConfig;
      this.buildVersion = buildVersion;
   }

   // TODO: filtering on "none" is a hack until we can filter on
   // vAppTemplate.getNetworkConfigSection().getNetworkConfigs() where
   // name = getChildren().NetworkConnectionSection.connection where ipallocationmode == none
   Predicate<Network> networkWithNoIpAllocation = new Predicate<Network>() {

      @Override
      public boolean apply(Network input) {
         return "none".equals(input.getName());
      }

   };
   
   /**
    * per john ellis at bluelock, vCloud Director 1.5 is more strict than earlier versions.
    * <p/>
    * It appears to be 15 characters to match Windows' hostname limitation. Must be alphanumeric, at
    * least one non-number character and hyphens and underscores are the only non-alpha character
    * allowed.
    */
   public static enum ComputerNameValidator  {
      INSTANCE;
      
      private DnsNameValidator validator;

      ComputerNameValidator(){
         this.validator = new  DnsNameValidator(3, 15);
      }
      
      public void validate(@Nullable String t) throws IllegalArgumentException {
         this.validator.validate(t);
      }

   }
   
   public NodeAndInitialCredentials<VApp> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      // no sense waiting until failures occur later
      ComputerNameValidator.INSTANCE.validate(name);
      
      URI templateId = URI.create(template.getImage().getId());

      VAppTemplate vAppTemplate = vAppTemplates.getUnchecked(templateId);

      if (vAppTemplate.getChildren().size() > 1)
         throw new UnsupportedOperationException("we currently do not support multiple vms in a vAppTemplate "
                  + vAppTemplate);

      if (vAppTemplate.getNetworkSection().getNetworks().size() > 1)
         throw new UnsupportedOperationException(
                  "we currently do not support multiple network connections in a vAppTemplate " + vAppTemplate);

      Network networkToConnect = get(vAppTemplate.getNetworkSection().getNetworks(), 0);

      NetworkConfig config;
      // if we only have a disconnected network, let's add a new section for the upstream
      // TODO: remove the disconnected entry
      if (networkWithNoIpAllocation.apply(networkToConnect))
         config = defaultNetworkConfig.get();
      else
         config = defaultNetworkConfig.get().toBuilder().networkName(networkToConnect.getName()).build();

      // note that in VCD 1.5, the network name after instantiation will be the same as the parent
      InstantiateVAppTemplateOptions options = addNetworkConfig(config);

      // TODO make disk size specifiable
      // disk((long) ((template.getHardware().getVolumes().get(0).getSize()) *
      // 1024 * 1024l));

      String customizationScript = VCloudTemplateOptions.class.cast(template.getOptions()).getCustomizationScript();
      IpAddressAllocationMode ipAllocationMode = VCloudTemplateOptions.class.cast(template.getOptions())
               .getIpAddressAllocationMode();

      String description = VCloudTemplateOptions.class.cast(template.getOptions()).getDescription();
      if (description == null)
         description = vAppTemplate.getName();

      options.description(description);
      options.deploy(false);
      options.powerOn(false);

      URI VDC = URI.create(template.getLocation().getId());

      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VApp vAppResponse = client.getVAppTemplateClient().createVAppInVDCByInstantiatingTemplate(name, VDC, templateId,
               options);
      waitForTask(vAppResponse.getTasks().get(0));
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());

      // vm data is available after instantiate completes
      vAppResponse = client.getVAppClient().getVApp(vAppResponse.getHref());

      // per above check, we know there is only a single VM
      Vm vm = get(vAppResponse.getChildren(), 0);

      // note we cannot do tasks in parallel or VCD will throw "is busy" errors

      // note we must do this before any other customizations as there is a dependency on
      // valid naming conventions before you can perform commands such as updateCPUCount
      logger.trace(">> updating customization vm(%s) name->(%s)", vm.getName(), name);
      waitForTask(updateVmWithNameAndCustomizationScript(vm, name, customizationScript));
      logger.trace("<< updated customization vm(%s)", name);

      ensureVmHasAllocationModeOrPooled(vAppResponse, ipAllocationMode);

      int cpuCount = new Double(getCores(template.getHardware())).intValue();
      logger.trace(">> updating cpuCount(%d) vm(%s)", cpuCount, vm.getName());
      waitForTask(updateCPUCountOfVm(vm, cpuCount));
      logger.trace("<< updated cpuCount vm(%s)", vm.getName());
      int memoryMB = template.getHardware().getRam();
      logger.trace(">> updating memoryMB(%d) vm(%s)", memoryMB, vm.getName());
      waitForTask(updateMemoryMBOfVm(vm, memoryMB));
      logger.trace("<< updated memoryMB vm(%s)", vm.getName());
      logger.trace(">> deploying vApp(%s)", vAppResponse.getName());
      waitForTask(client.getVAppClient().deployVApp(vAppResponse.getHref()));
      logger.trace("<< deployed vApp(%s)", vAppResponse.getName());

      // only after deploy is the password valid
      vAppResponse = client.getVAppClient().getVApp(vAppResponse.getHref());

      logger.trace(">> powering on vApp(%s)", vAppResponse.getName());
      client.getVAppClient().powerOnVApp(vAppResponse.getHref());

      return new NodeAndInitialCredentials<VApp>(vAppResponse, vAppResponse.getHref().toASCIIString(),
               getCredentialsFrom(vAppResponse));

   }

   public void waitForTask(Task task) {
      if (!successTester.apply(task.getHref())) {
         throw new TaskStillRunningException(task);
      }
   }

   /**
    * Naming constraints modifying a VM on a VApp in vCloud Director (at least v1.5) can be more
    * strict than those in a vAppTemplate. For example, while it is possible to instantiate a
    * vAppTemplate with a VM named (incorrectly) {@code Ubuntu_10.04}, you must change the name to a
    * valid (alphanumeric underscore) name before you can update it.
    */
   public Task updateVmWithNameAndCustomizationScript(Vm vm, String name, @Nullable String customizationScript) {
      GuestCustomizationSection guestConfiguration = vm.getGuestCustomizationSection();
      guestConfiguration.setComputerName(name);
      if (customizationScript != null) {
         // In version 1.0.0, the api returns a script that loses newlines, so we cannot append to a
         // customization script.
         // TODO: parameterize whether to overwrite or append existing customization
         if (!buildVersion.startsWith("1.0.0") && !"".endsWith(buildVersion)
                  && guestConfiguration.getCustomizationScript() != null)
            customizationScript = guestConfiguration.getCustomizationScript() + "\n" + customizationScript;

         guestConfiguration.setCustomizationScript(customizationScript);
      }
      return client.getVmClient().updateGuestCustomizationOfVm(guestConfiguration, vm.getHref());
   }

   public void ensureVmHasAllocationModeOrPooled(VApp vApp, @Nullable IpAddressAllocationMode ipAllocationMode) {
      Network networkToConnect = find(vApp.getNetworkSection().getNetworks(), not(networkWithNoIpAllocation));

      Vm vm = get(vApp.getChildren(), 0);

      NetworkConnectionSection net = vm.getNetworkConnectionSection();
      checkArgument(net.getConnections().size() > 0, "no connections on vm %s", vm);

      NetworkConnection toConnect = findWithPoolAllocationOrFirst(net);

      if (ipAllocationMode == null)
         ipAllocationMode = toConnect.getIpAddressAllocationMode();

      // make sure that we are in fact allocating ips
      if (ipAllocationMode == IpAddressAllocationMode.NONE)
         ipAllocationMode = IpAddressAllocationMode.POOL;

      if (toConnect.isConnected() && toConnect.getIpAddressAllocationMode() == ipAllocationMode
               && toConnect.getNetwork().equals(networkToConnect.getName())) {
         // then we don't need to change the network settings, and can save a call
      } else {
         Builder builder = net.toBuilder();
         builder.connections(ImmutableSet.of(toConnect.toBuilder().network(networkToConnect.getName()).connected(true)
                  .ipAddressAllocationMode(ipAllocationMode).build()));
         logger.trace(">> updating networkConnection vm(%s)", vm.getName());

         waitForTask(client.getVmClient().updateNetworkConnectionOfVm(builder.build(), vm.getHref()));
         logger.trace("<< updated networkConnection vm(%s)", vm.getName());

      }

   }

   private NetworkConnection findWithPoolAllocationOrFirst(NetworkConnectionSection net) {
      return find(net.getConnections(), new Predicate<NetworkConnection>() {

         @Override
         public boolean apply(NetworkConnection input) {
            return input.getIpAddressAllocationMode() == IpAddressAllocationMode.POOL;
         }

      }, get(net.getConnections(), 0));
   }

   public Task updateCPUCountOfVm(Vm vm, int cpuCount) {
      return client.getVmClient().updateCPUCountOfVm(cpuCount, vm.getHref());
   }

   public Task updateMemoryMBOfVm(Vm vm, int memoryInMB) {
      return client.getVmClient().updateMemoryMBOfVm(memoryInMB, vm.getHref());
   }
}