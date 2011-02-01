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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getCredentialsFrom;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getPrivateIpsFromVApp;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getPublicIpsFromVApp;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.toComputeOs;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppToNodeMetadata implements Function<VApp, NodeMetadata> {
   @Resource
   protected static Logger logger = Logger.NULL;

   protected final FindLocationForResource findLocationForResourceInVDC;
   protected final Function<VApp, Hardware> hardwareForVApp;
   protected final Map<Status, NodeState> vAppStatusToNodeState;
   protected final Map<String, Credentials> credentialStore;

   @Inject
   protected VAppToNodeMetadata(Map<Status, NodeState> vAppStatusToNodeState, Map<String, Credentials> credentialStore,
            FindLocationForResource findLocationForResourceInVDC, Function<VApp, Hardware> hardwareForVApp) {
      this.hardwareForVApp = checkNotNull(hardwareForVApp, "hardwareForVApp");
      this.findLocationForResourceInVDC = checkNotNull(findLocationForResourceInVDC, "findLocationForResourceInVDC");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.vAppStatusToNodeState = checkNotNull(vAppStatusToNodeState, "vAppStatusToNodeState");
   }

   public NodeMetadata apply(VApp from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.location(findLocationForResourceInVDC.apply(from.getVDC()));
      builder.group(parseGroupFromName(from.getName()));
      builder.operatingSystem(toComputeOs(from, null));
      builder.hardware(hardwareForVApp.apply(from));
      builder.state(vAppStatusToNodeState.get(from.getStatus()));
      builder.publicAddresses(getPublicIpsFromVApp(from));
      builder.privateAddresses(getPrivateIpsFromVApp(from));
      builder.credentials(getCredentialsFrom(from));
      Credentials fromApi = getCredentialsFrom(from);
      if (fromApi != null && !credentialStore.containsKey("node#" + from.getHref().toASCIIString()))
         credentialStore.put("node#" + from.getHref().toASCIIString(), fromApi);
      builder.credentials(credentialStore.get("node#" + from.getHref().toASCIIString()));
      return builder.build();
   }
}