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
package org.jclouds.openstack.keystone.v2_0.suppliers;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.parse.ParseAccessTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RegionIdToURIFromAccessForTypeAndVersionSupplierTest")
public class RegionIdToURIFromAccessForTypeAndVersionSupplierTest {
   private final RegionIdToURISupplier.Factory factory = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().implement(RegionIdToURISupplier.class,
                  RegionIdToURIFromAccessForTypeAndVersionSupplier.class).build(RegionIdToURISupplier.Factory.class));
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      public Supplier<Access> provide() {
         return Suppliers.ofInstance(new ParseAccessTest().expected());
      }
   }).getInstance(RegionIdToURISupplier.Factory.class);

   public void testRegionMatches() {
      assertEquals(Maps.transformValues(factory.createForApiTypeAndVersion("compute", "1.0").get(), Suppliers
               .<URI> supplierFunction()), ImmutableMap.of());
      assertEquals(Maps.transformValues(factory.createForApiTypeAndVersion("compute", "1.1").get(), Suppliers
               .<URI> supplierFunction()), ImmutableMap.of("az-1.region-a.geo-1", URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456"),
                                                           "az-2.region-a.geo-1", URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456"),
                                                           "az-3.region-a.geo-1", URI.create("https://az-3.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456")));
   }

}
