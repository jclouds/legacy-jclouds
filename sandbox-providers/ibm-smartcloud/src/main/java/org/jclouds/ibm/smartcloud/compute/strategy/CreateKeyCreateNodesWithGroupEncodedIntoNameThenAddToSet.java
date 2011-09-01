/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ibm.smartcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.compute.options.IBMSmartCloudTemplateOptions;
import org.jclouds.ibm.smartcloud.domain.Key;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
         CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final IBMSmartCloudClient client;
   private final Map<String, String> credentialsMap;
   private final Supplier<String> randomSuffix;

   @Inject
   protected CreateKeyCreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            IBMSmartCloudClient client, @Named("CREDENTIALS") Map<String, String> credentialsMap,
            Supplier<String> randomSuffix) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, executor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.client = checkNotNull(client, "client");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.randomSuffix = checkNotNull(randomSuffix, "randomSuffix");
   }

   @Override
   public Map<?, Future<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      IBMSmartCloudTemplateOptions options = template.getOptions().as(IBMSmartCloudTemplateOptions.class);
      if (options.shouldAutomaticallyCreateKeyPair() && options.getKeyPair() == null) {
         Key key = createNewKey(group);
         options.keyPair(key.getName());
         credentialsMap.put(key.getName(), key.getKeyMaterial());
      }
      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

   @VisibleForTesting
   Key createNewKey(String group) {
      checkNotNull(group, "group");
      logger.debug(">> creating key group(%s)", group);
      Key key = null;
      while (key == null) {
         try {
            key = client.generateKeyPair(getNextName(group));
            logger.debug("<< created key(%s)", key.getName());
         } catch (IllegalStateException e) {

         }
      }
      return key;
   }

   private String getNextName(String group) {
      return String.format("jclouds#%s#%s", group, randomSuffix.get());
   }

}
