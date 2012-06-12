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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

/**
 * Provides synchronous access to Security Groups.
 * <p/>
 * 
 * @see SecurityGroupAsyncClient
 * @author Jeremy Daggett
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SECURITY_GROUPS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface SecurityGroupClient {

   /**
    * List all Security Groups.
    * 
    * @return all Floating IPs
    */
   Set<SecurityGroup> listSecurityGroups();

   /**
    * Get a specific Security Group
    * 
    * @return a specific Security Group
    */
   SecurityGroup getSecurityGroup(String id);

   /**
    * Create a Security Group
    * 
    * @return a new Security Group
    */
   SecurityGroup createSecurityGroupWithNameAndDescription(String name, String description);

   /**
    * Delete a Security Group.
    * 
    * @return
    */
   Boolean deleteSecurityGroup(String id);

   /**
    * Create a Security Group Rule.
    * 
    * @return a new Security Group Rule
    */
   SecurityGroupRule createSecurityGroupRuleAllowingCidrBlock(String parentGroup, Ingress ingress, String sourceCidr);

   /**
    * Create a Security Group Rule.
    * 
    * @return a new Security Group Rule
    */
   SecurityGroupRule createSecurityGroupRuleAllowingSecurityGroupId(String parentGroup, Ingress ingress,
            String sourceCidr);

   /**
    * Delete a Security Group Rule.
    * 
    * @return
    */
   Boolean deleteSecurityGroupRule(String id);

}
