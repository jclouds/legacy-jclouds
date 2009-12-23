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

import java.net.InetAddress;
import java.net.URI;
import java.util.SortedSet;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkVApp;
import org.jclouds.vcloud.terremark.domain.TerremarkVirtualSystem;

import com.google.common.collect.ListMultimap;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkVAppImpl extends VAppImpl implements TerremarkVApp {

   private final NamedResource vDC;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public TerremarkVAppImpl(String id, String name, String type, URI location, VAppStatus status,
            long size, NamedResource vDC, ListMultimap<String, InetAddress> networkToAddresses,
            String operatingSystemDescription, TerremarkVirtualSystem system,
            SortedSet<ResourceAllocation> resourceAllocations) {
      super(id, name, location, status, size, networkToAddresses, operatingSystemDescription,
               system, resourceAllocations);
      this.vDC = vDC;
   }

   public NamedResource getVDC() {
      return vDC;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((vDC == null) ? 0 : vDC.hashCode());
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
      TerremarkVAppImpl other = (TerremarkVAppImpl) obj;
      if (vDC == null) {
         if (other.vDC != null)
            return false;
      } else if (!vDC.equals(other.vDC))
         return false;
      return true;
   }


}