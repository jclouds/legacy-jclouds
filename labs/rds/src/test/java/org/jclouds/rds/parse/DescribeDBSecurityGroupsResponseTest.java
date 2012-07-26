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
package org.jclouds.rds.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rds.domain.Authorization.Status;
import org.jclouds.rds.domain.EC2SecurityGroup;
import org.jclouds.rds.domain.IPRange;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.xml.DescribeDBSecurityGroupsResultHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeDBSecurityGroupsResponseTest")
public class DescribeDBSecurityGroupsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_securitygroups.xml");

      IterableWithMarker<SecurityGroup> expected = expected();

      DescribeDBSecurityGroupsResultHandler handler = injector.getInstance(DescribeDBSecurityGroupsResultHandler.class);
      IterableWithMarker<SecurityGroup> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public IterableWithMarker<SecurityGroup> expected() {
      return IterableWithMarkers.from(ImmutableSet.<SecurityGroup>builder()
               .add(SecurityGroup.builder()
                                 .ec2SecurityGroup(EC2SecurityGroup.builder()
                                                                   .rawStatus("authorized")
                                                                   .status(Status.AUTHORIZED)
                                                                   .name("myec2securitygroup")
                                                                   .ownerId("054794666394").build())
                                 .description("default")
                                 .ipRange(IPRange.builder()
                                                 .cidrIp("127.0.0.1/30")
                                                 .rawStatus("authorized")
                                                 .status(Status.AUTHORIZED).build())
                                 .ownerId("621567473609")
                                 .name("default")
                                 .vpcId("vpc-1ab2c3d4").build())
               .add(SecurityGroup.builder()
                                 .description("My new DBSecurityGroup")
                                 .ipRange(IPRange.builder()
                                                 .cidrIp("192.168.1.1/24")
                                                 .rawStatus("authorized")
                                                 .status(Status.AUTHORIZED).build())
                                 .ownerId("621567473609")
                                 .name("mydbsecuritygroup")
                                 .vpcId("vpc-1ab2c3d5").build())
               .add(SecurityGroup.builder()
                                 .description("My new DBSecurityGroup")
                                 .ownerId("621567473609")
                                 .name("mydbsecuritygroup4")
                                 .vpcId("vpc-1ab2c3d6").build()).build());
   }
}
