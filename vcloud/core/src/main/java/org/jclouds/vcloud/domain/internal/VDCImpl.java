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
import java.util.Map;

import org.jclouds.rest.domain.Link;
import org.jclouds.rest.domain.internal.LinkImpl;
import org.jclouds.vcloud.domain.VDC;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VDCImpl extends LinkImpl implements VDC {
   private final Map<String, Link> availableNetworks;
   private final Map<String, Link> resourceEntities;
   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public VDCImpl(String name, String type, URI location, Map<String, Link> resourceEntities,
            Map<String, Link> availableNetworks) {
      super(name, type, location);
      this.availableNetworks = checkNotNull(availableNetworks, "availableNetworks");
      this.resourceEntities = checkNotNull(resourceEntities, "resourceEntities");
   }

   public Map<String, Link> getAvailableNetworks() {
      return availableNetworks;
   }

   public Map<String, Link> getResourceEntities() {
      return resourceEntities;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((availableNetworks == null) ? 0 : availableNetworks.hashCode());
      result = prime * result + ((resourceEntities == null) ? 0 : resourceEntities.hashCode());
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
      VDCImpl other = (VDCImpl) obj;
      if (availableNetworks == null) {
         if (other.availableNetworks != null)
            return false;
      } else if (!availableNetworks.equals(other.availableNetworks))
         return false;
      if (resourceEntities == null) {
         if (other.resourceEntities != null)
            return false;
      } else if (!resourceEntities.equals(other.resourceEntities))
         return false;
      return true;
   }

}