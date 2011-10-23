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
package org.jclouds.loadbalancer.config;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.OnlyLocationOrFirstRegionOptionallyMatchingRegionId;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public abstract class BindLoadBalancerSuppliersByClass extends AbstractModule {

   @Override
   protected void configure() {
      bindLocationSupplier(defineLocationSupplier());
      bindDefaultLocationSupplier(defineDefaultLocationSupplier());
   }


   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return SupplierOfLocationSet.class;
   }

   @Singleton
   static class SupplierOfLocationSet implements Supplier<Set<? extends Location>> {
      private final Set<? extends Location> locations;

      @Inject
      SupplierOfLocationSet(Set<? extends Location> locations) {
         this.locations = locations;
      }

      @Override
      public Set<? extends Location> get() {
         return locations;
      }

   }

   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return OnlyLocationOrFirstRegionOptionallyMatchingRegionId.class;
   }

   protected void bindImageSupplier(Class<? extends Supplier<Set<? extends Image>>> clazz) {
      bind(new TypeLiteral<Supplier<Set<? extends Image>>>() {
      }).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindLocationSupplier(Class<? extends Supplier<Set<? extends Location>>> clazz) {
      bind(new TypeLiteral<Supplier<Set<? extends Location>>>() {
      }).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindDefaultLocationSupplier(Class<? extends Supplier<Location>> clazz) {
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindHardwareSupplier(Class<? extends Supplier<Set<? extends Hardware>>> clazz) {
      bind(new TypeLiteral<Supplier<Set<? extends Hardware>>>() {
      }).to(clazz).in(Scopes.SINGLETON);
   }

}