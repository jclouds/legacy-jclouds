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

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.LogicServerDto;
import com.abiquo.server.core.infrastructure.LogicServerPolicyDto;

/**
 * Adds high level functionality to {@link LogicServerDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href="http://community.abiquo.com/display/ABI20/Rack+Resource">
 *      http://community.abiquo.com/display/ABI20/Rack+Resource</a>
 */
@EnterpriseEdition
public class LogicServer extends DomainWrapper<LogicServerDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected LogicServer(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final LogicServerDto target) {
      super(context, target);
   }

   // Delegate Methods

   public String getName() {
      return target.getName();
   }

   public void setType(final String value) {
      target.setType(value);
   }

   public String getAssociated() {
      return target.getAssociated();
   }

   public String getType() {
      return target.getType();
   }

   public String getAssociatedTo() {
      return target.getAssociatedTo();
   }

   public String getDescription() {
      return target.getDescription();
   }

   public void setDescription(final String value) {
      target.setDescription(value);
   }

   public List<LogicServerPolicyDto> getCollection() {
      return target.getCollection();
   }

   @Override
   public String toString() {
      return "LogicServer [name=" + getName() + ", associated=" + getAssociated() + ", type=" + getType()
            + ", associatedTo=" + getAssociatedTo() + ", description=" + getDescription() + "]";
   }

}
