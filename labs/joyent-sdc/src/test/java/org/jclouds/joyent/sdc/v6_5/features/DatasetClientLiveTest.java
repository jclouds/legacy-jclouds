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
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Gerald Pereira
 */
@Test(groups = "live", testName = "DatasetClientLiveTest")
public class DatasetClientLiveTest extends BaseSDCClientLiveTest {

   @Test
   public void testListAndGetDatasets() throws Exception {
      for (String datacenterId : sdcContext.getApi().getConfiguredDatacenters()) {
         DatasetClient client = sdcContext.getApi().getDatasetClientForDatacenter(datacenterId);
         Set<Dataset> response = client.listDatasets();
         assert null != response;
         for (Dataset dataset : response) {
            Dataset newDetails = client.getDataset(dataset.getId());
            assertEquals(newDetails.getId(), dataset.getId());
            assertEquals(newDetails.getName(), dataset.getName());
            assertEquals(newDetails.getType(), dataset.getType());
            assertEquals(newDetails.getVersion(), dataset.getVersion());
            assertEquals(newDetails.getUrn(), dataset.getUrn());
            assertEquals(newDetails.getCreated(), dataset.getCreated());
            assertEquals(newDetails.isDefault(), dataset.isDefault());
         }
      }
   }
}
