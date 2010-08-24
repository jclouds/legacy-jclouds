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

package org.jclouds.vcloud.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudComputeClientImpl extends CommonVCloudComputeClientImpl<VAppTemplate, VApp> implements
         VCloudComputeClient {

   protected final Map<Status, NodeState> vAppStatusToNodeState;

   @Inject
   public VCloudComputeClientImpl(VCloudClient client, Predicate<URI> successTester,
            Map<Status, NodeState> vAppStatusToNodeState) {
      super(client, successTester);
      this.vAppStatusToNodeState = vAppStatusToNodeState;
   }

   @Override
   public Map<String, String> start(@Nullable URI VDC, URI templateId, String name,
            InstantiateVAppTemplateOptions options, int... portsToOpen) {
      checkNotNull(options, "options");
      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VDC vdc = client.getVDC(VDC);
      VAppTemplate template = VCloudClient.class.cast(client).getVAppTemplate(templateId);

      VApp vAppResponse = VCloudClient.class.cast(client).instantiateVAppTemplateInVDC(vdc.getId(), template.getId(),
               name, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());

      logger.debug(">> deploying vApp(%s)", vAppResponse.getName());

      Task task = VCloudClient.class.cast(client).deployAndPowerOnVAppOrVm(vAppResponse.getId());
      if (options.shouldBlockOnDeploy()) {
         if (!taskTester.apply(task.getId())) {
            throw new RuntimeException(String.format("failed to %s %s: %s", "deploy and power on", vAppResponse
                     .getName(), task));
         }
         logger.debug("<< deployed and powered on vApp(%s)", vAppResponse.getName());
      }
      return parseAndValidateResponse(template, vAppResponse);
   }

   @Override
   public Set<String> getPrivateAddresses(URI id) {
      return ImmutableSet.of();
   }

   @Override
   public Set<String> getPublicAddresses(URI id) {
      return ImmutableSet.of();
      // TODO
      // VApp vApp = refreshVApp(id);
      // return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

   @Override
   protected Status getStatus(VApp vApp) {
      return vApp.getStatus();
   }

   @Override
   protected VApp refreshVApp(URI id) {
      return VCloudClient.class.cast(client).getVApp(id);
   }

   @Override
   protected Task powerOff(VApp vApp) {
      return VCloudClient.class.cast(client).powerOffVAppOrVm(vApp.getId());
   }

   @Override
   protected Task reset(VApp vApp) {
      return VCloudClient.class.cast(client).resetVAppOrVm(vApp.getId());
   }

   @Override
   protected Task undeploy(VApp vApp) {
      return VCloudClient.class.cast(client).undeployAndSaveStateOfVAppOrVm(vApp.getId());
   }
}