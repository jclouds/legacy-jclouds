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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.ec2.EC2Api;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Andrew Bayer
 */
@Singleton
public class EC2SecurityGroupIdFromName implements Function<String, String> {
   protected EC2Api api;

   @Inject
   public EC2SecurityGroupIdFromName(EC2Api api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   public String apply(String input) {
      checkNotNull(input, "input");
      String[] parts = AWSUtils.parseHandle(input);
      String region = parts[0];
      String name = parts[1];

      return  Iterables.getOnlyElement(api.getSecurityGroupApi().get().describeSecurityGroupsInRegion(region, name), null).getId();
   }
}
