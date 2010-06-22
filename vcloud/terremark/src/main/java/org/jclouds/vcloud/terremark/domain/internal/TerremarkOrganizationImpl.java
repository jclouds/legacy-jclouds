/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain.internal;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.internal.OrganizationImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkOrganization;

/**
 * Locations of resources in a Terremark vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkOrganizationImpl extends OrganizationImpl implements
      TerremarkOrganization {

   private final NamedResource keysList;

   public TerremarkOrganizationImpl(String id, String name, URI location,
         Map<String, NamedResource> catalogs, Map<String, NamedResource> vdcs,
         Map<String, NamedResource> tasksLists, NamedResource keysList) {
      super(id, name, location, catalogs, vdcs, tasksLists);
      this.keysList = keysList;
   }

   @Override
   public NamedResource getKeysList() {
      return keysList;
   }

}