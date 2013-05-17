/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.trmk.vcloud_0_8.compute.TerremarkVCloudComputeClient;
import org.jclouds.trmk.vcloud_0_8.compute.functions.TemplateToInstantiateOptions;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class StartVAppWithGroupEncodedIntoName implements CreateNodeWithGroupEncodedIntoName {
   protected final TerremarkVCloudComputeClient computeClient;
   protected final TemplateToInstantiateOptions getOptions;
   protected final Function<VApp, NodeMetadata> vAppToNodeMetadata;
   protected final Map<String, Credentials> credentialStore;
   protected final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;


   @Inject
   protected StartVAppWithGroupEncodedIntoName(TerremarkVCloudComputeClient computeClient,
            Function<VApp, NodeMetadata> vAppToNodeMetadata, TemplateToInstantiateOptions getOptions,
            Map<String, Credentials> credentialStore, PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate) {
      this.computeClient = computeClient;
      this.vAppToNodeMetadata = vAppToNodeMetadata;
      this.getOptions = checkNotNull(getOptions, "getOptions");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.prioritizeCredentialsFromTemplate = checkNotNull(prioritizeCredentialsFromTemplate, "prioritizeCredentialsFromTemplate");
   }
   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      InstantiateVAppTemplateOptions options = getOptions.apply(template);
      NodeAndInitialCredentials<VApp> from = computeClient.startAndReturnCredentials(URI.create(template.getLocation().getId()), URI.create(template
               .getImage().getId()), name, options, template.getOptions().getInboundPorts());
      LoginCredentials fromNode = from.getCredentials();
      if (credentialStore.containsKey("group#" + group)) {
         fromNode = fromNode == null ? LoginCredentials.fromCredentials(credentialStore.get("group#" + group))
                  : fromNode.toBuilder().privateKey(credentialStore.get("group#" + group).credential).build();
      }
      LoginCredentials creds = prioritizeCredentialsFromTemplate.apply(template, fromNode);
      if (creds != null)
         credentialStore.put("node#" + from.getNodeId(), creds);
      return vAppToNodeMetadata.apply(from.getNode());
   }

}
