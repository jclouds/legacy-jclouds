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
import org.jclouds.openstack.quantum.v1_0.domain.Attachment;
import org.jclouds.openstack.quantum.v1_0.domain.Port;
import org.jclouds.openstack.quantum.v1_0.domain.PortDetails;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParsePortDetailsTest")
public class ParsePortDetailsTest extends BaseItemParserTest<PortDetails> {

   @Override
   public String resource() {
      return "/port_details.json";
   }

   @Override
   @SelectJson("port")
   @Consumes(MediaType.APPLICATION_JSON)
   public PortDetails expected() {
      return PortDetails.builder().id("0ccbe514-e36b-475b-91c9-208dfd96d3ac").state(Port.State.DOWN)
            .attachment(Attachment.builder().id("jclouds-live-test").build()).build();
   }
}
