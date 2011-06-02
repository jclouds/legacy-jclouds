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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudDestroyNodeStrategy implements DestroyNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Predicate<URI> successTester;
   protected final VCloudClient client;
   protected final GetNodeMetadataStrategy getNode;

   @Inject
   protected VCloudDestroyNodeStrategy(Predicate<URI> successTester, VCloudClient client,
            GetNodeMetadataStrategy getNode) {
      this.successTester = successTester;
      this.client = client;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata destroyNode(String id) {
      URI vappId = URI.create(checkNotNull(id, "node.id"));
      VApp vApp = client.getVAppClient().getVApp(vappId);
      if (vApp == null)
         return null;

      waitForPendingTasksToComplete(vApp);

      vApp = undeployVAppIfDeployed(vApp);
      deleteVApp(vApp);
      try {
         return getNode.getNode(id);
      } catch (AuthorizationException e) {
         // vcloud bug will sometimes throw an exception getting the vapp right after deleting it.
         logger.trace("authorization error getting %s after deletion: %s", id, e.getMessage());
         return null;
      }
   }

   void waitForPendingTasksToComplete(VApp vApp) {
      for (Task task : vApp.getTasks())
         waitForTask(task, vApp);
   }

   public void waitForTask(Task task, VApp vAppResponse) {
      if (!successTester.apply(task.getHref())) {
         throw new RuntimeException(String.format("failed to %s %s: %s", task.getName(), vAppResponse.getName(), task));
      }
   }

   void deleteVApp(VApp vApp) {
      logger.debug(">> deleting vApp(%s)", vApp.getHref());
      waitForTask(client.getVAppClient().deleteVApp(vApp.getHref()), vApp);
      logger.debug("<< deleted vApp(%s)", vApp.getHref());
   }

   VApp undeployVAppIfDeployed(VApp vApp) {
      if (vApp.getStatus() != Status.OFF) {
         logger.debug(">> undeploying vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         try {
            waitForTask(client.getVAppClient().undeployVApp(vApp.getHref()), vApp);
            vApp = client.getVAppClient().getVApp(vApp.getHref());
            logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
         } catch (IllegalStateException e) {
            logger.warn(e, "<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
         }
      }
      return vApp;
   }
}