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
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.servermanager.Server;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.servermanager.compute.functions.ServerToNodeMetadata;

/**
 * This creates a node in the backend client. You create it with the parameters of the
 * {@link Template} object. Then, convert it to a {@link NodeMetadata} object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ServerManagerAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<String, Credentials> credentialStore;
   private final ServerManager client;
   private final ServerToNodeMetadata serverToNodeMetadata;

   @Inject
   public ServerManagerAddNodeWithTagStrategy(Map<String, Credentials> credentialStore, ServerManager client,
         ServerToNodeMetadata serverToNodeMetadata) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.client = checkNotNull(client, "client");
      this.serverToNodeMetadata = checkNotNull(serverToNodeMetadata, "serverToNodeMetadata");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeMetadata execute(String tag, String name, Template template) {
      checkState(tag != null, "tag (that which groups identical nodes together) must be specified");
      checkState(name != null && name.indexOf(tag) != -1, "name should have %s encoded into it", tag);

      logger.debug(">> instantiating new server dc(%s) name(%s) image(%s) hardware(%s)",
            template.getLocation().getId(), name, template.getImage().getProviderId(), template.getHardware()
                  .getProviderId());

      // create the backend object using parameters from the template.
      Server from = client.createServerInDC(template.getLocation().getId(), name,
            Integer.parseInt(template.getImage().getProviderId()),
            Integer.parseInt(template.getHardware().getProviderId()));
      // store the credentials so that later functions can use them
      credentialStore.put(from.id + "", new Credentials(from.loginUser, from.password));
      logger.debug("<< instantiated server(%s)", from.id);
      return serverToNodeMetadata.apply(from);
   }

}