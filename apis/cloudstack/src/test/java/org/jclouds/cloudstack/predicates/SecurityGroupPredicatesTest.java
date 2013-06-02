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
package org.jclouds.cloudstack.predicates;

import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.portInRange;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.hasCidr;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.portInRangeForCidr;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.nameEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit")
public class SecurityGroupPredicatesTest {

   public SecurityGroup group() {
      return SecurityGroup
         .builder()
         .id("13")
         .name("default")
         .description("description")
         .account("adrian")
         .domainId("1")
         .domain("ROOT")
         .ingressRules(
                       ImmutableSet.of(
                                       
                                       IngressRule.builder().id("5").protocol("tcp").startPort(22).endPort(22)
                                       .securityGroupName("adriancole").account("adrian").build(),
                                       IngressRule.builder().id("6").protocol("udp").startPort(11).endPort(11).CIDR("1.1.1.1/24").build(),
                                       IngressRule.builder().id("7").protocol("tcp").startPort(40).endPort(50).CIDR("1.1.1.1/24").build(),
                                       IngressRule.builder().id("8").protocol("tcp").startPort(60).endPort(60).CIDR("2.2.2.2/16").build()
                                       )).build();
   }

   @Test
   public void testPortInRange() {
      assertTrue(portInRange(22).apply(group()));
      assertTrue(portInRange(45).apply(group()));
      assertFalse(portInRange(100).apply(group()));
   }

   @Test
   public void testHasCidr() {
      assertTrue(hasCidr("1.1.1.1/24").apply(group()));
      assertFalse(hasCidr("3.3.3.3/25").apply(group()));
   }

   @Test
   public void testPortInRangeForCidr() {
      assertTrue(portInRangeForCidr(11, "1.1.1.1/24").apply(group()));
      assertTrue(portInRangeForCidr(45, "1.1.1.1/24").apply(group()));
      assertFalse(portInRangeForCidr(45, "2.2.2.2/16").apply(group()));
      assertFalse(portInRangeForCidr(11, "2.2.2.2/16").apply(group()));
      assertFalse(portInRangeForCidr(11, "3.3.3.3/25").apply(group()));
   }
   
   @Test
   public void testNameEquals() {
      assertTrue(nameEquals("default").apply(group()));
      assertFalse(nameEquals("not-default").apply(group()));
   }
   
}
