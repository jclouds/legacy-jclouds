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

package org.jclouds.ec2.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.Futures;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.CreateImageOptions;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.Retryables;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * EC2 implementation of {@link ImageExtension} please note that {@link #createImage(ImageTemplate)}
 * only works by cloning EBS backed instances for the moment.
 * 
 * @author David Alves
 * 
 */
public class EC2ImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final EC2Client ec2Client;
   private final ExecutorService executor;
   private final PredicateWithResult<String, Image> imageReadyPredicate;
   @com.google.inject.Inject(optional = true)
   @Named("IMAGE_MAX_WAIT")
   private long maxWait = 3600;
   @com.google.inject.Inject(optional = true)
   @Named("IMAGE_WAIT_PERIOD")
   private long waitPeriod = 1;

   @Inject
   public EC2ImageExtension(EC2Client ec2Client, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            PredicateWithResult<String, Image> imageReadyPredicate) {
      this.ec2Client = checkNotNull(ec2Client);
      this.executor = checkNotNull(userThreads);
      this.imageReadyPredicate = imageReadyPredicate;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      Reservation<? extends RunningInstance> instance = Iterables.getOnlyElement(ec2Client.getInstanceServices()
               .describeInstancesInRegion(region, instanceId));
      if (instance == null)
         throw new NoSuchElementException("Cannot find server with id: " + id);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, " ec2 only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      String[] parts = AWSUtils.parseHandle(cloneTemplate.getSourceNodeId());
      final String region = parts[0];
      String instanceId = parts[1];

      final String imageId = ec2Client.getAMIServices().createImageInRegion(region, cloneTemplate.getName(),
               instanceId, CreateImageOptions.NONE);

      return Futures.makeListenable(executor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            return Retryables.retryGettingResultOrFailing(imageReadyPredicate, region + "/" + imageId, maxWait,
                     waitPeriod, TimeUnit.SECONDS, "Image was not created within the time limit, Giving up! [Limit: "
                              + maxWait + " secs.]");
         }
      }), executor);
   }

   @Override
   public boolean deleteImage(String id) {
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      try {
         ec2Client.getAMIServices().deregisterImageInRegion(region, instanceId);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

}
