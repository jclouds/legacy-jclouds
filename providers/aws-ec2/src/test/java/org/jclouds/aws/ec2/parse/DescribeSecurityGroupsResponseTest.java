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
package org.jclouds.aws.ec2.parse;

import static com.google.common.base.Throwables.propagate;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;

/**
 *
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "DescribeSecurityGroupsResponseTest")
public class DescribeSecurityGroupsResponseTest extends BaseEC2HandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_security_groups_vpc.xml");

      Set<SecurityGroup> expected = expected();

      DescribeSecurityGroupsResponseHandler handler = injector.getInstance(DescribeSecurityGroupsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<SecurityGroup> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public Set<SecurityGroup> expected() {
      return ImmutableSet.of(SecurityGroup.builder()
                                          .region(defaultRegion)
                                          .ownerId("123123123123")
                                          .id("sg-11111111")
                                          .name("default")
                                          .description("default VPC security group")
//                                          .vpcId("vpc-99999999")
                                          .ipPermission(IpPermission.builder()
                                                                    .ipProtocol(IpProtocol.ALL)
                                                                    .userIdGroupPair("123123123123","sg-11111111").build())
//                                          .ipPermissionEgress(IpPermission.builder()
//                                                                    .ipProtocol(IpProtocol.ALL)
//                                                                    .ipRange("0.0.0.0/0").build())
                                          .build());

   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      handler.setContext(request);
   }

}
