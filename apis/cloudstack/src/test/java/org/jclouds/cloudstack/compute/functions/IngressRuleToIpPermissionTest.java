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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;


/**
 * Tests for the function for transforming a cloudstack specific IngressRule into a generic
 * IpPermission object.
 * 
 * @author Andrew Bayer
 */
public class IngressRuleToIpPermissionTest {

   @Test
   public void testApplyWithTCP() {
      IngressRule ruleToConvert = IngressRule.builder()
         .id("some-id")
         .account("some-account")
         .securityGroupName("some-group-name")
         .protocol(IpProtocol.TCP.toString())
         .startPort(10)
         .endPort(20)
         .CIDR("0.0.0.0/0")
         .build();

      IngressRuleToIpPermission converter = new IngressRuleToIpPermission();

      IpPermission convertedPerm = converter.apply(ruleToConvert);

      assertEquals(convertedPerm.getIpProtocol(), IpProtocol.fromValue(ruleToConvert.getProtocol()));
      assertEquals(convertedPerm.getFromPort(), ruleToConvert.getStartPort());
      assertEquals(convertedPerm.getToPort(), ruleToConvert.getEndPort());
      assertEquals(convertedPerm.getCidrBlocks(), ImmutableSet.of("0.0.0.0/0"));
      assertTrue(convertedPerm.getTenantIdGroupNamePairs().size() == 0);
      assertTrue(convertedPerm.getGroupIds().size() == 0);
   }
}
