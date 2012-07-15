/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.vcloud.director.v1_5.compute.util.VCloudDirectorComputeUtils.getCredentialsFrom;
import static org.jclouds.vcloud.director.v1_5.compute.util.VCloudDirectorComputeUtils.getIpsFromVm;
import static org.jclouds.vcloud.director.v1_5.compute.util.VCloudDirectorComputeUtils.toComputeOs;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.util.InetAddresses2.IsPrivateIPAddress;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class VmToNodeMetadata implements Function<Vm, NodeMetadata> {
   @Resource
   protected static Logger logger = Logger.NULL;

   protected final FindLocationForResource findLocationForResourceInVDC;
   protected final Function<Vm, Hardware> hardwareForVm;
   protected final Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus;
   protected final Map<String, Credentials> credentialStore;
   protected final GroupNamingConvention nodeNamingConvention;

   @Inject
   protected VmToNodeMetadata(Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus, Map<String, Credentials> credentialStore,
         FindLocationForResource findLocationForResourceInVDC, Function<Vm, Hardware> hardwareForVm,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.hardwareForVm = checkNotNull(hardwareForVm, "hardwareForVm");
      this.findLocationForResourceInVDC = checkNotNull(findLocationForResourceInVDC, "findLocationForResourceInVDC");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.vAppStatusToNodeStatus = checkNotNull(vAppStatusToNodeStatus, "vAppStatusToNodeStatus");
   }

   public NodeMetadata apply(Vm from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.hostname(from.getName());
      builder.location(findLocationForResourceInVDC.apply(
            Iterables.find(from.getLinks(), LinkPredicates.typeEquals(VCloudDirectorMediaType.VDC))));
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getName()));
      builder.operatingSystem(toComputeOs(from));
      builder.hardware(hardwareForVm.apply(from));
      builder.status(vAppStatusToNodeStatus.get(from.getStatus()));
      Set<String> addresses = getIpsFromVm(from);
      builder.publicAddresses(filter(addresses, not(IsPrivateIPAddress.INSTANCE)));
      builder.privateAddresses(filter(addresses, IsPrivateIPAddress.INSTANCE));

      // normally, we don't affect the credential store when reading vApps.
      // However, login user, etc, is actually in the metadata, so lets see
      Credentials fromApi = getCredentialsFrom(from);
      if (fromApi != null && !credentialStore.containsKey("node#" + from.getHref().toASCIIString()))
         credentialStore.put("node#" + from.getHref().toASCIIString(), fromApi);
      return builder.build();
   }
}
