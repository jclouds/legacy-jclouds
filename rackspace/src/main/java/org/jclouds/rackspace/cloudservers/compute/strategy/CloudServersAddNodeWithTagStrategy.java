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

package org.jclouds.rackspace.cloudservers.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Server;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudServersAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   private final CloudServersClient client;

   @Inject
   protected CloudServersAddNodeWithTagStrategy(CloudServersClient client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public NodeMetadata execute(String tag, String name, Template template) {
      Server server = client.createServer(name, Integer.parseInt(template.getImage().getProviderId()), Integer
               .parseInt(template.getHardware().getProviderId()));
      return new NodeMetadataImpl(server.getId() + "", name, server.getId() + "", new LocationImpl(LocationScope.HOST,
               server.getHostId(), server.getHostId(), template.getLocation()), null, server.getMetadata(), tag,
               template.getImage().getId(), template.getImage().getOperatingSystem(), NodeState.PENDING, server
                        .getAddresses().getPublicAddresses(), server.getAddresses().getPrivateAddresses(), ImmutableMap
                        .<String, String> of(), new Credentials("root", server.getAdminPass()));
   }

}