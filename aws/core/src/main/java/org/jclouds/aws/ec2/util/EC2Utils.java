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
package org.jclouds.aws.ec2.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.inject.internal.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Utils {
   public static Image newImage(EC2Client client, Region region, OperatingSystem os,
            Architecture architecture, String ami) {
      org.jclouds.aws.ec2.domain.Image image = Iterables.getOnlyElement(client.getAMIServices()
               .describeImagesInRegion(region, imageIds(ami)));
      return new ImageImpl(ami, image.getDescription(), os, null, region.toString(), architecture);
   }

   public static void indexStringArrayToFormValuesWithPrefix(GeneratedHttpRequest<?> request,
            String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[],
               "this binder is only valid for String[] : " + input.getClass());
      String[] values = (String[]) input;
      for (int i = 0; i < values.length; i++) {
         request.addFormParam(prefix + "." + (i + 1), checkNotNull(values[i], prefix.toLowerCase()
                  + "s[" + i + "]"));
      }
   }

   public static void indexIterableToFormValuesWithPrefix(GeneratedHttpRequest<?> request,
            String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Iterable<?>,
               "this binder is only valid for Iterable<?>: " + input.getClass());
      Iterable<?> values = (Iterable<?>) input;
      int i = 0;
      for (Object o : values) {
         request.addFormParam(prefix + "." + (i++ + 1), checkNotNull(o.toString(), prefix
                  .toLowerCase()
                  + "s[" + i + "]"));
      }
   }

   public static Region findRegionInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof Region) {
            return (Region) arg;
         }
      }
      return null;
   }

   public static AvailabilityZone findAvailabilityZoneInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof AvailabilityZone) {
            return (AvailabilityZone) arg;
         }
      }
      return null;
   }
}