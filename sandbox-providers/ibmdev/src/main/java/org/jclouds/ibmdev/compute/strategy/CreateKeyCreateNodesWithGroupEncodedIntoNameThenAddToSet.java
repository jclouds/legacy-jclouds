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

package org.jclouds.ibmdev.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;

import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyCreateNodesWithGroupEncodedIntoNameThenAddToSet extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {
   private final IBMDeveloperCloudClient client;
   private final Map<String, String> credentialsMap;

   @Inject
   protected CreateKeyCreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            IBMDeveloperCloudClient client, @Named("CREDENTIALS") Map<String, String> credentialsMap) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, executor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.client = checkNotNull(client, "client");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
   }

   @Override
   public Map<?, Future<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      String keyAsText = template.getOptions().getPublicKey();
      if (keyAsText != null) {
         template.getOptions().dontAuthorizePublicKey();
         try {
            client.addPublicKey(group, keyAsText);
         } catch (IllegalStateException e) {
            // must not have been found
            client.updatePublicKey(group, keyAsText);
         }
      } else {
         credentialsMap.put(group, client.generateKeyPair(group).getKeyMaterial());
      }
      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

}