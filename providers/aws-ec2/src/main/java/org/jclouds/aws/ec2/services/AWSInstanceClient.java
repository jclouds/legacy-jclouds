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
package org.jclouds.aws.ec2.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
public interface AWSInstanceClient extends InstanceClient {

   @Override
   Set<? extends Reservation<? extends AWSRunningInstance>> describeInstancesInRegion(@Nullable String region,
            String... instanceIds);

   @Override
   Reservation<? extends AWSRunningInstance> runInstancesInRegion(@Nullable String region,
            @Nullable String nullableAvailabilityZone, String imageId, int minCount, int maxCount,
            RunInstancesOptions... options);

}
