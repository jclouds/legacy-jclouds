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
package org.jclouds.joyent.sdc.v6_5.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
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
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.joyent.sdc.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.sdc.v6_5.compute.options.SDCTemplateOptions;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatacenterAndName;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApplySDCTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
         CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private final LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyCache;

   @Inject
   protected ApplySDCTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            GroupNamingConvention.Factory namingConvention,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyCache) {
      super(addNodeWithTagStrategy, listNodesStrategy, namingConvention, executor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.keyCache = checkNotNull(keyCache, "keyCache");
   }

   @Override
   public Map<?, Future<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
         Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      Template mutableTemplate = template.clone();

      SDCTemplateOptions templateOptions = SDCTemplateOptions.class.cast(mutableTemplate.getOptions());

      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      String datacenter = mutableTemplate.getLocation().getId();

      if (templateOptions.shouldGenerateKey()) {
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