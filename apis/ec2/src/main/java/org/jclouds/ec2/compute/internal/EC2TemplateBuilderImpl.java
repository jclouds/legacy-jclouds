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

package org.jclouds.ec2.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;

import com.google.common.base.Supplier;
import com.google.common.collect.ComputationException;

/**
 * 
 * @author Adrian Cole
 */
public class EC2TemplateBuilderImpl extends TemplateBuilderImpl {

   private final Map<RegionAndName, Image> imageMap;

   @Inject
   protected EC2TemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            Supplier<Location> defaultLocation, Provider<TemplateOptions> optionsProvider,
            @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider, Map<RegionAndName, Image> imageMap) {
      super(locations, images, sizes, defaultLocation, optionsProvider, defaultTemplateProvider);
      this.imageMap = imageMap;
   }

   @Override
   protected void copyTemplateOptions(TemplateOptions from, TemplateOptions to) {
      super.copyTemplateOptions(from, to);
      if (from instanceof EC2TemplateOptions) {
         EC2TemplateOptions eFrom = EC2TemplateOptions.class.cast(from);
         EC2TemplateOptions eTo = EC2TemplateOptions.class.cast(to);
         if (eFrom.getGroupIds().size() > 0)
            eTo.securityGroups(eFrom.getGroupIds());
         if (eFrom.getKeyPair() != null)
            eTo.keyPair(eFrom.getKeyPair());
         if (eFrom.getBlockDeviceMappings().size() > 0)
            eTo.blockDeviceMappings(eFrom.getBlockDeviceMappings());
         if (!eFrom.shouldAutomaticallyCreateKeyPair())
            eTo.noKeyPair();
         if (eFrom.getUserData() != null)
            eTo.userData(eFrom.getUserData());
      }
   }

   final Provider<Image> lazyImageProvider = new Provider<Image>() {

      @Override
      public Image get() {
         if (imageId != null) {
            String[] regionName = imageId.split("/");
            checkArgument(regionName.length == 2,
                     "amazon image ids must include the region ( ex. us-east-1/ami-7ea24a17 ) you specified: "
                              + imageId);
            RegionAndName key = new RegionAndName(regionName[0], regionName[1]);
            try {
               return imageMap.get(key);
            } catch (NullPointerException nex) {
               throw new NoSuchElementException(String.format("image %s/%s not found", key.getRegion(), key.getName()));
            } catch (ComputationException nex) {
               throw new NoSuchElementException(String.format("image %s/%s not found", key.getRegion(), key.getName()));
            }
         }
         return null;
      }

   };

   /**
    * @throws NoSuchElementException
    *            if the image is not found
    */
   @Override
   protected Image resolveImage(Hardware size, Iterable<? extends Image> supportedImages) {
      try {
         return super.resolveImage(size, supportedImages);
      } catch (NoSuchElementException e) {
         Image returnVal = lazyImageProvider.get();
         if (returnVal != null)
            return returnVal;
         throw e;
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Set<? extends Image> getImages() {
      Set<Image> images = (Set<Image>) this.images.get();
      if (images.size() == 0) {
         Image toReturn = lazyImageProvider.get();
         if (toReturn != null) {
            images.add(toReturn);
         }
      }
      return images;
   }

}
