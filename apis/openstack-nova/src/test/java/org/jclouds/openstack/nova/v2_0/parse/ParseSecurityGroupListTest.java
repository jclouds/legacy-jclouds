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
package org.jclouds.openstack.nova.v2_0.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "ParseSecurityGroupListTest")
public class ParseSecurityGroupListTest extends BaseSetParserTest<SecurityGroup> {

   @Override
   public String resource() {
      return "/securitygroup_list.json";
   }

   @Override
   @SelectJson("security_groups")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<SecurityGroup> expected() {

      Set<SecurityGroupRule> securityGroupRules = ImmutableSet.<SecurityGroupRule> of(
            SecurityGroupRule.builder().fromPort(22)
                  .ipProtocol(IpProtocol.TCP).toPort(22).parentGroupId("3").ipRange("0.0.0.0/0")
                  .id("107").build(),
            SecurityGroupRule.builder().fromPort(7600)
                  .ipProtocol(IpProtocol.TCP).toPort(7600).parentGroupId("3").ipRange("0.0.0.0/0")
                  .id("118").build(),
            SecurityGroupRule.builder().fromPort(8084)
                  .ipProtocol(IpProtocol.TCP).toPort(8084).parentGroupId("3").ipRange("0.0.0.0/0")
                  .id("119").build());

      return ImmutableSet.of(SecurityGroup.builder().description("description1").id("1").tenantId("tenant1")
            .rules(securityGroupRules).name("name1").build());
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
