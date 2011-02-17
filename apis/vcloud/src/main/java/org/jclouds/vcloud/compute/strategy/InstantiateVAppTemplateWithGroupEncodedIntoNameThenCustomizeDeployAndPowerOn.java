/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

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
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn implements CreateNodeWithGroupEncodedIntoName {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final VCloudClient client;
   protected final GetNodeMetadataStrategy getNode;
   protected final Predicate<URI> successTester;

   @Inject
   protected InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn(Predicate<URI> successTester, VCloudClient client,
            GetNodeMetadataStrategy getNode) {
      this.client = client;
      this.successTester = successTester;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {
      InstantiateVAppTemplateOptions options = processorCount((int) getCores(template.getHardware())).memory(
               template.getHardware().getRam()).disk(
               (long) ((template.getHardware().getVolumes().get(0).getSize()) * 1024 * 1024l));

      String customizationScript = null;
      if (template.getOptions() instanceof VCloudTemplateOptions) {
         customizationScript = VCloudTemplateOptions.class.cast(template.getOptions()).getCustomizationScript();
         if (customizationScript != null) {
            options.customizeOnInstantiate(false);
            options.deploy(false);
            options.powerOn(false);
         }
      }

      if (!template.getOptions().shouldBlockUntilRunning())
         options.block(false);

      URI VDC = URI.create(template.getLocation().getId());
      URI templateId = URI.create(template.getImage().getId());

      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VApp vAppResponse = client.instantiateVAppTemplateInVDC(VDC, templateId, name, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());

      Task task = vAppResponse.getTasks().get(0);

      if (customizationScript == null) {
         return blockOnDeployAndPowerOnIfConfigured(options, vAppResponse, task);
      } else {
         if (!successTester.apply(task.getHref())) {
            throw new RuntimeException(String
                     .format("failed to %s %s: %s", "instantiate", vAppResponse.getName(), task));
         }
         Vm vm = Iterables.get(client.getVApp(vAppResponse.getHref()).getChildren(), 0);
         GuestCustomizationSection guestConfiguration = vm.getGuestCustomizationSection();
         // guestConfiguration
         // .setCustomizationScript(guestConfiguration.getCustomizationScript()
         // != null ?
         // guestConfiguration
         // .getCustomizationScript()
         // + "\n" + customizationScript : customizationScript);
         guestConfiguration.setCustomizationScript(customizationScript);
         task = client.updateGuestCustomizationOfVm(vm.getHref(), guestConfiguration);
         if (!successTester.apply(task.getHref())) {
            throw new RuntimeException(String.format("failed to %s %s: %s", "updateGuestCustomizationOfVm", vm
                     .getName(), task));
         }
         task = client.deployAndPowerOnVAppOrVm(vAppResponse.getHref());
         return blockOnDeployAndPowerOnIfConfigured(options, vAppResponse, task);
      }

   }

   private NodeMetadata blockOnDeployAndPowerOnIfConfigured(InstantiateVAppTemplateOptions options, VApp vAppResponse,
            Task task) {
      if (options.shouldBlock()) {
         if (!successTester.apply(task.getHref())) {
            throw new RuntimeException(String.format("failed to %s %s: %s", "deploy and power on", vAppResponse
                     .getName(), task));
         }
         logger.debug("<< ready vApp(%s)", vAppResponse.getName());
      }
      return getNode.getNode(vAppResponse.getHref().toASCIIString());
   }
}