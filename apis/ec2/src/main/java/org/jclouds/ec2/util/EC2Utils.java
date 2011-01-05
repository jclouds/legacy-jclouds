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

package org.jclouds.ec2.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.jclouds.aws.util.AWSUtils;
import org.jclouds.ec2.domain.AvailabilityZone;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Utils {
   public static String[] getAvailabilityZonesForRegion(String region) {
      Set<String> availabilityZones = new HashSet<String>();
      for (String az : AvailabilityZone.zones) {
         if (az.startsWith(region))
            availabilityZones.add(az);
      }

      return (String[]) availabilityZones.toArray(new String[availabilityZones.size()]);
   }

   public static Iterable<RunningInstance> getAllRunningInstancesInRegion(InstanceClient client, String region,
         String id) {
      return Iterables.concat(client.describeInstancesInRegion(region, id));
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

   private static final Pattern ELB_PATTERN = Pattern.compile("([^.]+)-[^.]+\\.([^.]+)\\.elb\\.amazonaws\\.com");

   public static Map<String, String> getLoadBalancerNameAndRegionFromDnsName(String dnsName) {
      Matcher matcher = ELB_PATTERN.matcher(checkNotNull(dnsName, "dnsName"));
      checkArgument(matcher.find(), "dnsName syntax is " + ELB_PATTERN + " didn't match: " + dnsName);
      String loadBalancerName = matcher.group(1);
      String regionName = matcher.group(2);
      checkArgument((AWSUtils.isRegion(regionName)),
            String.format("Region (%s)  parsed from (%s) is not a valid region", regionName, dnsName));
      return ImmutableMap.<String, String> of(regionName, loadBalancerName);
   }
}
