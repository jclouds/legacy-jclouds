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

package org.jclouds.compute.util;

import static com.google.common.collect.Maps.newLinkedHashMap;

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
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;

import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ComputeUtils {
   private final CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
   private final ExecutorService executor;

   @Inject
   public ComputeUtils(
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory = customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
      this.executor = executor;
   }

   public Map<?, Future<Void>> customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(TemplateOptions options,
            Iterable<NodeMetadata> runningNodes, Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
            Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      Map<NodeMetadata, Future<Void>> responses = newLinkedHashMap();
      for (NodeMetadata node : runningNodes) {
         responses.put(node, executor.submit(customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory.create(
                  options, node, goodNodes, badNodes, customizationResponses)));
      }
      return responses;
   }

}
