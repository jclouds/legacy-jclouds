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
import java.util.Map;

import javax.inject.Singleton;

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
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "RegionIdToAdminURIFromAccessForTypeAndVersionSupplierTest")
public class RegionIdToAdminURIFromAccessForTypeAndVersionSupplierTest {
   private final RegionIdToAdminURISupplier.Factory factory = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().implement(RegionIdToAdminURISupplier.class,
                  RegionIdToAdminURIFromAccessForTypeAndVersionSupplier.class).build(RegionIdToAdminURISupplier.Factory.class));
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      public Supplier<Access> provide() {
         return Suppliers.ofInstance(new ParseAccessTest().expected());
      }
   }).getInstance(RegionIdToAdminURISupplier.Factory.class);

   public void testRegionMatches() {
      assertEquals(Maps.transformValues(factory.createForApiTypeAndVersion("identity", "2.0").get(), Suppliers
               .<URI> supplierFunction()), ImmutableMap.of("region-a.geo-1", URI.create("https://csnode.jclouds.org:35357/v2.0/")));
      Map<String, URI> map = Maps.newLinkedHashMap();
      map.put("region-a.geo-1", null);
      assertEquals(Maps.transformValues(factory.createForApiTypeAndVersion("compute", "1.1").get(), Suppliers
               .<URI> supplierFunction()), map);
   }

}
