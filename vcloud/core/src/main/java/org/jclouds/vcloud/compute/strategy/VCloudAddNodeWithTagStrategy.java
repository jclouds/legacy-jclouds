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
package org.jclouds.vcloud.compute.strategy;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   protected final VCloudClient client;
   protected final VCloudComputeClient computeClient;
   protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

   @Inject
   protected VCloudAddNodeWithTagStrategy(VCloudClient client, VCloudComputeClient computeClient,
            Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      this.client = client;
      this.computeClient = computeClient;
      this.vAppStatusToNodeState = vAppStatusToNodeState;
   }

   @Override
   public NodeMetadata execute(String tag, String name, Template template) {

      InstantiateVAppTemplateOptions options = processorCount(
               Double.valueOf(template.getSize().getCores()).intValue()).memory(
               template.getSize().getRam()).disk(template.getSize().getDisk() * 1024 * 1024l);
      Map<String, String> metaMap = computeClient.start(template.getLocation().getId(), name,
               template.getImage().getProviderId(), options, template.getOptions()
                        .getInboundPorts());
      VApp vApp = client.getVApp(metaMap.get("id"));
      return newCreateNodeResponse(tag, template, metaMap, vApp);
   }

   protected NodeMetadata newCreateNodeResponse(String tag, Template template,
            Map<String, String> metaMap, VApp vApp) {
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vApp.getId(), template
               .getLocation(), vApp.getLocation(), ImmutableMap.<String, String> of(), tag,
               template.getImage(), vAppStatusToNodeState.get(vApp.getStatus()), computeClient
                        .getPublicAddresses(vApp.getId()), computeClient.getPrivateAddresses(vApp
                        .getId()), ImmutableMap.<String, String> of(), new Credentials(metaMap
                        .get("username"), metaMap.get("password")));
   }

}