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

import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Beta
@Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
public interface AWSSecurityGroupClient extends SecurityGroupClient {
   
   String createSecurityGroupInRegionAndReturnId(@Nullable String region, String name, String desc,
         CreateSecurityGroupOptions... options);

   void authorizeSecurityGroupIngressInRegion(@Nullable String region, String groupId, IpPermission perm);

   void authorizeSecurityGroupIngressInRegion(@Nullable String region, String groupId, Iterable<IpPermission> perm);

   void revokeSecurityGroupIngressInRegion(@Nullable String region, String groupId, IpPermission perm);

   void revokeSecurityGroupIngressInRegion(@Nullable String region, String groupId, Iterable<IpPermission> perm);

   Set<SecurityGroup> describeSecurityGroupsInRegionById(@Nullable String region, String... securityGroupIds);

   void deleteSecurityGroupInRegionById(@Nullable String region, String name);
}
