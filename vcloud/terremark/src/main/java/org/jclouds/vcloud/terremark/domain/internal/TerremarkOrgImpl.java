/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.OrgImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkOrg;

import com.google.common.collect.ImmutableList;

/**
 * Locations of resources in a Terremark vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkOrgImpl extends OrgImpl implements TerremarkOrg {

   private final NamedResource keysList;

   public TerremarkOrgImpl(String name, String type, URI id, String description, Map<String, NamedResource> catalogs,
            Map<String, NamedResource> vdcs, Map<String, NamedResource> networks, @Nullable NamedResource tasksList,
            NamedResource keysList) {
      super(name, type, id, name, description, catalogs, vdcs, networks, tasksList, ImmutableList.<Task> of());
      this.keysList = keysList;
   }

   @Override
   public NamedResource getKeysList() {
      return keysList;
   }

}