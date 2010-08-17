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

package org.jclouds.vcloud.terremark.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeClient;
import org.jclouds.vcloud.terremark.compute.functions.TemplateToInstantiateOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   protected final TerremarkVCloudClient client;
   protected final TerremarkVCloudComputeClient computeClient;
   protected final GetNodeMetadataStrategy getNode;
   protected final TemplateToInstantiateOptions getOptions;

   @Inject
   protected TerremarkVCloudAddNodeWithTagStrategy(TerremarkVCloudClient client,
            TerremarkVCloudComputeClient computeClient, GetNodeMetadataStrategy getNode,
            TemplateToInstantiateOptions getOptions) {
      this.client = client;
      this.computeClient = computeClient;
      this.getNode = getNode;
      this.getOptions = checkNotNull(getOptions, "getOptions");
   }

   @Override
   public NodeMetadata execute(String tag, String name, Template template) {
      TerremarkInstantiateVAppTemplateOptions options = getOptions.apply(template);
      Map<String, String> metaMap = computeClient.start(URI.create(template.getLocation().getId()), URI.create(template
               .getImage().getId()), name, options, template.getOptions().getInboundPorts());
      return getNode.execute(metaMap.get("id"));
   }

}