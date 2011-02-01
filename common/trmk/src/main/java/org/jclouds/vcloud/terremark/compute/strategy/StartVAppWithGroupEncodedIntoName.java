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
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeClient;
import org.jclouds.vcloud.terremark.compute.functions.TemplateToInstantiateOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class StartVAppWithGroupEncodedIntoName implements CreateNodeWithGroupEncodedIntoName {
   protected final TerremarkVCloudComputeClient computeClient;
   protected final TemplateToInstantiateOptions getOptions;
   protected final Function<VCloudExpressVApp, NodeMetadata> vAppToNodeMetadata;
   private final Map<String, Credentials> credentialStore;

   @Inject
   protected StartVAppWithGroupEncodedIntoName(TerremarkVCloudComputeClient computeClient,
            Function<VCloudExpressVApp, NodeMetadata> vAppToNodeMetadata, TemplateToInstantiateOptions getOptions,
            Map<String, Credentials> credentialStore) {
      this.computeClient = computeClient;
      this.vAppToNodeMetadata = vAppToNodeMetadata;
      this.getOptions = checkNotNull(getOptions, "getOptions");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      TerremarkInstantiateVAppTemplateOptions options = getOptions.apply(template);
      VCloudExpressVApp vApp = computeClient.start(URI.create(template.getLocation().getId()), URI.create(template
               .getImage().getId()), name, options, template.getOptions().getInboundPorts());
      NodeMetadata node = vAppToNodeMetadata.apply(vApp);
      NodeMetadataBuilder builder = NodeMetadataBuilder.fromNodeMetadata(node);
      // TODO refactor this so that it is automatic in any provider
      if (template.getImage().getAdminPassword() != null) {
         builder.adminPassword(template.getImage().getAdminPassword());
         // this is going to need refactoring.. we really need a credential list in the store per
         // node.  we need to store the credential here explicitly, as there's no connection from a node
         // in vcloud to the image it was created with.
         credentialStore.put("node#" + node.getId() + "#adminPassword", new Credentials("root", template.getImage()
                  .getAdminPassword()));
      }
      return builder.build();
   }

}