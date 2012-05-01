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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides monitoring services for EC2. For more information, refer to the Amazon CloudWatch
 * Developer Guide.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface MonitoringClient {

   /**
    * Enables monitoring for a running instance. For more information, refer to the Amazon
    * CloudWatch Developer Guide.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @see InstanceClient#runInstances
    * @see #unmonitorInstances
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-MonitorInstances.html"
    *      />
    */
   Map<String, MonitoringState> monitorInstancesInRegion(@Nullable String region, String instanceId,
            String... instanceIds);

   /**
    * Disables monitoring for a running instance. For more information, refer to the Amazon
    * CloudWatch Developer Guide.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * 
    * @see InstanceClient#runInstances
    * @see #monitorInstances
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-UnmonitorInstances.html"
    *      />
    */
   Map<String, MonitoringState> unmonitorInstancesInRegion(@Nullable String region, String instanceId,
            String... instanceIds);
}
