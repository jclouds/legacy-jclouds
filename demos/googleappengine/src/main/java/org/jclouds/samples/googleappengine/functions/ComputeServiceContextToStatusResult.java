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

package org.jclouds.samples.googleappengine.functions;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.StatusResult;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ComputeServiceContextToStatusResult implements Function<ComputeServiceContext, StatusResult> {

   @Resource
   protected Logger logger = Logger.NULL;

   public StatusResult apply(ComputeServiceContext in) {
      String host = in.getProviderSpecificContext().getEndpoint().getHost();
      String status;
      String name = "not found";
      try {
         long start = System.currentTimeMillis();

         name = String.format("%d nodes", in.getComputeService().listNodes().size());

         status = ((System.currentTimeMillis() - start) + "ms");
      } catch (Exception e) {
         logger.error(e, "Error listing context %s", in);
         status = (e.getMessage());
      }
      return new StatusResult(in.getProviderSpecificContext().getId(), host, name, status);
   }
}