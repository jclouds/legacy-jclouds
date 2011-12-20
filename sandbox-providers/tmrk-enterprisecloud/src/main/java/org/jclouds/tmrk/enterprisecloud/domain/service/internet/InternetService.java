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
package org.jclouds.tmrk.enterprisecloud.domain.service.internet;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Entity;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;
import org.jclouds.tmrk.enterprisecloud.domain.service.node.NodeServices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="InternetServiceType">
 * @author Jason King
 */
@XmlRootElement(name="InternetService")
public class InternetService extends Entity<InternetService> {

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

   public static class Builder extends Entity.Builder<InternetService> {
     
      
      private Protocol protocol;
      private int port;
      private boolean enabled;
      private String description;
      private NamedResource publicIp;
      private InternetServicePersistenceType persistence;
      private String redirectUrl;
      private NamedResource monitor;
      private NamedResource trustedNetworkGroup;
      private NamedResource backupInternetService;
      private NodeServices nodeServices;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getProtocol
       */
      public Builder protocol(Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getPort
       */
      public Builder port(int port) {
         this.port = port;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#isEnabled
       */
      public Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getPersistence
       */
      public Builder persistence(InternetServicePersistenceType persistence) {
         this.persistence = persistence;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getPublicIp
       */
      public Builder publicIp(NamedResource publicIp) {
         this.publicIp = publicIp;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getRedirectUrl
       */
      public Builder redirectUrl(String redirectUrl) {
         this.redirectUrl = redirectUrl;
         return this;
      }
      
      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getMonitor
       */
      public Builder monitor(NamedResource monitor) {
         this.monitor = monitor;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getTrustedNetworkGroup
       */
      public Builder trustedNetworkGroup(NamedResource trustedNetworkGroup) {
         this.trustedNetworkGroup = trustedNetworkGroup;
         return this;
      }
      
      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getBackupInternetService
       */
      public Builder backupInternetService(NamedResource backupInternetService) {
         this.backupInternetService = backupInternetService;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService#getNodeServices
       */
      public Builder nodeServices(NodeServices nodeServices) {
         this.nodeServices = nodeServices;
         return this;
      }
      
      @Override
      public InternetService build() {
         return new InternetService(href, type, name, links, actions, tasks, 
                                    protocol, port, enabled, description,publicIp,persistence,
                                    redirectUrl, monitor, trustedNetworkGroup, backupInternetService, nodeServices);
      }

      public Builder fromSshKey(InternetService in) {
         return fromEntity(in).protocol(in.getProtocol())
                              .port(in.getPort())
                              .enabled(in.isEnabled())
                              .description(in.getDescription())
                              .publicIp(in.getPublicIp())
                              .persistence(in.getPersistence())
                              .redirectUrl(in.getRedirectUrl())
                              .monitor(in.getMonitor())
                              .trustedNetworkGroup(in.getTrustedNetworkGroup())
                              .backupInternetService(in.getBackupInternetService())
                              .nodeServices(in.getNodeServices());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<InternetService> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<InternetService> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromEntity(Entity<InternetService> in) {
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

   @XmlElement(name = "Protocol", required = false)
   private Protocol protocol;

   @XmlElement(name = "Port", required = false)
   private int port;

   @XmlElement(name = "Enabled", required = true)
   private boolean enabled;

   @XmlElement(name = "PrivateKey", required = false)
   private String description;

   @XmlElement(name = "PublicIp", required = false)
   private NamedResource publicIp;

   @XmlElement(name = "Persistence", required = false)
   private InternetServicePersistenceType persistence;

   @XmlElement(name = "RedirectUrl", required = false)
   private String redirectUrl;

   @XmlElement(name = "Monitor", required = false)
   private NamedResource monitor;

   @XmlElement(name = "TrustedNetworkGroup", required = false)
   private NamedResource trustedNetworkGroup;

   @XmlElement(name = "BackupInternetService", required = false)
   private NamedResource backupInternetService;

   @XmlElement(name = "NodeServices", required = false)
   private NodeServices nodeServices;

   private InternetService(URI href, String type, String name, Set<Link> links, Set<Action> actions, Set<Task> tasks,
                           @Nullable Protocol protocol, int port, boolean enabled, @Nullable String description,
                           @Nullable NamedResource publicIp, @Nullable InternetServicePersistenceType persistence, @Nullable String redirectUrl, @Nullable NamedResource monitor,
                           @Nullable NamedResource trustedNetworkGroup, @Nullable NamedResource backupInternetService, @Nullable NodeServices nodeServices) {
      super(href, type, name, links, actions, tasks);
      this.protocol = protocol;
      this.port = port;
      this.enabled = enabled;
      this.description = description;
      this.publicIp = publicIp;
      this.persistence = persistence;
      this.redirectUrl = redirectUrl;
      this.monitor = monitor;
      this.trustedNetworkGroup = trustedNetworkGroup;
      this.backupInternetService = backupInternetService;
      this.nodeServices = nodeServices;
   }

   private InternetService() {
       //For JAXB
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

   public NamedResource getPublicIp() {
      return publicIp;
   }

   public InternetServicePersistenceType getPersistence() {
      return persistence;
   }

   public String getRedirectUrl() {
      return redirectUrl;
   }

   public NamedResource getMonitor() {
      return monitor;
   }

   public NamedResource getTrustedNetworkGroup() {
      return trustedNetworkGroup;
   }

   public NamedResource getBackupInternetService() {
      return backupInternetService;
   }

   public NodeServices getNodeServices() {
      return nodeServices;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      InternetService that = (InternetService) o;

      if (enabled != that.enabled) return false;
      if (port != that.port) return false;
      if (backupInternetService != null ? !backupInternetService.equals(that.backupInternetService) : that.backupInternetService != null)
         return false;
      if (description != null ? !description.equals(that.description) : that.description != null)
         return false;
      if (monitor != null ? !monitor.equals(that.monitor) : that.monitor != null)
         return false;
      if (nodeServices != null ? !nodeServices.equals(that.nodeServices) : that.nodeServices != null)
         return false;
      if (persistence != null ? !persistence.equals(that.persistence) : that.persistence != null)
         return false;
      if (protocol != that.protocol) return false;
      if (publicIp != null ? !publicIp.equals(that.publicIp) : that.publicIp != null)
         return false;
      if (redirectUrl != null ? !redirectUrl.equals(that.redirectUrl) : that.redirectUrl != null)
         return false;
      if (trustedNetworkGroup != null ? !trustedNetworkGroup.equals(that.trustedNetworkGroup) : that.trustedNetworkGroup != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
      result = 31 * result + port;
      result = 31 * result + (enabled ? 1 : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (publicIp != null ? publicIp.hashCode() : 0);
      result = 31 * result + (persistence != null ? persistence.hashCode() : 0);
      result = 31 * result + (redirectUrl != null ? redirectUrl.hashCode() : 0);
      result = 31 * result + (monitor != null ? monitor.hashCode() : 0);
      result = 31 * result + (trustedNetworkGroup != null ? trustedNetworkGroup.hashCode() : 0);
      result = 31 * result + (backupInternetService != null ? backupInternetService.hashCode() : 0);
      result = 31 * result + (nodeServices != null ? nodeServices.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+
            ", protocol="+protocol+", port="+port+", enabled="+enabled+", description="+description+
            ", publicIp="+publicIp+" persistence="+persistence+", redirectUrl="+redirectUrl+
            ", monitor="+monitor+", trustedNetworkGroup="+trustedNetworkGroup+
            ", backupInternetService="+backupInternetService+", nodeServices="+nodeServices;
   }

}