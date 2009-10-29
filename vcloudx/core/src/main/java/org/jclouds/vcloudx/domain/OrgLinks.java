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
package org.jclouds.vcloudx.domain;

import java.net.URI;
import java.util.Set;

import javax.inject.Singleton;

/**
 * Locations of resources in VCloud Express
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class OrgLinks {

   private final String name;
   private final URI org;
   private final URI catalog;
   private final Set<URI> vDCs;
   private final Set<URI> taskLists;

   public OrgLinks(String name, URI org, URI catalog, Set<URI> vDCs, Set<URI> tasksLists) {
      this.name = name;
      this.org = org;
      this.catalog = catalog;
      this.vDCs = vDCs;
      this.taskLists = tasksLists;
   }

   public String getName() {
      return name;
   }

   public URI getOrg() {
      return org;
   }

   public URI getCatalog() {
      return catalog;
   }

   public Set<URI> getVDCs() {
      return vDCs;
   }

   public Set<URI> getTaskLists() {
      return taskLists;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((org == null) ? 0 : org.hashCode());
      result = prime * result + ((taskLists == null) ? 0 : taskLists.hashCode());
      result = prime * result + ((vDCs == null) ? 0 : vDCs.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OrgLinks other = (OrgLinks) obj;
      if (catalog == null) {
         if (other.catalog != null)
            return false;
      } else if (!catalog.equals(other.catalog))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (org == null) {
         if (other.org != null)
            return false;
      } else if (!org.equals(other.org))
         return false;
      if (taskLists == null) {
         if (other.taskLists != null)
            return false;
      } else if (!taskLists.equals(other.taskLists))
         return false;
      if (vDCs == null) {
         if (other.vDCs != null)
            return false;
      } else if (!vDCs.equals(other.vDCs))
         return false;
      return true;
   }

}