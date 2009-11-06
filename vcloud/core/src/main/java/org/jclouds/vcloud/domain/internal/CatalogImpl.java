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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.Link;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogImpl extends HashMap<String, Link> implements Catalog {

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;
   private final Link catalog;

   public CatalogImpl(String name, String type, URI location, Map<String, Link> contents) {
      super(contents.size());
      this.catalog = new LinkImpl(checkNotNull(name, "name"), checkNotNull(type, "type"),
               checkNotNull(location, "location"));
      putAll(checkNotNull(contents, "contents"));
   }

   public URI getLocation() {
      return catalog.getLocation();
   }

   public String getName() {
      return catalog.getName();
   }

   public String getType() {
      return catalog.getType();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
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
      CatalogImpl other = (CatalogImpl) obj;
      if (catalog == null) {
         if (other.catalog != null)
            return false;
      } else if (!catalog.equals(other.catalog))
         return false;
      return true;
   }

}