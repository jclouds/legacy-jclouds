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
package org.jclouds.openstack.nova.v1_1.features;

import org.jclouds.openstack.nova.v1_1.domain.SecurityGroup;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupClient;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientLiveTest;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of {@code SecurityGroupClient}
 *
 * @author Michael Arnold
 */
@Test(groups = "live", testName = "SecurityGroupClientLiveTest")
public class SecurityGroupClientLiveTest extends BaseNovaClientLiveTest {

    public static final String SECURITY_GROUP_NAME = "testsg";

    public void listSecurityGroups() throws Exception {
        for (String regionId : context.getApi().getConfiguredRegions()) {
            SecurityGroupClient client = context.getApi().getSecurityGroupClientForRegion(regionId);
            Set<SecurityGroup> securityGroupsList = client.listSecurityGroups();
            assertNotNull(securityGroupsList);
        }
    }
    
    public void createGetAndDeleteSecurityGroup() throws Exception {
        for(String regionId : context.getApi().getConfiguredRegions()) {
            SecurityGroupClient client = context.getApi().getSecurityGroupClientForRegion(regionId);
            SecurityGroup securityGroup = null;
            String id;
            try {
                securityGroup = client.createSecurityGroup(SECURITY_GROUP_NAME, "test security group");
                assertNotNull(securityGroup);
                id = securityGroup.getId();
                SecurityGroup theGroup = client.getSecurityGroup(id);
                assertNotNull(theGroup);
            } finally {
                if (securityGroup != null) {
                    client.deleteSecurityGroup(securityGroup.getId());
                }
            }
        }
    }

    public void createAndDeleteSecurityGroupRule() throws Exception {
        for(String regionId : context.getApi().getConfiguredRegions()) {
            SecurityGroupClient client = context.getApi().getSecurityGroupClientForRegion(regionId);
            SecurityGroup securityGroup = null;

            try {
                securityGroup = client.createSecurityGroup(SECURITY_GROUP_NAME, "test security group");
                assertNotNull(securityGroup);

                SecurityGroupRule rule =  client.createSecurityGroupRule(
                        "tcp", "443", "443", "0.0.0.0/0", "", securityGroup.getId());
                assertNotNull(rule);

            } finally {
                if (securityGroup != null) {
                    client.deleteSecurityGroup(securityGroup.getId());
                }
            }
        }

    }
}
