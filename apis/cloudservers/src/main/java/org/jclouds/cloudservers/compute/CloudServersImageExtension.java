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

package org.jclouds.cloudservers.compute;

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
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.compute.ImageExtension;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.Futures;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.Retryables;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * CloudServers implementation of {@link ImageExtension}
 * 
 * @author David Alves
 * 
 */
@Singleton
public class CloudServersImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudServersClient syncClient;
   private final ExecutorService executor;
   private final Function<org.jclouds.cloudservers.domain.Image, Image> cloudserversImageToImage;
   @com.google.inject.Inject(optional = true)
   @Named("IMAGE_MAX_WAIT")
   long maxWait = 3600;
   @com.google.inject.Inject(optional = true)
   @Named("IMAGE_WAIT_PERIOD")
   long waitPeriod = 1;

   @Inject
   public CloudServersImageExtension(CloudServersClient novaClient,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            Function<org.jclouds.cloudservers.domain.Image, Image> cloudserversImageToImage) {
      this.syncClient = checkNotNull(novaClient);
      this.executor = userThreads;
      this.cloudserversImageToImage = cloudserversImageToImage;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      Server server = syncClient.getServer(Integer.parseInt(id));
      if (server == null)
         throw new NoSuchElementException("Cannot find server with id: " + id);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate,
               " openstack-nova only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      final org.jclouds.cloudservers.domain.Image image = syncClient.createImageFromServer(cloneTemplate.getName(),
               Integer.parseInt(cloneTemplate.getSourceNodeId()));
      return Futures.makeListenable(executor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            return Retryables.retryGettingResultOrFailing(new PredicateWithResult<Integer, Image>() {

               org.jclouds.cloudservers.domain.Image result;
               RuntimeException lastFailure;

               @Override
               public boolean apply(Integer input) {
                  result = checkNotNull(findImage(input));
                  switch (result.getStatus()) {
                     case ACTIVE:
                        logger.info("<< Image %s is available for use.", input);
                        return true;
                     case UNKNOWN:
                     case SAVING:
                        logger.debug("<< Image %s is not available yet.", input);
                        return false;
                     default:
                        lastFailure = new IllegalStateException("Image was not created: " + input);
                        throw lastFailure;
                  }
               }

               @Override
               public Image getResult() {
                  return cloudserversImageToImage.apply(image);
               }

               @Override
               public Throwable getLastFailure() {
                  return lastFailure;
               }
            }, image.getId(), maxWait, waitPeriod, TimeUnit.SECONDS,
                     "Image was not created within the time limit, Giving up! [Limit: " + maxWait + " secs.]");
         }
      }), executor);

   }

   @Override
   public boolean deleteImage(String id) {
      try {
         this.syncClient.deleteImage(Integer.parseInt(id));
      } catch (Exception e) {
         return false;
      }
      return true;
   }

   private org.jclouds.cloudservers.domain.Image findImage(final int id) {
      return Iterables.tryFind(syncClient.listImages(ListOptions.NONE),
               new Predicate<org.jclouds.cloudservers.domain.Image>() {
                  @Override
                  public boolean apply(org.jclouds.cloudservers.domain.Image input) {
                     return input.getId() == id;
                  }
               }).orNull();

   }

}
