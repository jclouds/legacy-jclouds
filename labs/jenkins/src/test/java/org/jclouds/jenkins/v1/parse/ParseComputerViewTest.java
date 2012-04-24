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
package org.jclouds.jenkins.v1.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.jenkins.v1.domain.Computer;
import org.jclouds.jenkins.v1.domain.ComputerView;
import org.jclouds.json.BaseItemParserTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseComputerViewTest")
public class ParseComputerViewTest extends BaseItemParserTest<ComputerView> {

   @Override
   public String resource() {
      return "/computerview.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ComputerView expected() {
      return ComputerView.builder()
                        .displayName("nodes")
                        .totalExecutors(4)
                        .busyExecutors(0)
                        .computers(ImmutableSet.<Computer>builder()
                                 .add(Computer.builder()
                                          .displayName("master")
                                          .idle(true)
                                          .offline(false).build())
                                 .add(Computer.builder()
                                              .displayName("Ruboto")
                                              .idle(true)
                                              .offline(false).build())
                                 .add(Computer.builder()
                                              .displayName("winserver2008-x86")
                                              .idle(true)
                                              .offline(false).build()).build()).build();
   }
}
