/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TerremarkOrg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Locations of resources in a Terremark vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkOrgImpl extends OrgImpl implements TerremarkOrg {

   private final ReferenceType keysList;
   private final ImmutableMap<String, ReferenceType> tasksLists;

   public TerremarkOrgImpl(String name, String type, URI id, String description, Map<String, ReferenceType> catalogs,
         Map<String, ReferenceType> vdcs, Map<String, ReferenceType> networks, Map<String, ReferenceType> tasksLists,
         ReferenceType keysList) {
      super(name, type, id, name, description, catalogs, vdcs, networks, Iterables.get(tasksLists.values(), 0),
            ImmutableList.<Task> of());
      this.tasksLists = ImmutableMap.copyOf(checkNotNull(tasksLists, "tasksLists"));
      this.keysList = checkNotNull(keysList, "keysList");
   }

   @Override
   public ReferenceType getKeysList() {
      return keysList;
   }

   @Override
   public Map<String, ReferenceType> getTasksLists() {
      return tasksLists;
   }

   @Override
   public ReferenceType getKeys() {
      return keysList;
   }

}