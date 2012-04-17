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
package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindLaunchSpecificationToFormParams implements Binder, Function<LaunchSpecification, Map<String, String>> {

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof LaunchSpecification, "this binder is only valid for LaunchSpecifications!");
      LaunchSpecification launchSpec = LaunchSpecification.class.cast(input);
      return ModifyRequest.putFormParams(request, Multimaps.forMap(apply(launchSpec)));
   }

   @Override
   public Map<String, String> apply(LaunchSpecification launchSpec) {
      Builder<String, String> builder = ImmutableMap.builder();
      builder.put("LaunchSpecification.ImageId", checkNotNull(launchSpec.getImageId(), "imageId"));
      if (launchSpec.getAvailabilityZone() != null)
         builder.put("LaunchSpecification.Placement.AvailabilityZone", launchSpec.getAvailabilityZone());

      AWSRunInstancesOptions options = new AWSRunInstancesOptions();
      if (launchSpec.getBlockDeviceMappings().size() > 0)
         options.withBlockDeviceMappings(launchSpec.getBlockDeviceMappings());
      if (launchSpec.getSecurityGroupNames().size() > 0)
         options.withSecurityGroups(launchSpec.getSecurityGroupNames());
      if (launchSpec.getSecurityGroupIds().size() > 0)
         options.withSecurityGroupIds(launchSpec.getSecurityGroupIds());
      options.asType(checkNotNull(launchSpec.getInstanceType(), "instanceType"));
      if (launchSpec.getKernelId() != null)
         options.withKernelId(launchSpec.getKernelId());
      if (launchSpec.getKeyName() != null)
         options.withKeyName(launchSpec.getKeyName());
      if (launchSpec.getRamdiskId() != null)
         options.withRamdisk(launchSpec.getRamdiskId());
      if (Boolean.TRUE.equals(launchSpec.isMonitoringEnabled()))
         options.enableMonitoring();
      if (launchSpec.getUserData() != null)
         options.withUserData(launchSpec.getUserData());

      for (Entry<String, String> entry : options.buildFormParameters().entries()) {
         builder.put("LaunchSpecification." + entry.getKey(), entry.getValue());
      }
      return builder.build();
   }
}
