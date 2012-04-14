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

package org.jclouds.openstack.nova.v1_1.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ImageExtension;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ImageInZone;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndId;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Singleton
public class NovaImageExtension implements ImageExtension {

   private final NovaClient novaClient;
   private final Function<ImageInZone, Image> imageInZoneToImage;

   @Inject
   public NovaImageExtension(NovaClient novaClient, Function<ImageInZone, Image> imageInZoneToImage) {
      this.novaClient = checkNotNull(novaClient);
      this.imageInZoneToImage = imageInZoneToImage;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      org.jclouds.openstack.nova.v1_1.domain.Image original = findImage(zoneAndId);
      if (original == null)
         throw new NoSuchElementException();
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public Image createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate,
               " openstack-nova only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(cloneTemplate.getSourceNodeId());
      String newImageId = novaClient.getServerClientForZone(zoneAndId.getZone()).createImageFromServer(
               cloneTemplate.getName(), cloneTemplate.getSourceNodeId());
      org.jclouds.openstack.nova.v1_1.domain.Image newImage = checkNotNull(findImage(ZoneAndId.fromZoneAndId(
               zoneAndId.getZone(), newImageId)));
      return imageInZoneToImage.apply(new ImageInZone(newImage, zoneAndId.getZone()));
   }

   @Override
   public boolean deleteImage(String id) {
      // TODO
      return false;
   }

   private org.jclouds.openstack.nova.v1_1.domain.Image findImage(final ZoneAndId zoneAndId) {
      return Iterables.tryFind(novaClient.getImageClientForZone(zoneAndId.getZone()).listImagesInDetail(),
               new Predicate<org.jclouds.openstack.nova.v1_1.domain.Image>() {
                  @Override
                  public boolean apply(org.jclouds.openstack.nova.v1_1.domain.Image input) {
                     return input.getId().equals(zoneAndId.getId());
                  }
               }).orNull();

   }

}
