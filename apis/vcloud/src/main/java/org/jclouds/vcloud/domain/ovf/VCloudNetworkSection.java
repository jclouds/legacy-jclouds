/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.domain.ovf;

import java.net.URI;

import org.jclouds.ovf.Network;
import org.jclouds.ovf.NetworkSection;

/**
 * VCloud extension
 */
public class VCloudNetworkSection extends NetworkSection {
   protected final String type;
   protected final URI href;

   public VCloudNetworkSection(String type, URI href, String info, Iterable<Network> networks) {
      super(info, networks);
      this.type = type;
      this.href = href;
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      VCloudNetworkSection other = (VCloudNetworkSection) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", type=" + getType() + ", info=" + getInfo() + ", networks=" + getNetworks() + "]";
   }
}