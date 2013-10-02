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
package org.jclouds.aws.ec2.compute.functions;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.functions.EC2SecurityGroupToSecurityGroup;

import com.google.common.base.Supplier;
import com.google.inject.Inject;


/**
 * A function for transforming an EC2-specific SecurityGroup into a generic
 * SecurityGroup object.
 * 
 * @author Andrew Bayer
 */
@Singleton
public class AWSEC2SecurityGroupToSecurityGroup extends EC2SecurityGroupToSecurityGroup {

   @Inject
   public AWSEC2SecurityGroupToSecurityGroup(@Memoized Supplier<Set<? extends Location>> locations) {
      super(locations);
   }

   @Override
   protected String idOrName(org.jclouds.ec2.domain.SecurityGroup group) {
      return group.getId();
   }

}
