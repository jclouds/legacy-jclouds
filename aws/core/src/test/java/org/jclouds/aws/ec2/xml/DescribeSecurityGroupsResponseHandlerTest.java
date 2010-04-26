/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.ec2.domain.IpPermission;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code DescribeSecurityGroupsHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeSecurityGroupsHandlerTest")
public class DescribeSecurityGroupsResponseHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_securitygroups.xml");

      SortedSet<SecurityGroup> expected = ImmutableSortedSet.of(new SecurityGroup(defaultRegion,
               "WebServers", "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", "Web Servers", ImmutableSortedSet
                        .of(new IpPermission(80, 80, ImmutableSortedSet.<UserIdGroupPair> of(),
                                 IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0")))),
               new SecurityGroup(defaultRegion, "RangedPortsBySource", "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM",
                        "Group A", ImmutableSortedSet.of(new IpPermission(6000, 7000,
                                 ImmutableSortedSet.<UserIdGroupPair> of(), IpProtocol.TCP,
                                 ImmutableSortedSet.<String> of()))));

      DescribeSecurityGroupsResponseHandler handler = injector
               .getInstance(DescribeSecurityGroupsResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<SecurityGroup> result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { null }).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
