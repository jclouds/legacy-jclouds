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

package org.jclouds.vcloud.bluelock.compute.config.suppliers;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.compute.domain.VCloudExpressImage;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseSizeFromImageSupplier implements Supplier<Set<? extends Size>> {
   // ex Ubuntu904Serverx64 1CPUx16GBx20GB
   public static final Pattern GBRAM_PATTERN = Pattern.compile("[^ ] ([0-9]+)CPUx([0-9]+)GBx([0-9]+)GB");

   // ex Windows2008stdx64 1CPUx512MBx30GB
   public static final Pattern MBRAM_PATTERN = Pattern.compile("[^ ] ([0-9]+)CPUx([0-9]+)MBx([0-9]+)GB");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final Supplier<Set<? extends Image>> images;

   @Inject
   ParseSizeFromImageSupplier(Supplier<Set<? extends Image>> images) {
      this.images = images;
   }

   @Override
   public Set<? extends Size> get() {

      return newLinkedHashSet(filter(transform(images.get(), new Function<Image, Size>() {

         @Override
         public Size apply(Image from) {
            try {
               VCloudExpressVAppTemplate template = VCloudExpressImage.class.cast(from).getVAppTemplate();
               Matcher matcher = getMatcherAndFind(template.getName());
               double cores = Double.parseDouble(matcher.group(1));
               int ram = Integer.parseInt(matcher.group(2));
               if (matcher.pattern().equals(GBRAM_PATTERN))
                  ram *= 1024;
               int disk = Integer.parseInt(matcher.group(3));
               String name = template.getName().split(" ")[1];
               return new SizeImpl(from.getId(), name, from.getId(), from.getLocation(), null, ImmutableMap
                        .<String, String> of(), cores, ram, disk, ImagePredicates.idEquals(from.getId()));
            } catch (NoSuchElementException e) {
               logger.debug("<< didn't match at all(%s)", from);
               return null;
            }
         }
      }), Predicates.notNull()));
   }

   /**
    * 
    * @throws NoSuchElementException
    *            if no configured matcher matches the name.
    */
   private Matcher getMatcherAndFind(String name) {
      for (Pattern pattern : new Pattern[] { GBRAM_PATTERN, MBRAM_PATTERN }) {
         Matcher matcher = pattern.matcher(name);
         if (matcher.find())
            return matcher;
      }
      throw new NoSuchElementException(name);
   }
}