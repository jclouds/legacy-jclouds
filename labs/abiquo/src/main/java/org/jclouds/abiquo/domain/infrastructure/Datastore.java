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
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.DatastoreDto;

/**
 * Adds high level functionality to {@link DatastoreDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class Datastore extends DomainWrapper<DatastoreDto> {
   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   private Datastore(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final DatastoreDto target) {
      super(context, target);
   }

   // Delegate methods

   public String getDatastoreUUID() {
      return target.getDatastoreUUID();
   }

   public String getDirectory() {
      return target.getDirectory();
   }

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public String getRootPath() {
      return target.getRootPath();
   }

   public long getSize() {
      return target.getSize();
   }

   public long getUsedSize() {
      return target.getUsedSize();
   }

   public boolean isEnabled() {
      return target.isEnabled();
   }

   public void setEnabled(final boolean enabled) {
      target.setEnabled(enabled);
   }

   @Override
   public String toString() {
      return "Datastore [id=" + getId() + ", uuid=" + getDatastoreUUID() + ", directory=" + getDirectory() + ", name="
            + getName() + ", rootPath=" + getRootPath() + ", size=" + getSize() + ", usedSize=" + getUsedSize()
            + ", enabled=" + isEnabled() + "]";
   }

}
