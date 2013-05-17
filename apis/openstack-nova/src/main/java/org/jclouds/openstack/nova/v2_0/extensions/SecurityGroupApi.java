/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Security Groups.
 * <p/>
 * 
 * @see SecurityGroupAsyncApi
 * @author Jeremy Daggett
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SECURITY_GROUPS)
public interface SecurityGroupApi {

   /**
    * List all Security Groups.
    * 
    * @return all Security Groups
    */
   FluentIterable<? extends SecurityGroup> list();

   /**
    * Get a specific Security Group
    * 
    * @return a specific Security Group
    */
   SecurityGroup get(String id);

   /**
    * Create a Security Group
    * 
    * @return a new Security Group
    */
   SecurityGroup createWithDescription(String name, String description);

   /**
    * Delete a Security Group.
    * 
    * @return
    */
   boolean delete(String id);

   /**
    * Create a Security Group Rule.
    * 
    * @return a new Security Group Rule
    */
   SecurityGroupRule createRuleAllowingCidrBlock(String parentGroup, Ingress ingress, String sourceCidr);

   /**
    * Create a Security Group Rule.
    * 
    * @return a new Security Group Rule
    */
   SecurityGroupRule createRuleAllowingSecurityGroupId(String parentGroup, Ingress ingress,
            String sourceCidr);

   /**
    * Delete a Security Group Rule.
    * 
    * @return
    */
   Boolean deleteRule(String id);

}
