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

package org.jclouds.cloudservers.compute.config;

import java.util.Set;

import org.jclouds.cloudservers.compute.suppliers.CloudServersHardwareSupplier;
import org.jclouds.cloudservers.compute.suppliers.CloudServersImageSupplier;
import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.JustProvider;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CloudServersBindComputeSuppliersByClass extends BindComputeSuppliersByClass {
   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return CloudServersHardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return CloudServersImageSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return JustProvider.class;
   }
}
