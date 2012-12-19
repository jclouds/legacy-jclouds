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

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.rest.RestContext;

import com.abiquo.am.model.TemplateStateDto;
import com.abiquo.am.model.TemplateStatusEnumType;

/**
 * Adds high level functionality to {@link TemplateStateDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class TemplateState extends DomainWrapper<TemplateStateDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected TemplateState(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final TemplateStateDto target) {
      super(context, target);
   }

   // Domain operations

   public Double getDownloadingProgress() {
      return target.getDownloadingProgress();
   }

   public String getErrorCause() {
      return target.getErrorCause();
   }

   public String getMasterOvf() {
      return target.getMasterOvf();
   }

   public String getOvfId() {
      return target.getOvfId();
   }

   public TemplateStatusEnumType getStatus() {
      return target.getStatus();
   }

   @Override
   public String toString() {
      return "TemplateState [getDownloadingProgress()=" + getDownloadingProgress() + ", getErrorCause()="
            + getErrorCause() + ", getMasterOvf()=" + getMasterOvf() + ", getOvfId()=" + getOvfId() + ", getStatus()="
            + getStatus() + "]";
   }
}
