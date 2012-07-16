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
package org.jclouds.rds.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rds.domain.Subnet;
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.xml.SubnetGroupHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetSubnetGroupResponseTest")
public class GetSubnetGroupResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_subnetgroup.xml");

      SubnetGroup expected = expected();

      SubnetGroupHandler handler = injector.getInstance(SubnetGroupHandler.class);
      SubnetGroup result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
  }

   public SubnetGroup expected() {
      return SubnetGroup.builder()
                        .vpcId("990524496922")
                        .status("Complete")
                        .description("description")
                        .name("subnet_grp1")
                        .subnets(ImmutableSet.<Subnet>builder()
                           .add(Subnet.builder()
                                      .status("Active")
                                      .id("subnet-7c5b4115")
                                      .availabilityZone("us-east-1c").build())
                           .add(Subnet.builder()
                                      .status("Active")
                                      .id("subnet-7b5b4112")
                                      .availabilityZone("us-east-1b").build())
                           .add(Subnet.builder()
                                      .status("Active")
                                      .id("subnet-3ea6bd57")
                                      .availabilityZone("us-east-1d").build()).build()).build();
   }
}
