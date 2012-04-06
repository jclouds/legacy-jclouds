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
package org.jclouds.ec2.compute.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.fail;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.loaders.RegionAndIdToImage;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;

/**
 * @author Aled Sage
 */
@Test(groups = "unit")
public class EC2ComputeServiceContextModuleTest {
   
   @Test
   public void testCacheLoaderDoesNotReloadAfterAuthorizationException() throws Exception {
      EC2ComputeServiceContextModule module = new EC2ComputeServiceContextModule() {
         public Supplier<CacheLoader<RegionAndName, Image>> provideRegionAndNameToImageSupplierCacheLoader(RegionAndIdToImage delegate) {
            return super.provideRegionAndNameToImageSupplierCacheLoader(delegate);
         }
      };
      
      RegionAndName regionAndName = new RegionAndName("myregion", "myname");
      AuthorizationException authException = new AuthorizationException();
      
      RegionAndIdToImage mockRegionAndIdToImage = createMock(RegionAndIdToImage.class);
      expect(mockRegionAndIdToImage.load(regionAndName)).andThrow(authException).once();
      replay(mockRegionAndIdToImage);
      
      CacheLoader<RegionAndName, Image> cacheLoader = module.provideRegionAndNameToImageSupplierCacheLoader(mockRegionAndIdToImage).get();

      for (int i = 0; i < 2; i++) {
         try {
            Image image = cacheLoader.load(regionAndName);
            fail("Expected Authrization exception, but got "+image);
         } catch (AuthorizationException e) {
            // success
         }
      }
   }
}
