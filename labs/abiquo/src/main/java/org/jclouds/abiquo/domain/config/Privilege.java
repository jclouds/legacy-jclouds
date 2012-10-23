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

package org.jclouds.abiquo.domain.config;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.enterprise.PrivilegeDto;

/**
 * Adds high level functionality to {@link PrivilegeDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@EnterpriseEdition
public class Privilege extends DomainWrapper<PrivilegeDto> {
   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   private Privilege(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final PrivilegeDto target) {
      super(context, target);
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   @Override
   public String toString() {
      return "Privilege [id=" + getId() + ", name=" + getName() + "]";
   }

}
