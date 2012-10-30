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
package org.jclouds.cloudservers.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateWithResult;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * 
 * @author David Alves
 * 
 */
public final class GetImageWhenStatusActivePredicateWithResult implements PredicateWithResult<Integer, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudServersClient client;
   private final Function<org.jclouds.cloudservers.domain.Image, Image> cloudserversImageToImage;
   private org.jclouds.cloudservers.domain.Image result;
   private RuntimeException lastFailure;

   @Inject
   public GetImageWhenStatusActivePredicateWithResult(CloudServersClient client,
            Function<org.jclouds.cloudservers.domain.Image, Image> cloudserversImageToImage) {
      this.client = client;
      this.cloudserversImageToImage = cloudserversImageToImage;
   }

   @Override
   public boolean apply(Integer input) {
      result = checkNotNull(findImage(input));
      switch (result.getStatus()) {
         case ACTIVE:
            logger.info("<< Image %s is available for use. %s", input, result);
            return true;
         case QUEUED:
         case PREPARING:
         case SAVING:
            logger.debug("<< Image %s is not available yet. %s", input, result);
            return false;
         default:
            lastFailure = new IllegalStateException("Image " + input + " was not created. " + result);
            throw lastFailure;
      }
   }

   @Override
   public Image getResult() {
      return cloudserversImageToImage.apply(result);
   }

   @Override
   public Throwable getLastFailure() {
      return lastFailure;
   }

   private org.jclouds.cloudservers.domain.Image findImage(final int id) {
      return Iterables.tryFind(client.listImages(new ListOptions().withDetails()),
               new Predicate<org.jclouds.cloudservers.domain.Image>() {
                  @Override
                  public boolean apply(org.jclouds.cloudservers.domain.Image input) {
                     return input.getId() == id;
                  }
               }).orNull();

   }
}
