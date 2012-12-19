/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.infrastructure;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.BladeLocatorLedDto;

/**
 * Adds high level functionality to {@link BladeLocatorLedDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href=
 *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-Retrievethelocatorledoftheblade"
 *      > http://community.abiquo.com/display/ABI20/MachineResource#
 *      MachineResource- Retrievethelocatorledoftheblade</a>
 */
@EnterpriseEdition
public class BladeLocatorLed extends DomainWrapper<BladeLocatorLedDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected BladeLocatorLed(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final BladeLocatorLedDto target) {
      super(context, target);
   }

   // Delegate Methods

   public String getAdminStatus() {
      return target.getAdminStatus();
   }

   public String getBladeDn() {
      return target.getBladeDn();
   }

   public String getColor() {
      return target.getColor();
   }

   public String getDn() {
      return target.getDn();
   }

   @Override
   public String toString() {
      return "BladeLocatorLed [Dn=" + getDn() + ", BladeDn=" + getBladeDn() + ", Color=" + getColor()
            + ", AdminStatus=" + getAdminStatus() + "]";
   }

}
