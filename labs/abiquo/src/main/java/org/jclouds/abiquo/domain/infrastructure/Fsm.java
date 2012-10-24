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

import com.abiquo.server.core.infrastructure.FsmDto;

/**
 * Adds high level functionality to {@link FsmDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href="http://community.abiquo.com/display/ABI20/Rack+Resource">
 *      http://community.abiquo.com/display/ABI20/Rack+Resource</a>
 */
@EnterpriseEdition
public class Fsm extends DomainWrapper<FsmDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Fsm(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final FsmDto target) {
      super(context, target);
   }

   // Delegate Methods

   public String getDescription() {
      return target.getDescription();
   }

   public String getDn() {
      return target.getDn();
   }

   public String getError() {
      return target.getError();
   }

   public String getProgress() {
      return target.getProgress();
   }

   public String getStatus() {
      return target.getStatus();
   }

   @Override
   public String toString() {
      return "Fsm [Dn=" + getDn() + ", Description=" + getDescription() + ", Error=" + getError() + ", Progress="
            + getProgress() + ", Status=" + getStatus() + "]";
   }

}
