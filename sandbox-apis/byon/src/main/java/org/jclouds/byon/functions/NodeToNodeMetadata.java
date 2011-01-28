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

package org.jclouds.byon.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class NodeToNodeMetadata implements Function<Node, NodeMetadata> {
   private final Supplier<Location> location;
   private final Map<String, Credentials> credentialStore;

   @Inject
   NodeToNodeMetadata(Supplier<Location> location, Map<String, Credentials> credentialStore) {
      this.location = checkNotNull(location, "location");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   public NodeMetadata apply(Node from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId());
      builder.name(from.getName());
      builder.location(location.get());
      builder.tag(from.getGroup());
      // TODO add tags!
      builder.operatingSystem(new OperatingSystemBuilder().arch(from.getOsArch()).family(
               OsFamily.fromValue(from.getOsFamily())).name(from.getOsName()).version(from.getOsVersion()).description(
               from.getDescription()).build());
      builder.state(NodeState.RUNNING);
      builder.publicAddresses(ImmutableSet.<String> of(from.getHostname()));
      Credentials creds = new Credentials(from.getUsername(), new String(CryptoStreams.base64(from.getCredential()),
               Charsets.UTF_8));
      builder.credentials(creds);
      if (from.getSudoPassword() != null)
         builder.adminPassword(new String(CryptoStreams.base64(from.getSudoPassword()), Charsets.UTF_8));
      credentialStore.put("node#" + from.getId(), creds);
      return builder.build();
   }

}
