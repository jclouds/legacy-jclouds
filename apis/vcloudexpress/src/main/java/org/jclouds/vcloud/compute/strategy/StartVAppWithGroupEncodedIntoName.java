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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class StartVAppWithGroupEncodedIntoName implements CreateNodeWithGroupEncodedIntoName {
   protected final VCloudExpressClient client;
   protected final VCloudExpressComputeClient computeClient;
   protected final Function<VCloudExpressVApp, NodeMetadata> vAppToNodeMetadata;

   @Inject
   protected StartVAppWithGroupEncodedIntoName(VCloudExpressClient client, VCloudExpressComputeClient computeClient,
         Function<VCloudExpressVApp, NodeMetadata> vAppToNodeMetadata) {
      this.client = client;
      this.computeClient = computeClient;
      this.vAppToNodeMetadata = vAppToNodeMetadata;
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {
      InstantiateVAppTemplateOptions options = processorCount((int) getCores(template.getHardware())).memory(
            template.getHardware().getRam()).disk(
            (long) ((template.getHardware().getVolumes().get(0).getSize()) * 1024 * 1024l));
      if (!template.getOptions().shouldBlockUntilRunning())
         options.block(false);
      VCloudExpressVApp vApp = computeClient.start(URI.create(template.getLocation().getId()),
            URI.create(template.getImage().getId()), name, options, template.getOptions().getInboundPorts());
      return vAppToNodeMetadata.apply(vApp);
   }

}