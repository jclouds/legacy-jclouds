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
package org.jclouds.trmk.vcloud_0_8.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cim.OSType;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.trmk.vcloud_0_8.compute.TerremarkVCloudComputeClient;
import org.jclouds.trmk.vcloud_0_8.compute.domain.KeyPairCredentials;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppToNodeMetadata implements Function<VApp, NodeMetadata> {

   protected final TerremarkVCloudComputeClient computeClient;
   protected final Map<String, Credentials> credentialStore;
   protected final Supplier<Set<? extends Image>> images;
   protected final FindLocationForResource findLocationForResourceInVDC;
   protected final HardwareForVCloudExpressVApp hardwareForVCloudExpressVApp;
   protected final Map<Status, NodeState> vAppStatusToNodeState;
   protected final ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap;

   @Inject
   protected VAppToNodeMetadata(TerremarkVCloudComputeClient computeClient,
         Map<String, Credentials> credentialStore, Map<Status, NodeState> vAppStatusToNodeState,
         HardwareForVCloudExpressVApp hardwareForVCloudExpressVApp,
         FindLocationForResource findLocationForResourceInVDC, @Memoized Supplier<Set<? extends Image>> images,
         ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap) {
      this.images = checkNotNull(images, "images");
      this.hardwareForVCloudExpressVApp = checkNotNull(hardwareForVCloudExpressVApp, "hardwareForVCloudExpressVApp");
      this.findLocationForResourceInVDC = checkNotNull(findLocationForResourceInVDC, "findLocationForResourceInVDC");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.computeClient = checkNotNull(computeClient, "computeClient");
      this.vAppStatusToNodeState = checkNotNull(vAppStatusToNodeState, "vAppStatusToNodeState");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
   }

   @Override
   public NodeMetadata apply(VApp from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.hostname(from.getName());
      Location vdcLocation = findLocationForResourceInVDC.apply(from.getVDC());
      builder.location(vdcLocation);
      if (from.getOsType() != null && OSType.fromValue(from.getOsType()) != OSType.UNRECOGNIZED) {
         builder.operatingSystem(new CIMOperatingSystem(OSType.fromValue(from.getOsType()), "", null, from
               .getOperatingSystemDescription()));
      } else if (from.getOperatingSystemDescription() != null) {
         OperatingSystem.Builder osBuilder = new OperatingSystem.Builder();
         if (from.getOsType() != null)
            osBuilder.name(from.getOsType() + "");
         osBuilder.family(ComputeServiceUtils.parseOsFamilyOrUnrecognized(from.getOperatingSystemDescription()));
         osBuilder.version("");
         osBuilder.is64Bit(from.getOperatingSystemDescription().indexOf("64") != -1);
         osBuilder.description(from.getOperatingSystemDescription());
         builder.operatingSystem(osBuilder.build());
      }
      builder.hardware(hardwareForVCloudExpressVApp.apply(from));
      builder.state(vAppStatusToNodeState.get(from.getStatus()));
      builder.publicAddresses(computeClient.getPublicAddresses(from.getHref()));
      builder.privateAddresses(computeClient.getPrivateAddresses(from.getHref()));
      String group = parseGroupFromName(from.getName());
      if (group != null) {
         builder.group(group);
         installCredentialsFromCache(from.getHref(), URI.create(vdcLocation.getParent().getId()), group, builder);
      } else {
         builder.credentials(credentialStore.get("node#" + from.getHref().toASCIIString()));
      }
      return builder.build();
   }

   protected void installCredentialsFromCache(URI nodeId, URI orgId, String group, NodeMetadataBuilder builder) {
      OrgAndName orgAndName = new OrgAndName(orgId, group);
      if (credentialsMap.containsKey(orgAndName)) {
         Credentials creds = credentialsMap.get(orgAndName);
         builder.credentials(creds);
         credentialStore.put("node#" + nodeId, creds);
      }
      // this is going to need refactoring.. we really need a credential list in
      // the store per
      // node.
      String adminPasswordKey = "node#" + nodeId + "#adminPassword";
      if (credentialStore.containsKey(adminPasswordKey)) {
         builder.adminPassword(credentialStore.get(adminPasswordKey).credential);
      }
   }
}