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

package org.jclouds.servermanager.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.servermanager.ServerManager;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerManagerDestroyNodeStrategy implements DestroyNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ServerManager client;
   private final GetNodeMetadataStrategy getNodeMetadataStrategy;

   @Inject
   protected ServerManagerDestroyNodeStrategy(ServerManager client, GetNodeMetadataStrategy getNodeMetadataStrategy) {
      this.client = checkNotNull(client, "client");
      this.getNodeMetadataStrategy = checkNotNull(getNodeMetadataStrategy, "getNodeMetadataStrategy");
   }

   @Override
   public NodeMetadata execute(String id) {

      NodeMetadata node = getNodeMetadataStrategy.execute(id);
      if (node == null)
         return node;

      logger.debug(">> destroying server(%s)", id);
      client.destroyServer(Integer.parseInt(id));
      logger.debug("<< destroyed server(%s)", id);

      return node;
   }
}
