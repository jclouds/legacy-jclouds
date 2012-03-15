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
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaRestClientExpectTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseSecurityGroupListTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseSecurityGroupTest;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests annotation parsing of {@code SecurityGroupAsyncClient}
 *
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "SecurityGroupClientExpectTest")
public class SecurityGroupClientExpectTest extends BaseNovaRestClientExpectTest {
    public void testListSecurityGroupsWhenResponseIs2xx() throws Exception {
        HttpRequest listSecurityGroups = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-security-groups"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/securitygroup_list.json")).build();


        NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                listSecurityGroups, listSecurityGroupsResponse);

        assertEquals(clientWhenSecurityGroupsExist.getConfiguredRegions(),
                ImmutableSet.of("North"));

        assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupClientForRegion("North")
                .listSecurityGroups().toString(), new ParseSecurityGroupListTest().expected()
                .toString());
    }

    public void testListSecurityGroupsWhenReponseIs404IsEmpty() throws Exception {
        HttpRequest listListSecurityGroups = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-security-groups"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse listListSecurityGroupsResponse = HttpResponse.builder().statusCode(404)
                .build();

        NovaClient clientWhenNoSecurityGroupsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                listListSecurityGroups, listListSecurityGroupsResponse);

        assertTrue(clientWhenNoSecurityGroupsExist.getSecurityGroupClientForRegion("North")
                .listSecurityGroups().isEmpty());
    }

    public void testGetSecurityGroupWhenResponseIs2xx() throws Exception {

        HttpRequest getSecurityGroup = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-security-groups/0"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/securitygroup_details.json")).build();

        NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                getSecurityGroup, getSecurityGroupResponse);

        assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupClientForRegion("North")
                .getSecurityGroup("0").toString(),
                new ParseSecurityGroupTest().expected().toString());
    }

    public void testGetSecurityGroupWhenResponseIs404() throws Exception {
        HttpRequest getSecurityGroup = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-security-groups/0"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(404).build();

        NovaClient clientWhenNoSecurityGroupsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                getSecurityGroup, getSecurityGroupResponse);

        assertNull(clientWhenNoSecurityGroupsExist.getSecurityGroupClientForRegion("North").getSecurityGroup("0"));

    }
}
