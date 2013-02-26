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
package org.jclouds.snia.cdmi.v1.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.domain.JsonBall;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Kenneth Nagin
 */
@Test(groups = "unit", testName = "ParseContainerTest")
public class ParseContainerTest extends BaseItemParserTest<Container> {

   @Override
   public String resource() {
      return "/container.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Container expected() {
      return Container.builder().objectType("application/cdmi-container").objectID("00007E7F00102E230ED82694DAA975D2")
               .objectName("MyContainer").parentURI("/").parentID("00007E7F0010128E42D87EE34F5A6560").domainURI("/cdmi_domains/MyDomain/").capabilitiesURI("/cdmi_capabilities/container/").completionStatus("Complete")
               .metadata(ImmutableMap.<String, JsonBall> builder().put("cdmi_size", new JsonBall("\"83\"")).build())
               .children(ImmutableSet.<String> builder().add("MyDataObject.txt").build()).childrenrange("0-1").build();
   }
}
