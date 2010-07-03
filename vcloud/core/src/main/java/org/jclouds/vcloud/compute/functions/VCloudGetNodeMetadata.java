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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link BaseVCloudComputeClient}
 * bound.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VCloudGetNodeMetadata {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   protected final VCloudClient client;
   protected final VCloudComputeClient computeClient;
   protected final Provider<Set<? extends Image>> images;
   protected final FindLocationForResourceInVDC findLocationForResourceInVDC;
   protected final GetExtra getExtra;
   protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

   // hex [][][] are templateId, last two are instanceId
   public static final Pattern TAG_PATTERN_WITH_TEMPLATE = Pattern
            .compile("([^-]+)-([0-9a-f][0-9a-f][0-9a-f])[0-9a-f]+");

   public static final Pattern TAG_PATTERN_WITHOUT_TEMPLATE = Pattern.compile("([^-]+)-[0-9]+");

   @Inject
   VCloudGetNodeMetadata(VCloudClient client, VCloudComputeClient computeClient,
            Map<VAppStatus, NodeState> vAppStatusToNodeState, GetExtra getExtra,
            FindLocationForResourceInVDC findLocationForResourceInVDC,
            Provider<Set<? extends Image>> images) {
      this.client = checkNotNull(client, "client");
      this.images = checkNotNull(images, "images");
      this.getExtra = checkNotNull(getExtra, "getExtra");
      this.findLocationForResourceInVDC = checkNotNull(findLocationForResourceInVDC,
               "findLocationForResourceInVDC");
      this.computeClient = checkNotNull(computeClient, "computeClient");
      this.vAppStatusToNodeState = checkNotNull(vAppStatusToNodeState, "vAppStatusToNodeState");
   }

   public NodeMetadata execute(String id) {
      VApp vApp = client.getVApp(id);
      if (vApp == null)
         return null;

      String tag = null;
      Image image = null;
      Matcher matcher = vApp.getName() != null ? TAG_PATTERN_WITH_TEMPLATE.matcher(vApp.getName())
               : null;

      final Location location = findLocationForResourceInVDC.apply(vApp, vApp.getVDC().getId());
      if (matcher != null && matcher.find()) {
         tag = matcher.group(1);
         String templateIdInHexWithoutLeadingZeros = matcher.group(2).replaceAll("^[0]+", "");
         final String templateId = Integer.parseInt(templateIdInHexWithoutLeadingZeros, 16) + "";
         try {
            image = Iterables.find(images.get(), new Predicate<Image>() {

               @Override
               public boolean apply(Image input) {
                  return input.getProviderId().equals(templateId)
                           && input.getLocation().equals(location);
               }

            });
         } catch (NoSuchElementException e) {
            logger.warn(
                     "could not find a matching image for vapp %s; vapptemplate %s in location %s",
                     vApp, templateId, location);
         }
      } else {
         matcher = TAG_PATTERN_WITHOUT_TEMPLATE.matcher(vApp.getName());
         if (matcher.find()) {
            tag = matcher.group(1);
         } else {
            tag = "NOTAG-" + vApp.getName();
         }
      }
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vApp.getId(), location, vApp
               .getLocation(), ImmutableMap.<String, String> of(), tag, image,
               vAppStatusToNodeState.get(vApp.getStatus()), computeClient.getPublicAddresses(id),
               computeClient.getPrivateAddresses(id), getExtra.apply(vApp), null);
   }
}