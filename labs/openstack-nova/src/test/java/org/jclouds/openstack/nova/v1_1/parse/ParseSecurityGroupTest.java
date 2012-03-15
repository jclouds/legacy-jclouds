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
package org.jclouds.openstack.nova.v1_1.parse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v1_1.config.NovaParserModule;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroup;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroupRule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "ParseSecurityGroupTest")
public class ParseSecurityGroupTest extends BaseItemParserTest<SecurityGroup> {

    @Override
    public String resource() {
        return "/securitygroup_details.json";
    }

    @Override
    @SelectJson("security_group")
    @Consumes(MediaType.APPLICATION_JSON)
    public SecurityGroup expected() {

        Set<SecurityGroupRule> securityGroupRules = ImmutableSet.<SecurityGroupRule> of(
                SecurityGroupRule
                        .builder()
                        .fromPort(22)
                        .group(new HashMap<String, String>())
                        .ipProtocol(SecurityGroupRule.IpProtocol.TCP)
                        .toPort(22)
                        .parentGroupId("28")
                        .ipRange(ImmutableMap.of("cidr", "10.2.6.0/24"))
                        .id("108")
                        .build(),
                SecurityGroupRule
                        .builder()
                        .fromPort(22)
                        .group(ImmutableMap.of("tenant_id","admin", "name", "11111"))
                        .ipProtocol(SecurityGroupRule.IpProtocol.TCP)
                        .toPort(22)
                        .parentGroupId("28")
                        .ipRange(new HashMap<String, String>())
                        .id("109")
                        .build()
        );

        return SecurityGroup
                .builder()
                .description("description0")
                .id("0")
                .tenantId("tenant0")
                .rules(securityGroupRules)
                .name("name0")
                .build();
    }

    protected Injector injector() {
        return Guice.createInjector(new NovaParserModule(), new GsonModule());
    }
}
