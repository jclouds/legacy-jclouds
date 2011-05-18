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
package org.jclouds.ibm.smartcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.ibm.smartcloud.predicates.InstanceActiveOrFailed;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class IBMSmartCloudDestroyNodeStrategy implements DestroyNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final IBMSmartCloudClient client;
   private final GetNodeMetadataStrategy getNode;

   @Inject
   protected IBMSmartCloudDestroyNodeStrategy(IBMSmartCloudClient client, GetNodeMetadataStrategy getNode) {
      this.client = checkNotNull(client, "client");
      this.getNode = checkNotNull(getNode, "getNode");
   }

   @Override
   public NodeMetadata destroyNode(String id) {
      Instance instance = client.getInstance(id);
      if (instance != null && instance.getStatus() != Instance.Status.DEPROVISIONING
               && instance.getStatus() != Instance.Status.DEPROVISION_PENDING) {
         // often it takes 8 minutes to finish provisioning a suse host
         int timeout = (instance.getStatus() == Instance.Status.NEW || instance.getStatus() == Instance.Status.PROVISIONING) ? 600
                  : 30;
         logger.debug(">> awaiting up to %s seconds for instance %s to be ready for delete", timeout, id);
         boolean ready = new RetryablePredicate<Instance>(new InstanceActiveOrFailed(client), timeout, 2,
                  TimeUnit.SECONDS).apply(instance);
         logger.debug(">> instance state is %sready, deleting", ready ? "" : "not ", id);
         client.deleteInstance(instance.getId());
      }
      return instance != null ? getNode.getNode(id) : null;
   }
}
