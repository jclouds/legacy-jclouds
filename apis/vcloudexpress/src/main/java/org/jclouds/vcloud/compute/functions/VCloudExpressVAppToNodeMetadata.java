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

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.os.CIMOperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VCloudExpressVApp;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudExpressVAppToNodeMetadata implements Function<VCloudExpressVApp, NodeMetadata> {

   protected final VCloudExpressComputeClient computeClient;
   protected final Map<String, Credentials> credentialStore;
   protected final Supplier<Set<? extends Image>> images;
   protected final FindLocationForResource findLocationForResourceInVDC;
   protected final HardwareForVCloudExpressVApp hardwareForVCloudExpressVApp;
   protected final Map<Status, NodeState> vAppStatusToNodeState;

   @Inject
   protected VCloudExpressVAppToNodeMetadata(VCloudExpressComputeClient computeClient,
            Map<String, Credentials> credentialStore, Map<Status, NodeState> vAppStatusToNodeState,
            HardwareForVCloudExpressVApp hardwareForVCloudExpressVApp,
            FindLocationForResource findLocationForResourceInVDC, @Memoized Supplier<Set<? extends Image>> images) {
      this.images = checkNotNull(images, "images");
      this.hardwareForVCloudExpressVApp = checkNotNull(hardwareForVCloudExpressVApp, "hardwareForVCloudExpressVApp");
      this.findLocationForResourceInVDC = checkNotNull(findLocationForResourceInVDC, "findLocationForResourceInVDC");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.computeClient = checkNotNull(computeClient, "computeClient");
      this.vAppStatusToNodeState = checkNotNull(vAppStatusToNodeState, "vAppStatusToNodeState");
   }

   @Override
   public NodeMetadata apply(VCloudExpressVApp from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.location(findLocationForResourceInVDC.apply(from.getVDC()));
      builder.group(parseGroupFromName(from.getName()));
      builder.operatingSystem(from.getOsType() != null ? new CIMOperatingSystem(CIMOperatingSystem.OSType
               .fromValue(from.getOsType()), null, null, from.getOperatingSystemDescription()) : null);
      builder.hardware(hardwareForVCloudExpressVApp.apply(from));
      builder.state(vAppStatusToNodeState.get(from.getStatus()));
      builder.publicAddresses(computeClient.getPublicAddresses(from.getHref()));
      builder.privateAddresses(computeClient.getPrivateAddresses(from.getHref()));
      builder.credentials(credentialStore.get("node#" + from.getHref().toASCIIString()));
      return builder.build();
   }
}