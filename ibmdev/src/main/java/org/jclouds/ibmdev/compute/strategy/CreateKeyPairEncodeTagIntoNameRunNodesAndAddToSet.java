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

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.io.Payload;
import org.jclouds.util.Utils;

/**
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyPairEncodeTagIntoNameRunNodesAndAddToSet extends EncodeTagIntoNameRunNodesAndAddToSetStrategy {
   private final IBMDeveloperCloudClient client;
   private final Map<String, String> credentialsMap;

   @Inject
   protected CreateKeyPairEncodeTagIntoNameRunNodesAndAddToSet(AddNodeWithTagStrategy addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy, @Named("NAMING_CONVENTION") String nodeNamingConvention,
            ComputeUtils utils, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            IBMDeveloperCloudClient client, @Named("CREDENTIALS") Map<String, String> credentialsMap) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, utils, executor);
      this.client = checkNotNull(client, "client");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
   }

   @Override
   public Map<?, Future<Void>> execute(String tag, int count, Template template, Set<NodeMetadata> nodes,
            Map<NodeMetadata, Exception> badNodes) {
      Payload key = template.getOptions().getPublicKey();
      if (key != null) {
         String keyAsText;
         try {
            keyAsText = Utils.toStringAndClose(key.getInput());
         } catch (IOException e1) {
            throw new RuntimeException(e1);
         }
         template.getOptions().dontAuthorizePublicKey();
         try {
            client.addPublicKey(tag, keyAsText);
         } catch (IllegalStateException e) {
            // must not have been found
            client.updatePublicKey(tag, keyAsText);
         }
      } else {
         credentialsMap.put(tag, client.generateKeyPair(tag).getKeyMaterial());
      }
      return super.execute(tag, count, template, nodes, badNodes);
   }

}