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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * The NetworkConnectionSection element specifies how a Vm is connected to a vApp network. It
 * extends the ovf:NetworkConnection element.
 * <p/>
 * NOTE The OVF NetworkSection element and the vCloud API NetworkConnectionSection element specify
 * many of the same parameters for a network connection. If both are present in a Vm body, the
 * values specified in the NetworkConnectionSection override those specified in the NetworkSection.
 */
public class NetworkConnectionSection {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String type;
      protected URI href;
      protected String info;
      protected Integer primaryNetworkConnectionIndex;
      protected Set<NetworkConnection> connections = ImmutableSet.of();
      protected ReferenceType edit;

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      public Builder info(String info) {
         this.info = info;
         return this;
      }

      public Builder primaryNetworkConnectionIndex(Integer primaryNetworkConnectionIndex) {
         this.primaryNetworkConnectionIndex = primaryNetworkConnectionIndex;
         return this;
      }

      public Builder connections(Iterable<NetworkConnection> connections) {
         this.connections = ImmutableSet.copyOf(checkNotNull(connections, "connections"));
         return this;
      }

      public Builder edit(ReferenceType edit) {
         this.edit = edit;
         return this;
      }

      public NetworkConnectionSection build() {
         return new NetworkConnectionSection(type, href, info, primaryNetworkConnectionIndex, connections, edit);
      }

      public static Builder fromNetworkConnectionSection(NetworkConnectionSection in) {
         return new Builder().type(in.getType()).href(in.getHref()).info(in.getInfo())
               .primaryNetworkConnectionIndex(in.getPrimaryNetworkConnectionIndex()).connections(in.getConnections())
               .edit(in.getEdit());
      }
   }

   protected final String type;
   protected final URI href;
   protected final String info;
   protected final Integer primaryNetworkConnectionIndex;
   protected final Set<NetworkConnection> connections;
   protected final ReferenceType edit;

   public NetworkConnectionSection(String type, URI href, String info, Integer primaryNetworkConnectionIndex,
         Iterable<NetworkConnection> connections, ReferenceType edit) {
      this.type = type;
      this.href = href;
      this.info = info;
      this.primaryNetworkConnectionIndex = primaryNetworkConnectionIndex;
      this.connections = ImmutableSet.copyOf(checkNotNull(connections, "connections"));
      this.edit = edit;
   }

   /**
    * 
    * @return media type of this section
    */
   public String getType() {
      return type;
   }

   /**
    * 
    * @return URL to access this section
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return
    */
   public String getInfo() {
      return info;
   }

   /**
    * 
    * @return The value of the rasd:AddressOnParent element of the device (NIC) supporting the
    *         primary network connection to the containing virtual machine.
    */
   public Integer getPrimaryNetworkConnectionIndex() {
      return primaryNetworkConnectionIndex;
   }

   /**
    * 
    */
   public Set<NetworkConnection> getConnections() {
      return connections;
   }

   /**
    * 
    */
   public ReferenceType getEdit() {
      return edit;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((connections == null) ? 0 : connections.hashCode());
      result = prime * result + ((edit == null) ? 0 : edit.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result
            + ((primaryNetworkConnectionIndex == null) ? 0 : primaryNetworkConnectionIndex.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      NetworkConnectionSection other = (NetworkConnectionSection) obj;
      if (connections == null) {
         if (other.connections != null)
            return false;
      } else if (!connections.equals(other.connections))
         return false;
      if (edit == null) {
         if (other.edit != null)
            return false;
      } else if (!edit.equals(other.edit))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (primaryNetworkConnectionIndex == null) {
         if (other.primaryNetworkConnectionIndex != null)
            return false;
      } else if (!primaryNetworkConnectionIndex.equals(other.primaryNetworkConnectionIndex))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   public Builder toBuilder() {
      return Builder.fromNetworkConnectionSection(this);
   }

   @Override
   public String toString() {
      return "[href=" + href + ", connections=" + connections + ", primaryNetworkConnectionIndex="
            + primaryNetworkConnectionIndex + "]";
   }

}
