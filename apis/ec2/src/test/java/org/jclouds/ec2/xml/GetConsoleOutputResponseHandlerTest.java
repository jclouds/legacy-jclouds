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
package org.jclouds.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GetConsoleOutputResponseHandler}
 * 
 * @author Andrew Kennedy
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetConsoleOutputResponseHandlerTest")
public class GetConsoleOutputResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream(
               "/get_console_output_response.xml");

      String result = factory.create(
               injector.getInstance(GetConsoleOutputResponseHandler.class)).parse(is);

      String expected = "Linux version 2.6.16-xenU (builder@patchbat.amazonsa) (gcc version 4.0.1 20050727 (Red Hat 4.0.1-5)) #1 SMP Thu Oct 26 08:41:26 SAST 2006\n" + 
              "BIOS-provided physical RAM map:\n" + 
              "Xen: 0000000000000000 - 000000006a400000 (usable)\n" + 
              "980MB HIGHMEM available.\n" + 
              "727MB LOWMEM available.\n" + 
              "NX (Execute Disable) protection: active\n" + 
              "IRQ lockup detection disabled\n" + 
              "Built 1 zonelists\n" + 
              "Kernel command line: root=/dev/sda1 ro 4\n" + 
              "Enabling fast FPU save and restore... done.\n";

      assertEquals(result, expected);
   }
}
