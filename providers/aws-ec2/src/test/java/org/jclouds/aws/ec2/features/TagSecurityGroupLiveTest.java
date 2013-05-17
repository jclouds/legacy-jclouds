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
package org.jclouds.aws.ec2.features;

import static org.jclouds.ec2.domain.Tag.ResourceType.SECURITY_GROUP;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.services.AWSSecurityGroupClient;
import org.jclouds.ec2.features.internal.BaseTagApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class TagSecurityGroupLiveTest extends BaseTagApiLiveTest {
   public TagSecurityGroupLiveTest() {
      provider = "aws-ec2";
   }

   @Override
   protected Resource createResourceForTagging(String prefix) {
      try {
         return new Resource(securityGroupApi().createSecurityGroupInRegionAndReturnId(null, prefix, prefix),
               SECURITY_GROUP);
      } catch (IllegalStateException e) {
         return new Resource(Iterables.get(securityGroupApi().describeSecurityGroupsInRegion(null, prefix), 0).getId(),
               SECURITY_GROUP);
      }
   }

   @Override
   protected void cleanupResource(Resource resource) {
      securityGroupApi().deleteSecurityGroupInRegionById(null, resource.id);
   }

   private AWSSecurityGroupClient securityGroupApi() {
      return AWSEC2Client.class.cast(api).getSecurityGroupServices();
   }
}
