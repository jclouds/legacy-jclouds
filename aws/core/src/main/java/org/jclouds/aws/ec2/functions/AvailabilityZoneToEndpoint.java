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
package org.jclouds.aws.ec2.functions;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.AvailabilityZone;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AvailabilityZoneToEndpoint implements Function<Object, URI> {
   private final Map<AvailabilityZone, Region> availabilityZoneToRegion;
   private final Map<Region, URI> regionToEndpoint;

   @Inject
   public AvailabilityZoneToEndpoint(@EC2 Map<Region, URI> regionToEndpoint,
            Map<AvailabilityZone, Region> availabilityZoneToRegion) {
      this.regionToEndpoint = regionToEndpoint;
      this.availabilityZoneToRegion = availabilityZoneToRegion;
   }

   public URI apply(Object from) {
      return regionToEndpoint.get(availabilityZoneToRegion.get(from));
   }

}