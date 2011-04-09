/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.xml.ovf.ResourceAllocationHandler;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ResourceAllocationHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ResourceAllocationHandlerTest")
public class ResourceAllocationHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/resourceallocation-hosting.xml");

      ResourceAllocation result = factory.create(
               injector.getInstance(ResourceAllocationHandler.class)).parse(is);

      ResourceAllocation expects = new ResourceAllocation(1, "1 virtual CPU(s)",
               "Number of Virtual CPUs", ResourceType.PROCESSOR, null, null, null, null, null,
               null, 1, "hertz * 10^6");
      assertEquals(result, expects);

   }
}
