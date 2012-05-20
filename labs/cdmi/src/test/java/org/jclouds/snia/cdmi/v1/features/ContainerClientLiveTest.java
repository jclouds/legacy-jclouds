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
package org.jclouds.snia.cdmi.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIClientLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Kenneth Nagin
 */
@Test(groups = "live", testName = "ContainerClientLiveTest")
public class ContainerClientLiveTest extends BaseCDMIClientLiveTest {

   @Test
   public void testGetContainer() throws Exception {
//	  String pContainerName = "NaginContainer1335695552671"; 
	  String pContainerName = System.getProperty("test.cdmi.containerName","myContainer"); 
      ContainerClient client = cdmiContext.getApi().getContainerClient();
      Container container = client.getContainer(pContainerName);
      assertNotNull(container);
      assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
      assertNotNull(container.getObjectID());
      assertNotNull(container.getObjectName());
      assertEquals(container.getObjectName(),pContainerName+"/");
      assertNotNull(container.getChildren());
      assertNotNull(container.getMetadata());
   }

}
