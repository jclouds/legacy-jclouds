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

package org.jclouds.aws.ec2.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Utils {
   @Singleton
   public static class GetRegionFromLocation implements Function<Location, String> {
      public String apply(Location location) {
         String region = location.getScope() == LocationScope.REGION ? location.getId() : location.getParent().getId();
         return region;
      }
   }

   public static String[] getAvailabilityZonesForRegion(String region) {
      Set<String> availabilityZones = new HashSet<String>();
      for (String az : AvailabilityZone.zones) {
         if (az.startsWith(region))
            availabilityZones.add(az);
      }

      return (String[]) availabilityZones.toArray(new String[availabilityZones.size()]);
   }

   public static <R extends HttpRequest> R indexStringArrayToFormValuesWithPrefix(R request, String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[], "this binder is only valid for String[] : "
            + input.getClass());
      String[] values = (String[]) input;
      Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
      for (int i = 0; i < values.length; i++) {
         builder.put(prefix + "." + (i + 1), checkNotNull(values[i], prefix.toLowerCase() + "s[" + i + "]"));
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : ModifyRequest.putFormParams(request, forms);
   }

   public static Iterable<RunningInstance> getAllRunningInstancesInRegion(InstanceClient client, String region,
         String id) {
      return Iterables.concat(client.describeInstancesInRegion(region, id));
   }

   public static String[] parseHandle(String id) {
      String[] parts = checkNotNull(id, "id").split("/");
      checkArgument(parts.length == 2, "id syntax is region/instanceid");
      return parts;
   }

   public static <R extends HttpRequest> R indexIterableToFormValuesWithPrefix(R request, String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Iterable<?>, "this binder is only valid for Iterable<?>: "
            + input.getClass());
      Iterable<?> values = (Iterable<?>) input;
      Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
      int i = 0;
      for (Object o : values) {
         builder.put(prefix + "." + (i++ + 1), checkNotNull(o.toString(), prefix.toLowerCase() + "s[" + i + "]"));
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : ModifyRequest.putFormParams(request, forms);
   }

   public static String findRegionInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof String) {
            String regionName = (String) arg;
            if (isRegion(regionName))
               return regionName;
         }
      }
      return null;
   }

   public static boolean isRegion(String regionName) {
      return Region.EU_WEST_1.equals(regionName) || Region.US_WEST_1.equals(regionName)
            || Region.US_EAST_1.equals(regionName) || Region.US_STANDARD.equals(regionName)
            || Region.AP_SOUTHEAST_1.equals(regionName);
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

   public static <R extends HttpRequest> R indexStringArrayToFormValuesWithStringFormat(R request, String format,
         Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[], "this binder is only valid for String[] : "
            + input.getClass());
      String[] values = (String[]) input;
      Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
      for (int i = 0; i < values.length; i++) {
         builder.put(String.format(format, (i + 1)), checkNotNull(values[i], format.toLowerCase() + "s[" + i + "]"));
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : ModifyRequest.putFormParams(request, forms);
   }

   private static final Pattern ELB_PATTERN = Pattern.compile("([^.]+)-[^.]+\\.([^.]+)\\.elb\\.amazonaws\\.com");

   public static Map<String, String> getLoadBalancerNameAndRegionFromDnsName(String dnsName) {
      Matcher matcher = ELB_PATTERN.matcher(checkNotNull(dnsName, "dnsName"));
      checkArgument(matcher.find(), "dnsName syntax is " + ELB_PATTERN + " didn't match: " + dnsName);
      String loadBalancerName = matcher.group(1);
      String regionName = matcher.group(2);
      checkArgument((isRegion(regionName)),
            String.format("Region (%s)  parsed from (%s) is not a valid region", regionName, dnsName));
      return ImmutableMap.<String, String> of(regionName, loadBalancerName);
   }
}