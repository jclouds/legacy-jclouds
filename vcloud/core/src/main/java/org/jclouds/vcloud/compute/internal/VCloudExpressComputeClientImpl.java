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
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudExpressComputeClientImpl extends
      CommonVCloudComputeClientImpl<VCloudExpressVAppTemplate, VCloudExpressVApp> implements VCloudExpressComputeClient {

   protected final Map<Status, NodeState> vAppStatusToNodeState;

   @Inject
   public VCloudExpressComputeClientImpl(VCloudExpressClient client, Predicate<URI> successTester,
         Map<Status, NodeState> vAppStatusToNodeState) {
      super(client, successTester);
      this.vAppStatusToNodeState = vAppStatusToNodeState;
   }

   @Override
   protected void deleteVApp(VCloudExpressVApp vApp) {
      logger.debug(">> deleting vApp(%s)", vApp.getName());
      VCloudExpressClient.class.cast(client).deleteVApp(vApp.getHref());
   }

   @Override
   public VCloudExpressVApp start(@Nullable URI VDC, URI templateId, String name,
         InstantiateVAppTemplateOptions options, int... portsToOpen) {
      checkNotNull(options, "options");
      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VCloudExpressVApp vAppResponse = VCloudExpressClient.class.cast(client).instantiateVAppTemplateInVDC(VDC,
            templateId, name, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());
      if (options.shouldDeploy()) {
         logger.debug(">> deploying vApp(%s)", vAppResponse.getName());

         Task task = VCloudExpressClient.class.cast(client).deployVApp(vAppResponse.getHref());
         if (options.shouldBlock()) {
            if (!taskTester.apply(task.getHref())) {
               throw new RuntimeException(String.format("failed to %s %s: %s", "deploy", vAppResponse.getName(), task));
            }
            logger.debug("<< deployed vApp(%s)", vAppResponse.getName());
            if (options.shouldPowerOn()) {
               logger.debug(">> powering vApp(%s)", vAppResponse.getName());
               task = VCloudExpressClient.class.cast(client).powerOnVApp(vAppResponse.getHref());
               if (!taskTester.apply(task.getHref())) {
                  throw new RuntimeException(String.format("failed to %s %s: %s", "powerOn", vAppResponse.getName(),
                        task));
               }
               logger.debug("<< on vApp(%s)", vAppResponse.getName());
            }
         }
      }
      return vAppResponse;
   }

   @Override
   public Set<String> getPrivateAddresses(URI id) {
      return ImmutableSet.of();
   }

   @Override
   public Set<String> getPublicAddresses(URI id) {
      VCloudExpressVApp vApp = refreshVApp(id);
      return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

   @Override
   protected Status getStatus(VCloudExpressVApp vApp) {
      return vApp.getStatus();
   }

   @Override
   protected VCloudExpressVApp refreshVApp(URI id) {
      return VCloudExpressClient.class.cast(client).getVApp(id);
   }

   @Override
   protected Task powerOff(VCloudExpressVApp vApp) {
      return VCloudExpressClient.class.cast(client).powerOffVApp(vApp.getHref());
   }

   @Override
   protected Task reset(VCloudExpressVApp vApp) {
      return VCloudExpressClient.class.cast(client).resetVApp(vApp.getHref());
   }

   @Override
   protected Task undeploy(VCloudExpressVApp vApp) {
      return VCloudExpressClient.class.cast(client).undeployVApp(vApp.getHref());
   }
}