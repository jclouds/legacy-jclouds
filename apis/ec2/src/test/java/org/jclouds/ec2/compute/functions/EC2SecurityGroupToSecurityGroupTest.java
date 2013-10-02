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

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.util.IpPermissions;
import org.jclouds.net.domain.IpPermission;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "EC2SecurityGroupToSecurityGroupTest")
public class EC2SecurityGroupToSecurityGroupTest {

   static Location provider = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1")
         .description("us-east-1").build();

   @Test
   public void testApply() {
      IpPermissions authorization = IpPermissions.permitAnyProtocol();

      org.jclouds.ec2.domain.SecurityGroup origGroup = org.jclouds.ec2.domain.SecurityGroup.builder()
         .region("us-east-1")
         .id("some-id")
         .name("some-group")
         .ownerId("some-owner")
         .description("some-description")
         .ipPermission(authorization)
         .build();

      EC2SecurityGroupToSecurityGroup parser = createGroupParser(ImmutableSet.of(provider));

      SecurityGroup group = parser.apply(origGroup);
      
      assertEquals(group.getLocation(), provider);
      assertEquals(group.getId(), provider.getId() + "/" + origGroup.getName());
      assertEquals(group.getProviderId(), origGroup.getId());
      assertEquals(group.getName(), origGroup.getName());
      assertEquals(group.getIpPermissions(), (Set<IpPermission>)origGroup);
      assertEquals(group.getOwnerId(), origGroup.getOwnerId());
   }

   private EC2SecurityGroupToSecurityGroup createGroupParser(final ImmutableSet<Location> locations) {
      Supplier<Set<? extends Location>> locationSupplier = new Supplier<Set<? extends Location>>() {

         @Override
         public Set<? extends Location> get() {
            return locations;
         }

      };

      EC2SecurityGroupToSecurityGroup parser = new EC2SecurityGroupToSecurityGroup(locationSupplier);

      return parser;
   }

}
