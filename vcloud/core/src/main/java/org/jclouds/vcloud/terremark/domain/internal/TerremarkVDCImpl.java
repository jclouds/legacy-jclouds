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
package org.jclouds.vcloud.terremark.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.rest.domain.Link;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;

/**
 * Locations of resources in Terremark vDC
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkVDCImpl extends VDCImpl implements TerremarkVDC {

   private final Link catalog;
   private final Link publicIps;
   private final Link internetServices;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public TerremarkVDCImpl(String name, String type, URI location,
            Map<String, Link> availableNetworks, Map<String, Link> resourceEntities, Link catalog,
            Link publicIps, Link internetServices) {
      super(name, type, location, availableNetworks, resourceEntities);
      this.catalog = checkNotNull(catalog, "catalog");
      this.publicIps = checkNotNull(publicIps, "publicIps");
      this.internetServices = checkNotNull(internetServices, "internetServices");
   }

   public Link getCatalog() {
      return catalog;
   }

   public Link getPublicIps() {
      return publicIps;
   }

   public Link getInternetServices() {
      return internetServices;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
      result = prime * result + ((internetServices == null) ? 0 : internetServices.hashCode());
      result = prime * result + ((publicIps == null) ? 0 : publicIps.hashCode());
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
      TerremarkVDCImpl other = (TerremarkVDCImpl) obj;
      if (catalog == null) {
         if (other.catalog != null)
            return false;
      } else if (!catalog.equals(other.catalog))
         return false;
      if (internetServices == null) {
         if (other.internetServices != null)
            return false;
      } else if (!internetServices.equals(other.internetServices))
         return false;
      if (publicIps == null) {
         if (other.publicIps != null)
            return false;
      } else if (!publicIps.equals(other.publicIps))
         return false;
      return true;
   }

}