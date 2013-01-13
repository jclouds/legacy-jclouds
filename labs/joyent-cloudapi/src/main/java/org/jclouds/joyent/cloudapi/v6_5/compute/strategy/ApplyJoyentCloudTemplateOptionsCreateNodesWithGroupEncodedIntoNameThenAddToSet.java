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
package org.jclouds.joyent.cloudapi.v6_5.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudProperties.AUTOGENERATE_KEYS;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApplyJoyentCloudTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
         CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private final LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyCache;
   private final boolean defaultToAutogenerateKeys;

   @Inject
   protected ApplyJoyentCloudTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            GroupNamingConvention.Factory namingConvention,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyCache, 
            @Named(AUTOGENERATE_KEYS) boolean defaultToAutogenerateKeys) {
      super(addNodeWithTagStrategy, listNodesStrategy, namingConvention, userExecutor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.keyCache = checkNotNull(keyCache, "keyCache");
      this.defaultToAutogenerateKeys = defaultToAutogenerateKeys;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
         Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      Template mutableTemplate = template.clone();

      JoyentCloudTemplateOptions templateOptions = JoyentCloudTemplateOptions.class.cast(mutableTemplate.getOptions());

      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      String datacenter = mutableTemplate.getLocation().getId();
      
      if (!templateOptions.shouldGenerateKey().isPresent())
         templateOptions.generateKey(defaultToAutogenerateKeys);

      if (templateOptions.shouldGenerateKey().get()) {
         KeyAndPrivateKey keyPair = keyCache.getUnchecked(DatacenterAndName.fromDatacenterAndName(datacenter, namingConvention.create()
               .sharedNameForGroup(group)));
         // in order to delete the key later
         keyCache.asMap().put(DatacenterAndName.fromDatacenterAndName(datacenter, keyPair.getKey().getName()), keyPair);
         templateOptions.overrideLoginPrivateKey(keyPair.getPrivateKey());
      }
      checkArgument(templateOptions.getRunScript() == null || templateOptions.getLoginPrivateKey() != null,
            "when specifying runScript, you must either set overrideLoginPrivateKey, or generateKey(true)");
      return super.execute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }
}
