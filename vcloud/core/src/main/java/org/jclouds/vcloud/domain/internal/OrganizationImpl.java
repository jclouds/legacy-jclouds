/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.domain.internal;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.Link;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VDC;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class OrganizationImpl extends LinkImpl implements Organization {

   private final Link catalog;
   private final Map<String, Link> vdcs;
   private final Map<String, Link> tasksLists;

   public OrganizationImpl(String name, String type, URI location, Link catalog,
            Map<String, Link> vdcs, Map<String, Link> tasksLists) {
      super(name, type, location);
      this.catalog = catalog;
      this.vdcs = vdcs;
      this.tasksLists = tasksLists;
   }

   @Catalog
   public Link getCatalog() {
      return catalog;
   }

   @VDC
   public Map<String, Link> getVDCs() {
      return vdcs;
   }

   @TasksList
   public Map<String, Link> getTasksLists() {
      return tasksLists;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
      result = prime * result + ((tasksLists == null) ? 0 : tasksLists.hashCode());
      result = prime * result + ((vdcs == null) ? 0 : vdcs.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OrganizationImpl other = (OrganizationImpl) obj;
      if (catalog == null) {
         if (other.catalog != null)
            return false;
      } else if (!catalog.equals(other.catalog))
         return false;
      if (tasksLists == null) {
         if (other.tasksLists != null)
            return false;
      } else if (!tasksLists.equals(other.tasksLists))
         return false;
      if (vdcs == null) {
         if (other.vdcs != null)
            return false;
      } else if (!vdcs.equals(other.vdcs))
         return false;
      return true;
   }

}