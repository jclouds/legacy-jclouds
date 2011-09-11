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
package org.jclouds.ibm.smartcloud.parse;

import java.util.Date;
import java.util.Set;

import org.jclouds.http.HttpUtils;
import org.jclouds.ibm.smartcloud.config.IBMSmartCloudParserModule;
import org.jclouds.ibm.smartcloud.domain.Image;
import org.jclouds.ibm.smartcloud.domain.InstanceType;
import org.jclouds.ibm.smartcloud.domain.Price;
import org.jclouds.ibm.smartcloud.domain.Image.Visibility;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.Unwrap;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "ListImagesTest")
public class ListImagesTest extends BaseSetParserTest<Image> {

   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new IBMSmartCloudParserModule());
   }

   @Override
   public String resource() {
      return "/images.json";
   }

   @Override
   @Unwrap
   public Set<Image> expected() {
      return ImmutableSet
               .of(new Image(
                        "SUSE Linux Enterprise Server 11 for x86",
                        HttpUtils
                                 .createUri("https://www-147.ibm.com/cloud/enterprise/ram.ws/RAMSecure/artifact/{F006D027-02CC-9D08-D389-6C729D939D44}/1.0/parameters.xml"),
                        Image.State.AVAILABLE,
                        Visibility.PUBLIC,
                        "SYSTEM",
                        "SUSE Linux Enterprise Server/11",
                        Image.Architecture.X86_64,
                        new Date(1216944000000l),
                        "41",
                        ImmutableSet.<InstanceType> of(new InstanceType("Bronze 32 bit", new Price(0.17, "UHR  ",
                                 "897", null, "USD", 1), "BRZ32.1/2048/175"), new InstanceType("Gold 32 bit",
                                 new Price(0.41, "UHR  ", "897", null, "USD", 1), "GLD32.4/4096/350"),
                                 new InstanceType("Silver 32 bit", new Price(0.265, "UHR  ", "897", null, "USD", 1),
                                          "SLV32.2/4096/350")),
                        ImmutableSet.<String> of("ifeE7VOzRG6SGvoDlRPTQw"),
                        HttpUtils
                                 .createUri("https://www-147.ibm.com/cloud/enterprise/ram.ws/RAMSecure/artifact/{F006D027-02CC-9D08-D389-6C729D939D44}/1.0/GettingStarted.html"),
                        "20001150", "SUSE Linux Enterprise Server 11 for x86 Base OS 32-bit with pay for use licensing"));
   }
}
