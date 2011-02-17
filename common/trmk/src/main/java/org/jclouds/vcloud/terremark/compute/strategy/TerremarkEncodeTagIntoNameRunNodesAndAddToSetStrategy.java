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

import java.net.URI;
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
import org.jclouds.domain.LocationScope;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;

import com.google.common.collect.Multimap;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class TerremarkEncodeTagIntoNameRunNodesAndAddToSetStrategy extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private final CreateNewKeyPairUnlessUserSpecifiedOtherwise createNewKeyPairUnlessUserSpecifiedOtherwise;

   @Inject
   protected TerremarkEncodeTagIntoNameRunNodesAndAddToSetStrategy(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            CreateNewKeyPairUnlessUserSpecifiedOtherwise createNewKeyPairUnlessUserSpecifiedOtherwise) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, executor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.createNewKeyPairUnlessUserSpecifiedOtherwise = createNewKeyPairUnlessUserSpecifiedOtherwise;
   }

   @Override
   public Map<?, Future<Void>> execute(String tag, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      assert template.getLocation().getParent().getScope() == LocationScope.REGION : "template location should have a parent of org, which should be mapped to region: "
               + template.getLocation();
      String orgId = template.getLocation().getParent().getId();
      assert orgId.startsWith("http") : "parent id should be a rest url: " + template.getLocation().getParent();
      createNewKeyPairUnlessUserSpecifiedOtherwise.execute(URI.create(orgId), tag, template.getImage()
               .getDefaultCredentials().identity, template.getOptions().as(TerremarkVCloudTemplateOptions.class));
      return super.execute(tag, count, template, goodNodes, badNodes, customizationResponses);
   }
}