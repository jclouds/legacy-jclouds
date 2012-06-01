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
package org.jclouds.openstack.quantum.v1_0.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Port;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseNetworkDetailsTest")
public class ParseNetworkDetailsTest extends BaseItemParserTest<NetworkDetails> {

   @Override
   public String resource() {
      return "/network_details.json";
   }

   @Override
   @SelectJson("network")
   @Consumes(MediaType.APPLICATION_JSON)
   public NetworkDetails expected() {
      return NetworkDetails.builder().name("jclouds-port-test").id("25e3e0f8-f1f0-4850-97a3-8d5393c3385b")
            .ports(ImmutableSet.of(Port.builder().state(Port.State.DOWN).id("908391f6-ef3c-4bc6-acec-46582f9b231d").build())).build();
   }
}
