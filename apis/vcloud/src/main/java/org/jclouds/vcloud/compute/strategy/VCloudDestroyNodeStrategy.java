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
      VApp vApp = client.getVApp(vappId);
      if (vApp == null)
         return null;
      vApp = powerOffVAppIfDeployed(vApp);
      vApp = undeployVAppIfDeployed(vApp);
      deleteVApp(vappId);
      try {
         return getNode.getNode(id);
      } catch (AuthorizationException e) {
         logger.trace("authorization error getting %s after deletion: %s", id, e.getMessage());
         return null;
      }
   }

   void deleteVApp(URI vappId) {
      logger.debug(">> deleting vApp(%s)", vappId);
      Task task = client.deleteVApp(vappId);
      if (!successTester.apply(task.getHref())) {
         throw new RuntimeException(String.format("failed to %s %s: %s", "delete", vappId, task));
      }
      logger.debug("<< deleted vApp(%s)", vappId);
   }

   VApp undeployVAppIfDeployed(VApp vApp) {
      if (vApp.getStatus().compareTo(Status.RESOLVED) > 0) {
         logger.debug(">> undeploying vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         Task task = client.undeployVAppOrVm(vApp.getHref());
         if (!successTester.apply(task.getHref())) {
            // TODO timeout
            throw new RuntimeException(String.format("failed to %s %s: %s", "undeploy", vApp.getName(), task));
         }
         vApp = client.getVApp(vApp.getHref());
         logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
      }
      return vApp;
   }

   VApp powerOffVAppIfDeployed(VApp vApp) {
      if (vApp.getStatus().compareTo(Status.OFF) > 0) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         Task task = client.powerOffVAppOrVm(vApp.getHref());
         if (!successTester.apply(task.getHref())) {
            // TODO timeout
            throw new RuntimeException(String.format("failed to %s %s: %s", "powerOff", vApp.getName(), task));
         }
         vApp = client.getVApp(vApp.getHref());
         logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
      }
      return vApp;
   }
}