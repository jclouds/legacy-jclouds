/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute.config;

import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.suppliers.DefaultLocationSupplier;
import org.jclouds.compute.suppliers.LocationSupplier;
import org.jclouds.domain.Location;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
/**
 * @author Adrian Cole
 */
public abstract class BindComputeSuppliersByClass extends AbstractModule {

   @Override
   protected void configure() {
      bindImageSupplier(defineImageSupplier());
      bindLocationSupplier(defineLocationSupplier());
      bindHardwareSupplier(defineHardwareSupplier());
      bindDefaultLocationSupplier(defineDefaultLocationSupplier());
   }

   protected abstract Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier();

   protected abstract Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier();

   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return LocationSupplier.class;
   }

   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return DefaultLocationSupplier.class;
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