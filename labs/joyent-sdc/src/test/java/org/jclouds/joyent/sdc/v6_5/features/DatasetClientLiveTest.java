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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Gerald Pereira
 */
@Test(groups = "live", testName = "DatasetClientLiveTest")
public class DatasetClientLiveTest extends BaseSDCClientLiveTest {

	public void testListDatasets() {
		Set<Dataset> datasets = sdcContext.getApi().getDatasetClient()
				.listDatasets();
		assertNotNull(datasets);
		assertTrue(datasets.size() > 0);
	}

	public void testGetDataset() {
		final String id = "e4cd7b9e-4330-11e1-81cf-3bb50a972bda";
		Dataset dataset = sdcContext.getApi().getDatasetClient().getDataset(id);
		assertNotNull(dataset);
		assertEquals(dataset.getId(), id);
	}
}
