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
package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromCommaDelimitedValue;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getCredentialsFrom;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getIpsFromVApp;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.toComputeOs;

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
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.*;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppToNodeMetadata implements Function<VApp, NodeMetadata> {
   @Resource
   protected static Logger logger = Logger.NULL;

   protected final FindLocationForResource findLocationForResourceInVDC;
   protected final Function<VApp, Hardware> hardwareForVApp;
   protected final Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus;
   protected final Map<String, Credentials> credentialStore;
   protected final GroupNamingConvention nodeNamingConvention;

   @Inject
   protected VAppToNodeMetadata(Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus, Map<String, Credentials> credentialStore,
         FindLocationForResource findLocationForResourceInVDC, Function<VApp, Hardware> hardwareForVApp,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.hardwareForVApp = checkNotNull(hardwareForVApp, "hardwareForVApp");
      this.findLocationForResourceInVDC = checkNotNull(findLocationForResourceInVDC, "findLocationForResourceInVDC");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.vAppStatusToNodeStatus = checkNotNull(vAppStatusToNodeStatus, "vAppStatusToNodeStatus");
   }

   public NodeMetadata apply(VApp from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.uri(from.getHref());
      builder.name(from.getName());
      if (!isNullOrEmpty(from.getDescription()) && from.getDescription().indexOf('=') != -1)
         addMetadataAndParseTagsFromCommaDelimitedValue(builder,
                  Splitter.on('\n').withKeyValueSeparator("=").split(from.getDescription()));
      builder.hostname(from.getName());
      builder.location(findLocationForResourceInVDC.apply(from.getVDC()));
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getName()));
      builder.operatingSystem(toComputeOs(from, null));
      builder.hardware(hardwareForVApp.apply(from));
      builder.status(vAppStatusToNodeStatus.get(from.getStatus()));
      Set<String> addresses = getIpsFromVApp(from);
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
