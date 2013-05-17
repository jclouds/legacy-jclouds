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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListSecurityGroupsResponseTest extends BaseSetParserTest<SecurityGroup> {

   @Override
   public String resource() {
      return "/listsecuritygroupsresponse.json";
   }

   @Override
   @SelectJson("securitygroup")
   public Set<SecurityGroup> expected() {
      return ImmutableSet
            .<SecurityGroup> builder()
            .add(SecurityGroup
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

                              IngressRule.builder().id("6").protocol("udp").startPort(11).endPort(11).CIDR("1.1.1.1/24")
                                    .build())).build())
            .add(SecurityGroup.builder().id("12").name("adriancole").account("adrian").domainId("1").domain("ROOT").build())
            .add(SecurityGroup.builder().id("15").name("2").description("description").account("adrian").domainId("1")
                  .domain("ROOT").build())

            .add(SecurityGroup.builder().id("14").name("1").description("description").account("adrian").domainId("1")
                  .domain("ROOT").ingressRules(ImmutableSet.of(

                  IngressRule.builder().id("7").protocol("tcp").startPort(10).endPort(10).CIDR("1.1.1.1/24").build(),

                  IngressRule.builder().id("8").protocol("tcp").startPort(10).endPort(10).CIDR("2.2.2.2/16").build()))
                  .build())
            .add(SecurityGroup
                  .builder()
                  .id("16")
                  .name("with1and2")
                  .description("description")
                  .account("adrian")
                  .domainId("1")
                  .domain("ROOT")
                  .ingressRules(
                        ImmutableSet.of(IngressRule.builder().id("9").protocol("icmp").ICMPType(-1).ICMPCode(-1)
                              .securityGroupName("1").account("adrian").build(),

                        IngressRule.builder().id("10").protocol("tcp").startPort(22).endPort(22).securityGroupName("1")
                              .account("adrian").build(),

                        IngressRule.builder().id("11").protocol("tcp").startPort(22).endPort(22).securityGroupName("2")
                              .account("adrian").build())).build()).build();

   }

}
