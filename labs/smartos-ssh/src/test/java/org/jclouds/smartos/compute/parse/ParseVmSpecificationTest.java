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
package org.jclouds.smartos.compute.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.smartos.compute.config.SmartOSParserModule;
import org.jclouds.smartos.compute.domain.DataSet;
import org.jclouds.smartos.compute.domain.VmNIC;
import org.jclouds.smartos.compute.domain.VmSpecification;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseVmSpecificationTest")
public class ParseVmSpecificationTest extends BaseItemParserTest<VmSpecification> {

   @Override
   public String resource() {
      return "/vmspec.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public VmSpecification expected() {
      return VmSpecification.builder()
                            .alias("small")
                            .brand("joyent")
                            .dataset(DataSet.builder()
                                            .uuid("56108678-1183-11e1-83c3-ff3185a5b47f")
                                            .os("linux")
                                            .published("2011-11-18")
                                            .urn("sdc:sdc:ubuntu10.04:0.1.0").build())
                            .nic(VmNIC.builder()
                                      .ip("192.168.1.4")
                                      .gateway("192.168.1.1")
                                      .netmask("255.255.255.0")
                                      .tag("eth0").build())
                            .dnsDomain("local")
                            .quota("0")
                            .maxPhysicalMemory(256)
                            .maxLockedMemory(256)
                            .maxSwap(256)
                            .tmpFs(256).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new SmartOSParserModule(), new GsonModule());
   }
}
