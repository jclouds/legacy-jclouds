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

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.cloudstack.domain.IngressRule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "CloudStackSecurityGroupToSecurityGroupTest")
public class CloudStackSecurityGroupToSecurityGroupTest {

   private static final IngressRuleToIpPermission ruleConverter = new IngressRuleToIpPermission();
   
   @Test
   public void testApply() {
      IngressRule ruleToConvert = IngressRule.builder()
         .id("some-id")
         .account("some-account")
         .securityGroupName("some-group-name")
         .protocol(IpProtocol.TCP.toString())
         .startPort(10)
         .endPort(20)
         .CIDR("0.0.0.0/0")
         .build();

      org.jclouds.cloudstack.domain.SecurityGroup origGroup = org.jclouds.cloudstack.domain.SecurityGroup.builder()
         .id("some-id")
         .name("some-group")
         .description("some-description")
         .account("some-account")
         .ingressRules(ImmutableSet.of(ruleToConvert))
         .build();

      CloudStackSecurityGroupToSecurityGroup parser = createGroupParser();

      SecurityGroup group = parser.apply(origGroup);
      
      assertEquals(group.getId(), origGroup.getId());
      assertEquals(group.getProviderId(), origGroup.getId());
      assertEquals(group.getName(), origGroup.getName());
      assertEquals(group.getOwnerId(), origGroup.getAccount());
      assertEquals(group.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getIngressRules(), ruleConverter)));
   }

   private CloudStackSecurityGroupToSecurityGroup createGroupParser() {
      CloudStackSecurityGroupToSecurityGroup parser = new CloudStackSecurityGroupToSecurityGroup(ruleConverter);

      return parser;
   }

}
