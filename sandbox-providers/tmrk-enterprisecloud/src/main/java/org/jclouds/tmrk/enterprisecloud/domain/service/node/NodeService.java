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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Entity;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.network.IpAddressReference;
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="NodeServiceType">
 * @author Jason King
 */
public class NodeService extends Entity<NodeService> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromSshKey(this);
   }

   public static class Builder extends Entity.Builder<NodeService> {
      private IpAddressReference ipAddress;
      private Protocol protocol;
      private int port;
      private boolean enabled;
      private String description;

      /**
       * @see NodeService#getIpAddress
       */
      public Builder ipAddress(IpAddressReference ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      /**
       * @see NodeService#getProtocol
       */
      public Builder protocol(Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      /**
       * @see NodeService#getPort
       */
      public Builder port(int port) {
         this.port = port;
         return this;
      }

      /**
       * @see NodeService#isEnabled
       */
      public Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      /**
       * @see NodeService#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      @Override
      public NodeService build() {
         return new NodeService(href, type, name, links, actions, tasks, ipAddress, protocol, port, enabled, description);
      }

      public Builder fromSshKey(NodeService in) {
         return fromEntity(in).ipAddress(in.getIpAddress())
                              .protocol(in.getProtocol())
                              .port(in.getPort())
                              .enabled(in.isEnabled())
                              .description(in.getDescription());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<NodeService> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<NodeService> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromEntity(Entity<NodeService> in) {
         return Builder.class.cast(super.fromEntity(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         return Builder.class.cast(super.tasks(tasks));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "IpAddress", required = false)
   private IpAddressReference ipAddress;

   @XmlElement(name = "Protocol", required = false)
   private Protocol protocol;

   @XmlElement(name = "Port", required = false)
   private int port;

   @XmlElement(name = "Enabled", required = false)
   private boolean enabled;

   @XmlElement(name = "PrivateKey", required = false)
   private String description;

   private NodeService(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<Task> tasks,
                       @Nullable IpAddressReference ipAddress, @Nullable Protocol protocol, int port, boolean enabled, @Nullable String description) {
      super(href, type, name, links, actions, tasks);
      this.ipAddress = ipAddress;
      this.protocol = protocol;
      this.port = port;
      this.enabled = enabled;
      this.description = description;
   }

   private NodeService() {
       //For JAXB
   }

   public IpAddressReference getIpAddress() {
      return ipAddress;
   }

   public Protocol getProtocol() {
      return protocol;
   }

   public int getPort() {
      return port;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      NodeService that = (NodeService) o;

      if (enabled != that.enabled) return false;
      if (port != that.port) return false;
      if (description != null ? !description.equals(that.description) : that.description != null)
         return false;
      if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null)
         return false;
      if (protocol != that.protocol) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
      result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
      result = 31 * result + port;
      result = 31 * result + (enabled ? 1 : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", ipAddress="+ipAddress+", protocol="+protocol+", port="+port+", enabled="+enabled+", description="+description;
   }

}