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
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.cloudstack.domain.IngressRule;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;


/**
 * A function for transforming a CloudStack-specific SecurityGroup into a generic
 * SecurityGroup object.
 * 
 * @author Andrew Bayer
 */
@Singleton
public class CloudStackSecurityGroupToSecurityGroup implements Function<org.jclouds.cloudstack.domain.SecurityGroup, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Function<IngressRule,IpPermission> ruleToPermission;
   
   @Inject
   public CloudStackSecurityGroupToSecurityGroup(Function<IngressRule,IpPermission> ruleToPermission) {
      this.ruleToPermission = ruleToPermission;
   }

   @Override
   public SecurityGroup apply(org.jclouds.cloudstack.domain.SecurityGroup group) {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();
      
      builder.id(group.getId());
      builder.providerId(group.getId());
      builder.name(group.getName());
      builder.ownerId(group.getAccount());
      builder.ipPermissions(transform(group.getIngressRules(), ruleToPermission));
      
      return builder.build();
   }
}
