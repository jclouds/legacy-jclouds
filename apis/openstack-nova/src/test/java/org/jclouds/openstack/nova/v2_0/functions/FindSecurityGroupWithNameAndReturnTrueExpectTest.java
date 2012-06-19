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
package org.jclouds.openstack.nova.v2_0.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseSecurityGroupListTest;
import org.jclouds.openstack.nova.v2_0.predicates.FindSecurityGroupWithNameAndReturnTrue;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "FindSecurityGroupWithNameAndReturnTrueExpectTest")
public class FindSecurityGroupWithNameAndReturnTrueExpectTest extends BaseNovaClientExpectTest {

   public void testUpdateReferenceWhenSecurityGroupListContainsGroupName() throws Exception {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_list.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listSecurityGroups,
               listSecurityGroupsResponse);

      FindSecurityGroupWithNameAndReturnTrue predicate = new FindSecurityGroupWithNameAndReturnTrue(
               clientWhenSecurityGroupsExist);

      AtomicReference<ZoneAndName> securityGroupInZoneRef = new AtomicReference<ZoneAndName>(ZoneAndName
               .fromZoneAndName("az-1.region-a.geo-1", "name1"));

      // we can find it
      assertTrue(predicate.apply(securityGroupInZoneRef));

      // the reference is now up to date, and includes the actual group found.
      assertEquals(securityGroupInZoneRef.get().toString(), new SecurityGroupInZone(Iterables
               .getOnlyElement(new ParseSecurityGroupListTest().expected()), "az-1.region-a.geo-1").toString());

   }

   public void testDoesNotUpdateReferenceWhenSecurityGroupListMissingGroupName() throws Exception {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_list.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listSecurityGroups,
               listSecurityGroupsResponse);

      FindSecurityGroupWithNameAndReturnTrue predicate = new FindSecurityGroupWithNameAndReturnTrue(
               clientWhenSecurityGroupsExist);

      ZoneAndName zoneAndGroup = ZoneAndName.fromZoneAndName("az-1.region-a.geo-1", "name2");

      AtomicReference<ZoneAndName> securityGroupInZoneRef = new AtomicReference<ZoneAndName>(zoneAndGroup);

      // we cannot find it
      assertFalse(predicate.apply(securityGroupInZoneRef));

      // the reference is the same
      assertEquals(securityGroupInZoneRef.get(), zoneAndGroup);

   }
}
