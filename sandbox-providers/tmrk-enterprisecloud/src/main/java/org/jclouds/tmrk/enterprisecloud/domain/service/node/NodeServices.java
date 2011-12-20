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
package org.jclouds.tmrk.enterprisecloud.domain.service.node;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Jason King
 */
public class NodeServices {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNodeServices(this);
   }

   public static class Builder {

       private Set<NodeService> services = Sets.newLinkedHashSet();

       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.service.node.NodeServices#getNodeServices()
        */
       public Builder services(Set<NodeService> services) {
          this.services = Sets.newLinkedHashSet(checkNotNull(services, "services"));
          return this;
       }

       public Builder addNodeService(NodeService service) {
          services.add(checkNotNull(service, "service"));
          return this;
       }

       public NodeServices build() {
           return new NodeServices(services);
       }

       public Builder fromNodeServices(NodeServices in) {
          return services(in.getNodeServices());
       }
   }

   private NodeServices() {
      //For JAXB and builder use
   }

   private NodeServices(Set<NodeService> services) {
      this.services = Sets.newLinkedHashSet(services);
   }

   @XmlElement(name = "NodeService")
   private Set<NodeService> services = Sets.newLinkedHashSet();

   public Set<NodeService> getNodeServices() {
      return Collections.unmodifiableSet(services);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NodeServices that = (NodeServices) o;

      if (!services.equals(that.services)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return services.hashCode();
   }

   public String toString() {
      return "["+ services.toString()+"]";
   }
}
