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
package org.jclouds.aws.ec2.compute.config;

import java.util.Set;

import org.jclouds.aws.ec2.compute.suppliers.AWSEC2ImageSupplier;
import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.config.EC2BindComputeSuppliersByClass;

import com.google.common.base.Supplier;
/**
 * @author Aled Sage
 */
public class AWSEC2BindComputeSuppliersByClass extends EC2BindComputeSuppliersByClass {
   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return AWSEC2ImageSupplier.class;
   }
}
