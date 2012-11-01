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
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.config.SystemPropertyDto;

/**
 * Adds high level functionality to {@link SystemPropertyDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/System+Property+resource"
 *      > http://community.abiquo.com/display/ABI20/System+Property+resource</a>
 */

public class SystemProperty extends DomainWrapper<SystemPropertyDto> {
   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   private SystemProperty(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final SystemPropertyDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/System+Property+resource#SystemPropertyresource-ModifyanexistingSystemProperty"
    *      > http://community.abiquo.com/display/ABI20/System+Property+resource#
    *      SystemPropertyresource-ModifyanexistingSystemProperty</a>
    */
   public void update() {
      target = context.getApi().getConfigApi().updateSystemProperty(target);
   }

   // Delegate methods

   public String getName() {
      return target.getName();
   }

   public String getValue() {
      return target.getValue();
   }

   public void setValue(final String value) {
      target.setValue(value);
   }

   @Override
   public String toString() {
      return "SystemProperty [getName()=" + getName() + ", getValue()=" + getValue() + "]";
   }

}
