/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link BaseVCloudComputeClient}
 * bound.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VCloudGetNodeMetadata {

   protected final VCloudClient client;
   protected final VCloudComputeClient computeClient;
   protected final Map<String, ? extends Image> images;
   protected final Map<String, ? extends Location> locations;
   protected final GetExtra getExtra;
   protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

   // hex [][][] are templateId, last two are instanceId
   public static final Pattern TAG_PATTERN_WITH_TEMPLATE = Pattern
            .compile("([^-]+)-([0-9a-f][0-9a-f][0-9a-f])[0-9a-f]+");

   public static final Pattern TAG_PATTERN_WITHOUT_TEMPLATE = Pattern.compile("([^-]+)-[0-9]+");

   @Inject
   protected VCloudGetNodeMetadata(VCloudClient client, VCloudComputeClient computeClient,
            Map<VAppStatus, NodeState> vAppStatusToNodeState, GetExtra getExtra,
            Map<String, ? extends Location> locations, Map<String, ? extends Image> images) {
      this.client = checkNotNull(client, "client");
      this.images = checkNotNull(images, "images");
      this.getExtra = checkNotNull(getExtra, "getExtra");
      this.locations = checkNotNull(locations, "locations");
      this.computeClient = checkNotNull(computeClient, "computeClient");
      this.vAppStatusToNodeState = checkNotNull(vAppStatusToNodeState, "vAppStatusToNodeState");
   }

   protected NodeMetadata getNodeMetadataByIdInVDC(String vDCId, String id) {
      VApp vApp = client.getVApp(id);

      String tag = null;
      Image image = null;
      Matcher matcher = TAG_PATTERN_WITH_TEMPLATE.matcher(vApp.getName());
      if (matcher.find()) {
         tag = matcher.group(1);
         String templateIdInHexWithoutLeadingZeros = matcher.group(2).replaceAll("^[0]+", "");
         String templateId = Integer.parseInt(templateIdInHexWithoutLeadingZeros, 16) + "";
         image = images.get(templateId);
      } else {
         matcher = TAG_PATTERN_WITHOUT_TEMPLATE.matcher(vApp.getName());
         if (matcher.find()) {
            tag = matcher.group(1);
         } else {
            tag = "NOTAG-" + vApp.getName();
         }
      }
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), locations.get(vDCId), vApp
               .getLocation(), ImmutableMap.<String, String> of(), tag, image,
               vAppStatusToNodeState.get(vApp.getStatus()), computeClient.getPublicAddresses(id),
               computeClient.getPrivateAddresses(id), getExtra.apply(vApp), null);
   }
}