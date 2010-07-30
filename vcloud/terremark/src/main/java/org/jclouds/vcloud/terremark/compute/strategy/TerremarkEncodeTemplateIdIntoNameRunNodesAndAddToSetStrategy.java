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
package org.jclouds.vcloud.terremark.compute.strategy;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.LocationScope;
import org.jclouds.vcloud.compute.strategy.EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;

import java.util.concurrent.Future;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class TerremarkEncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy extends
         EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy {

   private final CreateNewKeyPairUnlessUserSpecifiedOtherwise createNewKeyPairUnlessUserSpecifiedOtherwise;

   @Inject
   protected TerremarkEncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy(
            AddNodeWithTagStrategy addNodeWithTagStrategy, ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, SecureRandom random,
            CreateNewKeyPairUnlessUserSpecifiedOtherwise createNewKeyPairUnlessUserSpecifiedOtherwise) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, utils, executor, random);
      this.createNewKeyPairUnlessUserSpecifiedOtherwise = createNewKeyPairUnlessUserSpecifiedOtherwise;
   }

   @Override
   public Map<?, Future<Void>> execute(String tag, int count, Template template, Set<NodeMetadata> nodes,
            Map<NodeMetadata, Exception> badNodes) {
      assert template.getLocation().getParent().getScope() == LocationScope.REGION : "template location should have a parent of org, which should be mapped to region: "
               + template.getLocation();
      createNewKeyPairUnlessUserSpecifiedOtherwise.execute(template.getLocation().getParent().getId(), tag, template
               .getOptions().as(TerremarkVCloudTemplateOptions.class));
      return super.execute(tag, count, template, nodes, badNodes);
   }
}