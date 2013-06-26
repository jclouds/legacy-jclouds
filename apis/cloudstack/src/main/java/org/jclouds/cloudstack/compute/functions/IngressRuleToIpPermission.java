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

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;


/**
 * A function for transforming a CloudStack-specific IngressRule into a generic
 * IpPermission object.
 * 
 * @author Andrew Bayer
 */
public class IngressRuleToIpPermission implements Function<IngressRule, IpPermission> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   public IngressRuleToIpPermission() {
   }

   @Override
   public IpPermission apply(IngressRule rule) {
      IpPermission.Builder builder = IpPermission.builder();
      builder.ipProtocol(IpProtocol.fromValue(rule.getProtocol()));
      builder.fromPort(rule.getStartPort());
      builder.toPort(rule.getEndPort());
      builder.cidrBlock(rule.getCIDR());
      
      return builder.build();
   }
}