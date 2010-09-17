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

package org.jclouds.vcloud.compute.strategy;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.os.CIMOperatingSystem;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudExpressAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   protected final VCloudExpressClient client;
   protected final VCloudExpressComputeClient computeClient;
   protected final Map<Status, NodeState> vAppStatusToNodeState;

   @Inject
   protected VCloudExpressAddNodeWithTagStrategy(VCloudExpressClient client, VCloudExpressComputeClient computeClient,
            Map<Status, NodeState> vAppStatusToNodeState) {
      this.client = client;
      this.computeClient = computeClient;
      this.vAppStatusToNodeState = vAppStatusToNodeState;
   }

   @Override
   public NodeMetadata execute(String tag, String name, Template template) {
      InstantiateVAppTemplateOptions options = processorCount((int) getCores(template.getHardware())).memory(
               template.getHardware().getRam()).disk(
               (long) ((template.getHardware().getVolumes().get(0).getSize()) * 1024 * 1024l));
      if (!template.getOptions().shouldBlockUntilRunning())
         options.block(false);
      Map<String, String> metaMap = computeClient.start(URI.create(template.getLocation().getId()), URI.create(template
               .getImage().getId()), name, options, template.getOptions().getInboundPorts());
      VCloudExpressVApp vApp = client.getVApp(URI.create(metaMap.get("id")));
      return newCreateNodeResponse(tag, template, metaMap, vApp);
   }

   protected NodeMetadata newCreateNodeResponse(String tag, Template template, Map<String, String> metaMap,
            VCloudExpressVApp vApp) {
      return new NodeMetadataImpl(vApp.getHref().toASCIIString(), vApp.getName(), vApp.getHref().toASCIIString(),
               template.getLocation(), vApp.getHref(), ImmutableMap.<String, String> of(), tag, template.getHardware(),
               template.getImage().getId(), getOperatingSystemForVAppOrDefaultTo(vApp, template.getImage()
                        .getOperatingSystem()), vAppStatusToNodeState.get(vApp.getStatus()), computeClient
                        .getPublicAddresses(vApp.getHref()), computeClient.getPrivateAddresses(vApp.getHref()),
               new Credentials(metaMap.get("username"), metaMap.get("password")));
   }

   private OperatingSystem getOperatingSystemForVAppOrDefaultTo(VCloudExpressVApp vApp, OperatingSystem operatingSystem) {
      return vApp.getOsType() != null ? new CIMOperatingSystem(CIMOperatingSystem.OSType.fromValue(vApp.getOsType()),
               null, null, vApp.getOperatingSystemDescription()) : operatingSystem;
   }

}