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

package org.jclouds.openstack.nova.v2_0.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ImageInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.predicates.PredicateWithResult;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author David Alves
 */
public final class GetImageWhenImageInZoneHasActiveStatusPredicateWithResult implements
         PredicateWithResult<ZoneAndId, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private org.jclouds.openstack.nova.v2_0.domain.Image result;
   private ZoneAndId resultZoneAndId;
   private RuntimeException lastFailure;
   private Function<ImageInZone, Image> imageInZoneToImage;
   private NovaClient client;

   @Inject
   public GetImageWhenImageInZoneHasActiveStatusPredicateWithResult(Function<ImageInZone, Image> imageInZoneToImage,
            NovaClient client) {
      this.imageInZoneToImage = imageInZoneToImage;
      this.client = client;
   }

   @Override
   public boolean apply(ZoneAndId input) {
      result = checkNotNull(findImage(ZoneAndId.fromZoneAndId(input.getZone(), input.getId())));
      resultZoneAndId = input;
      switch (result.getStatus()) {
         case ACTIVE:
            logger.info("<< Image %s is available for use. %s", input.getId(), result);
            return true;
         case UNRECOGNIZED:
         case SAVING:
            logger.debug("<< Image %s is not available yet. %s", input.getId(), result);
            return false;
         default:
            lastFailure = new IllegalStateException("Image " + input.getId() + " was not created. " + result);
            throw lastFailure;
      }
   }

   @Override
   public Image getResult() {
      return imageInZoneToImage.apply(new ImageInZone(result, resultZoneAndId.getZone()));
   }

   @Override
   public Throwable getLastFailure() {
      return lastFailure;
   }

   public org.jclouds.openstack.nova.v2_0.domain.Image findImage(final ZoneAndId zoneAndId) {
      return Iterables.tryFind(client.getImageClientForZone(zoneAndId.getZone()).listImagesInDetail(),
               new Predicate<org.jclouds.openstack.nova.v2_0.domain.Image>() {
                  @Override
                  public boolean apply(org.jclouds.openstack.nova.v2_0.domain.Image input) {
                     return input.getId().equals(zoneAndId.getId());
                  }
               }).orNull();

   }
}