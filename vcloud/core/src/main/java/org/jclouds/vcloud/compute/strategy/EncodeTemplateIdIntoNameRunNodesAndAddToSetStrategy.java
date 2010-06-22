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
package org.jclouds.vcloud.compute.strategy;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;

import com.google.common.base.Strings;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy extends
      EncodeTagIntoNameRunNodesAndAddToSetStrategy {

   private final SecureRandom random;

   @Inject
   protected EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy(
         AddNodeWithTagStrategy addNodeWithTagStrategy,
         ListNodesStrategy listNodesStrategy,
         @Named("NAMING_CONVENTION") String nodeNamingConvention,
         ComputeUtils utils,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
         SecureRandom random) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention,
            utils, executor);
      this.random = random;
   }

   /**
    * Get a name corresponding to the tag-hex*5 where the first 3 hex correspond
    * to the template id and the last a random number
    * 
    */
   @Override
   protected String getNextName(final String tag, final Template template) {
      return String.format(nodeNamingConvention, tag, Strings.padStart(
            Integer.toHexString(Integer.parseInt(template.getImage()
                  .getProviderId())), 3, '0'), Strings.padStart(Integer
            .toHexString(random.nextInt(255)), 2, '0'));
   }
}