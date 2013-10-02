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
package org.jclouds.openstack.keystone.v1_1.suppliers;

import static org.testng.Assert.assertEquals;

import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitRegionIdSupplier;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.parse.ParseAuthTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "V1DefaultRegionIdSupplierTest")
public class V1DefaultRegionIdSupplierTest {
   private final V1DefaultRegionIdSupplier.Factory factory = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bindConstant().annotatedWith(Provider.class).to("keystone");
         install(new FactoryModuleBuilder().implement(ImplicitRegionIdSupplier.class, V1DefaultRegionIdSupplier.class)
                  .build(V1DefaultRegionIdSupplier.Factory.class));
      }

      @Provides
      @Singleton
      public Supplier<Auth> provide() {
         return Suppliers.ofInstance(new ParseAuthTest().expected());
      }
   }).getInstance(V1DefaultRegionIdSupplier.Factory.class);

   public void testRegionMatches() {
      assertEquals(factory.createForApiType("cloudFilesCDN").get(), "LON");
   }

   public void testTakesFirstPartOfDNSWhenNoRegion() {
      assertEquals(factory.createForApiType("cloudServers").get(), "lon");
   }
}
