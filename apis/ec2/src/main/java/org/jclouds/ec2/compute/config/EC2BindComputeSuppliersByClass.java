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

import java.util.Set;

import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.suppliers.EC2HardwareSupplier;
import org.jclouds.ec2.compute.suppliers.EC2ImageSupplier;

import com.google.common.base.Supplier;
/**
 * @author Adrian Cole
 */
public class EC2BindComputeSuppliersByClass extends BindComputeSuppliersByClass {
   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return EC2HardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return EC2ImageSupplier.class;
   }
}
