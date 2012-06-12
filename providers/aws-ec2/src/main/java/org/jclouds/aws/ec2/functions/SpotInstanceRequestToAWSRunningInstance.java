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
package org.jclouds.aws.ec2.functions;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.InstanceState;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class SpotInstanceRequestToAWSRunningInstance implements Function<SpotInstanceRequest, AWSRunningInstance> {

   @Override
   public AWSRunningInstance apply(SpotInstanceRequest request) {
      if (request == null)
         return null;
      if (request.getState() != SpotInstanceRequest.State.OPEN)
         return null;
      AWSRunningInstance.Builder builder = AWSRunningInstance.builder();
      builder.spotInstanceRequestId(request.getId());
      builder.instanceId(request.getId());
      builder.instanceState(InstanceState.PENDING);
      builder.rawState(request.getRawState());
      builder.region(request.getRegion());
      builder.tags(request.getTags());
      LaunchSpecification spec = request.getLaunchSpecification();
      builder.availabilityZone(spec.getAvailabilityZone());
      // TODO convert
      // builder.devices(spec.getBlockDeviceMappings());
      builder.groupIds(spec.getSecurityGroupNames());
      builder.imageId(spec.getImageId());
      builder.instanceType(spec.getInstanceType());
      builder.kernelId(spec.getKernelId());
      builder.keyName(spec.getKeyName());
      builder.ramdiskId(spec.getRamdiskId());
      builder.monitoringState(Boolean.TRUE.equals(spec.isMonitoringEnabled()) ? MonitoringState.PENDING
               : MonitoringState.DISABLED);
      //TODO: determine the exact hypervisor
      builder.hypervisor(Hypervisor.XEN);
      return builder.build();
   }

}
