/*
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
package org.jclouds.vcloud.director.v1_5;

import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkEntityType;

import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link VCloudDirectorApi}.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VCloudDirectorApiLiveTest")
public class VCloudDirectorApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   @Test(description = "GET /entity/{id}")
   public void testResolveEntity() {
      for (Reference orgRef : context.getApi().getOrgApi().getOrgList()) {
         Org org = context.getApi().getOrgApi().getOrg(orgRef.getHref());
         Entity entity = context.getApi().resolveEntity(org.getId());
         checkEntityType(entity);
      }
   }

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
   }
}
