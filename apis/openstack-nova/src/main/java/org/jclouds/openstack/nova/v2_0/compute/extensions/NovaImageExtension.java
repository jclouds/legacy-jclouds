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

package org.jclouds.openstack.nova.v2_0.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.Futures;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.Retryables;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Nova implementation of {@link ImageExtension}
 * 
 * @author David Alves
 *
 */
@Singleton
public class NovaImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final NovaClient novaClient;
   private final ExecutorService executor;
   @com.google.inject.Inject(optional = true)
   @Named("IMAGE_MAX_WAIT")
   private long maxWait = 3600;
   @com.google.inject.Inject(optional = true)
   @Named("IMAGE_WAIT_PERIOD")
   private long waitPeriod = 1;
   private PredicateWithResult<ZoneAndId, Image> imageReadyPredicate;

   @Inject
   public NovaImageExtension(NovaClient novaClient,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            PredicateWithResult<ZoneAndId, Image> imageReadyPredicate) {
      this.novaClient = checkNotNull(novaClient);
      this.executor = userThreads;
      this.imageReadyPredicate = imageReadyPredicate;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      Server server = novaClient.getServerClientForZone(zoneAndId.getZone()).getServer(zoneAndId.getId());
      if (server == null)
         throw new NoSuchElementException("Cannot find server with id: " + zoneAndId);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate,
               " openstack-nova only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      ZoneAndId sourceImageZoneAndId = ZoneAndId.fromSlashEncoded(cloneTemplate.getSourceNodeId());

      String newImageId = novaClient.getServerClientForZone(sourceImageZoneAndId.getZone()).createImageFromServer(
               cloneTemplate.getName(), sourceImageZoneAndId.getId());

      final ZoneAndId targetImageZoneAndId = ZoneAndId.fromZoneAndId(sourceImageZoneAndId.getZone(), newImageId);

      logger.info(">> Registered new Image %s, waiting for it to become available.", newImageId);

      return Futures.makeListenable(executor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            return Retryables.retryGettingResultOrFailing(imageReadyPredicate, targetImageZoneAndId, maxWait,
                     waitPeriod, TimeUnit.SECONDS, "Image was not created within the time limit, Giving up! [Limit: "
                              + maxWait + " secs.]");
         }
      }), executor);
   }

   @Override
   public boolean deleteImage(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      try {
         this.novaClient.getImageClientForZone(zoneAndId.getZone()).deleteImage(zoneAndId.getId());
      } catch (Exception e) {
         return false;
      }
      return true;
   }

}
