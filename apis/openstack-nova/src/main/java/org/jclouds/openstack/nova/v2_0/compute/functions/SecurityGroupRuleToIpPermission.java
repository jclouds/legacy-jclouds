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

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;

import com.google.common.base.Function;


/**
 * A function for transforming a nova-specific SecurityGroupRule into a generic
 * IpPermission object.
 * 
 * @author Andrew Bayer
 */
public class SecurityGroupRuleToIpPermission implements Function<SecurityGroupRule, IpPermission> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   public SecurityGroupRuleToIpPermission() {
   }

   @Override
   public IpPermission apply(SecurityGroupRule rule) {
      IpPermission.Builder builder = IpPermission.builder();
      builder.ipProtocol(rule.getIpProtocol());
      builder.fromPort(rule.getFromPort());
      builder.toPort(rule.getToPort());
      if (rule.getGroup() != null) 
         builder.tenantIdGroupNamePair(rule.getGroup().getTenantId(), rule.getGroup().getName());
      if (rule.getIpRange() != null)
         builder.cidrBlock(rule.getIpRange());
      
      return builder.build();
   }
}