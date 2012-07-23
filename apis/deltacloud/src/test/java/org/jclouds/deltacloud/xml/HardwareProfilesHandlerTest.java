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
package org.jclouds.deltacloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.deltacloud.domain.EnumHardwareProperty;
import org.jclouds.deltacloud.domain.FixedHardwareProperty;
import org.jclouds.deltacloud.domain.HardwareParameter;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.HardwareProperty;
import org.jclouds.deltacloud.domain.RangeHardwareProperty;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code HardwareProfilesHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "HardwareProfilesHandlerTest")
public class HardwareProfilesHandlerTest extends BaseHandlerTest {

   @Test
   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_list_hardware_profiles.xml");
      Set<? extends HardwareProfile> expects = ImmutableSet.of(
            new HardwareProfile(URI.create("http://localhost:3001/api/hardware_profiles/m1-small"), "m1-small",
                  "m1-small", ImmutableSet.<HardwareProperty> of(
                        new FixedHardwareProperty("cpu", "count", Long.valueOf(1)), new FixedHardwareProperty("memory",
                              "MB", new Double(1740.8)), new FixedHardwareProperty("storage", "GB", Long.valueOf(160)),
                        new FixedHardwareProperty("architecture", "label", "i386"))),
            new HardwareProfile(URI.create("http://localhost:3001/api/hardware_profiles/m1-large"), "m1-large",
                  "m1-large", ImmutableSet.<HardwareProperty> of(
                        new FixedHardwareProperty("cpu", "count", Long.valueOf(2)),
                        new RangeHardwareProperty("memory", "MB", Long.valueOf(10240), new HardwareParameter(URI
                              .create("http://localhost:3001/api/instances"), "post", "hwp_memory", "create"),
                              new Double(7680.0), Long.valueOf(15360)), new EnumHardwareProperty("storage", "GB", Long.valueOf(
                              850), new HardwareParameter(URI.create("http://localhost:3001/api/instances"), "post",
                              "hwp_storage", "create"), ImmutableSet.<Object> of(Long.valueOf(850), Long.valueOf(1024))),
                        new FixedHardwareProperty("architecture", "label", "x86_64"))),
            new HardwareProfile(URI.create("http://localhost:3001/api/hardware_profiles/m1-xlarge"), "m1-xlarge",
                  "m1-xlarge", ImmutableSet.<HardwareProperty> of(
                        new FixedHardwareProperty("cpu", "count", Long.valueOf(4)),
                        new RangeHardwareProperty("memory", "MB", Long.valueOf(12288), new HardwareParameter(URI
                              .create("http://localhost:3001/api/instances"), "post", "hwp_memory", "create"),
                              Long.valueOf(12288), Long.valueOf(32768)),
                        new EnumHardwareProperty("storage", "GB", Long.valueOf(1024), new HardwareParameter(URI
                              .create("http://localhost:3001/api/instances"), "post", "hwp_storage", "create"),
                              ImmutableSet.<Object> of(Long.valueOf(1024), Long.valueOf(2048), Long.valueOf(4096))),
                        new FixedHardwareProperty("architecture", "label", "x86_64"))),
            new HardwareProfile(URI.create("http://localhost:3001/api/hardware_profiles/opaque"), "opaque", "opaque",
                  ImmutableSet.<HardwareProperty> of()));
      assertEquals(factory.create(injector.getInstance(HardwareProfilesHandler.class)).parse(is), expects);
   }
}
