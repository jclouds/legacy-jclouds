/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.compute;


import org.jclouds.compute.domain.Image;
import org.jclouds.virtualbox.functions.IMachineToImage;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import java.net.URI;

/**
 * Tests basic functionality of the VirtualBoxComputeServiceAdapter.
 * <p/>
 * Note that you need vboxwebsrv running for these tests to work.
 */
@Test(groups = "live")
public class VirtualBoxComputeServiceAdapterLiveTest {


   @Test
   public void testGetImages() throws Exception {

      VirtualBoxManager virtualBoxManager = VirtualBoxManager.createInstance("");

      URI endpoint = new URI("http://localhost:18083");
      virtualBoxManager.connect(endpoint.toASCIIString(), "admin", "123456");

      VirtualBoxComputeServiceAdapter adapter = new VirtualBoxComputeServiceAdapter(virtualBoxManager);
      IMachineToImage iMachineToImage = new IMachineToImage(virtualBoxManager);
      Iterable<IMachine> iMachineIterable = adapter.listImages();

      for (IMachine iMachine : iMachineIterable) {
         Image image = iMachineToImage.apply(iMachine);
         System.out.println(image);
      }
   }
}