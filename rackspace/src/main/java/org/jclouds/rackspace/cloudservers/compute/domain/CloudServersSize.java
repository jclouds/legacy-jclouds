/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudservers.compute.domain;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.rackspace.cloudservers.domain.Flavor;

/**
 * 
 * @author Adrian Cole
 */
public class CloudServersSize extends SizeImpl {
   private final Flavor flavor;

   public CloudServersSize(Flavor flavor, String id, int cores, int ram, int disk,
            Iterable<Architecture> supportedArchitectures) {
      super(id, cores, ram, disk, supportedArchitectures);
      this.flavor = flavor;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((flavor == null) ? 0 : flavor.hashCode());
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
      CloudServersSize other = (CloudServersSize) obj;
      if (flavor == null) {
         if (other.flavor != null)
            return false;
      } else if (!flavor.equals(other.flavor))
         return false;
      return true;
   }

   public Flavor getFlavor() {
      return flavor;
   }

}
