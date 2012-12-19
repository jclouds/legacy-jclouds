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

package org.jclouds.abiquo.domain.enterprise;

import java.util.Map;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;

/**
 * Adds high level functionality to {@link EnterprisePropertiesDto}.
 * 
 * @author Francesc Montserrat
 * @see API: <a href=
 *      "http://community.abiquo.com/display/ABI20/Enterprise+Properties+Resource"
 *      >
 *      http://community.abiquo.com/display/ABI20/Enterprise+Properties+Resource
 *      </a>
 */
@EnterpriseEdition
public class EnterpriseProperties extends DomainWrapper<EnterprisePropertiesDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected EnterpriseProperties(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         final EnterprisePropertiesDto target) {
      super(context, target);
   }

   // Domain operations
   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Properties+Resource#EnterprisePropertiesResource-UpdatesthepropertiesforanEnterprise"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Properties+
    *      Resource#
    *      EnterprisePropertiesResource-UpdatesthepropertiesforanEnterprise</a>
    */
   public void update() {
      target = context.getApi().getEnterpriseApi().updateEnterpriseProperties(target);
   }

   // Parent access
   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Resource#EnterpriseResource-RetrieveaEnterprise"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Resource#
    *      EnterpriseResource- RetrieveaEnterprise</a>
    */
   public Enterprise getEnterprise() {
      Integer enterpriseId = target.getIdFromLink(ParentLinkName.ENTERPRISE);
      return wrap(context, Enterprise.class, context.getApi().getEnterpriseApi().getEnterprise(enterpriseId));
   }

   // Delegate methods
   public Map<String, String> getProperties() {
      return target.getProperties();
   }

   public void setProperties(final Map<String, String> properties) {
      target.setProperties(properties);
   }
}
