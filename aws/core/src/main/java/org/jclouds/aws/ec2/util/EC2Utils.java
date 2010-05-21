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

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Utils {
   @Singleton
   public static class GetRegionFromLocation implements Function<Location, String> {
      public String apply(Location location) {
         String region = location.getScope() == LocationScope.REGION ? location.getId() : location
                  .getParent().getId();
         return region;
      }
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

   public static Iterable<RunningInstance> getAllRunningInstancesInRegion(InstanceClient client,
            String region, String id) {
      return Iterables.concat(client.describeInstancesInRegion(region, id));
   }

   public static String[] parseHandle(String id) {
      String[] parts = checkNotNull(id, "id").split("/");
      checkArgument(parts.length == 2, "id syntax is region/instanceid");
      return parts;
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

   public static String findRegionInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof String) {
            String regionName = (String) arg;
            if (Region.EU_WEST_1.equals(regionName) || Region.US_WEST_1.equals(regionName)
                     || Region.US_EAST_1.equals(regionName)
                     || Region.US_STANDARD.equals(regionName)
                     || Region.AP_SOUTHEAST_1.equals(regionName))
               return regionName;
         }
      }
      return null;
   }

   public static String findAvailabilityZoneInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof String) {
            String zone = (String) arg;
            if (AvailabilityZone.zones.contains(zone))
               return zone;
         }
      }
      return null;
   }

   public static void indexStringArrayToFormValuesWithStringFormat(GeneratedHttpRequest<?> request,
            String format, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[],
               "this binder is only valid for String[] : " + input.getClass());
      String[] values = (String[]) input;
      for (int i = 0; i < values.length; i++) {
         request.addFormParam(String.format(format, (i + 1)), checkNotNull(values[i], format
                  .toLowerCase()
                  + "s[" + i + "]"));
      }
   }
}