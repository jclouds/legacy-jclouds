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
package org.jclouds.compute.util;

import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;

import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ComputeUtils {
   private final CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
   private final ListeningExecutorService userExecutor;

   @Inject
   public ComputeUtils(
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory = customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
      this.userExecutor = userExecutor;
   }

   public Map<?, ListenableFuture<Void>> customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(TemplateOptions options,
            Iterable<NodeMetadata> runningNodes, Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
            Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      Map<NodeMetadata, ListenableFuture<Void>> responses = newLinkedHashMap();
      for (NodeMetadata node : runningNodes) {
         responses.put(node, userExecutor.submit(customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory.create(
                  options, new AtomicReference<NodeMetadata>(node), goodNodes, badNodes, customizationResponses)));
      }
      return responses;
   }

}
