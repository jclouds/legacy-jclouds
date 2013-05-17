/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.trmk.vcloud_0_8.domain.ComputePoolReference;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkAdapter;
import org.jclouds.trmk.vcloud_0_8.domain.Subnet;
import org.jclouds.trmk.vcloud_0_8.domain.VAppExtendedInfo;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VAppExtendedInfoHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VAppExtendedInfoHandlerTest")
public class VAppExtendedInfoHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/vapp-ext.xml");

      VAppExtendedInfo result = factory.create(injector.getInstance(VAppExtendedInfoHandler.class)).parse(is);
      assertEquals(
               result,
               VAppExtendedInfo
                        .builder()
                        .id("392992")
                        .href(
                                 URI
                                          .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/vapp/392992"))
                        .name("instance01")
                        .networkAdapter(
                                 NetworkAdapter
                                          .builder()
                                          .macAddress("00:50:56:95:12:96")
                                          .name("Network adapter 1")
                                          .subnet(
                                                   Subnet
                                                            .builder()
                                                            .href(
                                                                     URI
                                                                              .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/network/43781"))
                                                            .name("10.122.213.192/27").build()).build())
                        .computePoolReference(
                                 ComputePoolReference
                                          .builder()
                                          .href(
                                                   URI
                                                            .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/computePool/692"))
                                          .name(

                                          "Resource Pool 692").build()).build());

   }
}
