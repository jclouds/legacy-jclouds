/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vcloud.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.internal.BaseVCloudApiLiveTest;
import org.testng.annotations.Test;

import com.google.inject.Key;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "CatalogApiLiveTest")
public class CatalogApiLiveTest extends BaseVCloudApiLiveTest {
   @Test
   public void testGetCatalog() throws Exception {
      Org org = getVCloudApi().getOrgApi().findOrgNamed(null);
      for (ReferenceType catalog : org.getCatalogs().values()) {
         assertEquals(catalog.getType(), VCloudMediaType.CATALOG_XML);
         assertNotNull(getVCloudApi().getCatalogApi().getCatalog(catalog.getHref()));
      }
   }

   @Test
   public void testFindCatalogIsWriteableIfNotVersion1_5() throws Exception {
      // when we are in vCloud 1.0.0 public catalogs don't work, so our default
      // catalog is private
      if (!view.utils().injector().getInstance(Key.get(String.class, ApiVersion.class)).startsWith("1.5"))
         assertTrue(getVCloudApi().getCatalogApi().findCatalogInOrgNamed(null, null).isReadOnly());
   }
}
