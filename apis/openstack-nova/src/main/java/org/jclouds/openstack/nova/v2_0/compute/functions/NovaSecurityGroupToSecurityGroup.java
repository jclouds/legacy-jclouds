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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.collect.Iterables.transform;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;

import com.google.common.base.Function;
import com.google.inject.Inject;


/**
 * A function for transforming a Nova-specific SecurityGroup into a generic
 * SecurityGroup object.
 * 
 * @author Andrew Bayer
 */
@Singleton
public class NovaSecurityGroupToSecurityGroup implements Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected Function<SecurityGroupRule,IpPermission> ruleToPermission;
   
   @Inject
   public NovaSecurityGroupToSecurityGroup(Function<SecurityGroupRule,IpPermission> ruleToPermission) {
      this.ruleToPermission = ruleToPermission;
   }

   @Override
   public SecurityGroup apply(org.jclouds.openstack.nova.v2_0.domain.SecurityGroup group) {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();
      
      builder.id(group.getId());
      builder.providerId(group.getId());
      builder.ownerId(group.getTenantId());
      builder.name(group.getName());
      if (group.getRules() != null) {
         builder.ipPermissions(transform(group.getRules(), ruleToPermission));
      }

      return builder.build();
   }
}
