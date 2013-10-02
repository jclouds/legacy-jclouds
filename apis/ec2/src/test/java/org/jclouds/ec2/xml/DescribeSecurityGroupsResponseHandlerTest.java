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
package org.jclouds.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code DescribeSecurityGroupsResponseHandler}
 *
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "DescribeSecurityGroupsResponseHandlerTest")
public class DescribeSecurityGroupsResponseHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/describe_securitygroups.xml");

      Set<SecurityGroup> expected = ImmutableSet.of(
            new SecurityGroup(defaultRegion, "sg-3c6ef654", "WebServers", "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", "Web Servers",
                  ImmutableSet.of(new IpPermission(IpProtocol.TCP, 80, 80, ImmutableMultimap.<String, String> of(),
                        ImmutableSet.<String> of(), ImmutableSet.of("0.0.0.0/0")))),
            new SecurityGroup(defaultRegion, "sg-867309ab", "RangedPortsBySource", "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", "Group A",
                  ImmutableSet.of(new IpPermission(IpProtocol.TCP, 6000, 7000, ImmutableMultimap
                        .<String, String> of(), ImmutableSet.<String> of(), ImmutableSet.<String> of()))));

      DescribeSecurityGroupsResponseHandler handler = injector.getInstance(DescribeSecurityGroupsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<SecurityGroup> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   // Response from OpenStack 1.1 EC2 API
   public void testApplyInputStreamWithEmptyFields() {

      InputStream is = getClass().getResourceAsStream("/describe_securitygroups_empty.xml");

      Multimap<String, String> userIdGroupPairs = LinkedHashMultimap.create();
      userIdGroupPairs.put("UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", "jclouds#cluster#world");

      Set<SecurityGroup> expected = ImmutableSet.of(
            new SecurityGroup(defaultRegion, "sg-3c6ef654", "jclouds#cluster#world", "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", "Cluster",
                  ImmutableSet.of(
                        new IpPermission(IpProtocol.TCP, 22, 22, ImmutableMultimap.<String, String> of(),
                              ImmutableSet.<String> of(), ImmutableSet.of("0.0.0.0/0")),
                        new IpPermission(IpProtocol.ALL, -1, -1, userIdGroupPairs,
                              ImmutableSet.<String> of(), ImmutableSet.<String> of()))));

      DescribeSecurityGroupsResponseHandler handler = injector.getInstance(DescribeSecurityGroupsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<SecurityGroup> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      handler.setContext(request);
   }
}
