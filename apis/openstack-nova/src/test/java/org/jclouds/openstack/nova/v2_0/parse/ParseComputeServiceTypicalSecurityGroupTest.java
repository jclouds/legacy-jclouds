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

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseSecurityGroupTest")
public class ParseComputeServiceTypicalSecurityGroupTest extends BaseItemParserTest<SecurityGroup> {

   @Override
   public String resource() {
      return "/securitygroup_details_computeservice_typical.json";
   }

   @Override
   @SelectJson("security_group")
   @Consumes(MediaType.APPLICATION_JSON)
   public SecurityGroup expected() {

      Set<SecurityGroupRule> securityGroupRules = ImmutableSet.of(
            SecurityGroupRule.builder().fromPort(22)
                  .ipProtocol(IpProtocol.TCP).toPort(22).parentGroupId("2769")
                  .ipRange("0.0.0.0/0").id("10331").build(),
            SecurityGroupRule.builder().fromPort(22).group(TenantIdAndName.builder().tenantId("37936628937291").name("jclouds_mygroup").build())
                  .ipProtocol(IpProtocol.TCP).toPort(22).parentGroupId("2769")
                  .id("10332").build(),
            SecurityGroupRule.builder().fromPort(8080)
                  .ipProtocol(IpProtocol.TCP).toPort(8080).parentGroupId("2769")
                  .ipRange("0.0.0.0/0").id("10333").build(),
            SecurityGroupRule.builder().fromPort(8080).group(TenantIdAndName.builder().tenantId("37936628937291").name("jclouds_mygroup").build())
                  .ipProtocol(IpProtocol.TCP).toPort(8080).parentGroupId("2769")
                  .id("10334").build()                  
      );

      return SecurityGroup.builder().description("jclouds_mygroup").id("2769").tenantId("37936628937291").rules(securityGroupRules)
            .name("jclouds_mygroup").build();
   }
   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
