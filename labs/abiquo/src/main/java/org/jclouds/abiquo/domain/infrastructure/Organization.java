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

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.OrganizationDto;

/**
 * Adds high level functionality to {@link OrganizationDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href="http://community.abiquo.com/display/ABI20/RackResource">
 *      http://community.abiquo.com/display/ABI20/RackResource</a>
 */
@EnterpriseEdition
public class Organization extends DomainWrapper<OrganizationDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Organization(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final OrganizationDto target) {
      super(context, target);
   }

   // Delegate Methods

   public String getDescription() {
      return target.getDescription();
   }

   public String getDn() {
      return target.getDn();
   }

   public String getLevel() {
      return target.getLevel();
   }

   public String getName() {
      return target.getName();
   }

   public String getStatus() {
      return target.getStatus();
   }

   public void setDescription(final String value) {
      target.setDescription(value);
   }

   public void setDn(final String dn) {
      target.setDn(dn);
   }

   public void setLevel(final String value) {
      target.setLevel(value);
   }

   public void setName(final String value) {
      target.setName(value);
   }

   public void setStatus(final String value) {
      target.setStatus(value);
   }

   @Override
   public String toString() {
      return "Organization [name=" + getName() + ", description=" + getDescription() + ", dn=" + getDn() + ", level="
            + getLevel() + ", status=" + getStatus() + "]";
   }
}
