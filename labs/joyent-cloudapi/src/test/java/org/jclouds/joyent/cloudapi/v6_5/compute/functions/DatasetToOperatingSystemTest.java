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
package org.jclouds.joyent.cloudapi.v6_5.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.DatasetToOperatingSystem;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;
import org.jclouds.joyent.cloudapi.v6_5.parse.ParseDatasetTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests for the function for transforming a cloudApi specific Image into a generic
 * OperatingSystem object.
 * 
 * @author Adrian Cole
 */
@Test(testName = "DatasetToOperatingSystemTest")
public class DatasetToOperatingSystemTest {

   public void testCentos6() {

      Dataset datasetToConvert = new ParseDatasetTest().expected();

      OperatingSystem convertedOs = new DatasetToOperatingSystem(ImmutableMap.<OsFamily, Map<String, String>> of(
            OsFamily.CENTOS, ImmutableMap.of("6", "6.0"))).apply(datasetToConvert);

      assertEquals(convertedOs.getName(), datasetToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.CENTOS);
      assertEquals(convertedOs.getDescription(), datasetToConvert.getUrn());
      assertEquals(convertedOs.getVersion(), "6.0");
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }

}
