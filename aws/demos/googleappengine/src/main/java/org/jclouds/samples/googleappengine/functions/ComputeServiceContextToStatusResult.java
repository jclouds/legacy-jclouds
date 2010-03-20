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
package org.jclouds.samples.googleappengine.functions;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.StatusResult;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

@Singleton
public class ComputeServiceContextToStatusResult implements Function<String, StatusResult> {

   @Inject
   private Map<String, ComputeServiceContext> contexts;

   @Resource
   protected Logger logger = Logger.NULL;

   public StatusResult apply(final String contextName) {
      final ComputeServiceContext context = contexts.get(contextName);
      final String host = context.getProviderSpecificContext().getEndPoint().getHost();
      String status;
      String name = "not found";
      try {
         long start = System.currentTimeMillis();
         Map<String, ? extends ComputeMetadata> nodes = context.getComputeService().getNodes();
         if (nodes.size() > 0)
            name = Iterables.get(nodes.keySet(), 0);
         status = ((System.currentTimeMillis() - start) + "ms");
      } catch (Exception e) {
         logger.error(e, "Error listing service %s", contextName);
         status = (e.getMessage());
      }
      return new StatusResult(contextName, host, name, status);
   }
}