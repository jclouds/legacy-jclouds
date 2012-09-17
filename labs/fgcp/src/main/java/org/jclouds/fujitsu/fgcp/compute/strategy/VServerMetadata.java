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
package org.jclouds.fujitsu.fgcp.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.domain.Template;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithVNICs;

import com.google.common.collect.ImmutableSet;

/**
 * Holds metadata on a virtual server, both static (name, id, type, etc.) and
 * dynamic (status, mapped public IPs, etc.).
 * 
 * @author Dies Koper
 */
public class VServerMetadata {

   protected VServer server;
   protected String id;
   protected String name;
   protected Template template;
   protected String initialPassword;
   protected VServerStatus status = VServerStatus.UNRECOGNIZED;
   protected Set<PublicIP> ips;
   protected DiskImage image;

   public VServerMetadata(VServer server, String initialPassword,
         VServerStatus status, DiskImage image, Set<PublicIP> publicIps) {
      this.server = checkNotNull(server, "server");
      this.initialPassword = initialPassword;
      this.status = status;
      this.image = image;
      this.ips = publicIps;
      id = server.getId();
      name = server.getName();
   }

   public VServerMetadata(String id, String name, Template template,
         VServerStatus status) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.template = checkNotNull(template, "template");
      this.status = checkNotNull(status, "status");
   }

   public VServer getServer() {
      return server;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public Template getTemplate() {
      return template;
   }

   public void setTemplate(Template template) {
      this.template = template;
   }

   public String getInitialPassword() {
      return initialPassword;
   }

   public VServerStatus getStatus() {
      return status;
   }

   public Set<PublicIP> getIps() {
      return ips;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private VServer server;
      private VServerWithVNICs serverWithDetails;
      private String id;
      private String name;
      private Template template;
      private String initialPassword;
      private VServerStatus status = VServerStatus.UNRECOGNIZED;
      private Set<PublicIP> publicIps = ImmutableSet.of();
      private DiskImage image;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder template(Template template) {
         this.template = template;
         return this;
      }

//      public Builder server(VServer server) {
//         this.server = server;
//         return this;
//      }

      public Builder serverWithDetails(VServerWithVNICs serverWithDetails) {
         this.serverWithDetails = serverWithDetails;
         return this;
      }

      public Builder initialPassword(String password) {
         this.initialPassword = password;
         return this;
      }

      public Builder status(VServerStatus status) {
         this.status = status;
         return this;
      }

      public Builder image(DiskImage image) {
         this.image = image;
         return this;
      }

      public Builder publicIps(Set<PublicIP> publicIps) {
         this.publicIps = publicIps;
         return this;
      }

      public VServerMetadata build() {
         if (initialPassword == null) initialPassword = "";
         if (server != null) {
            return new VServerMetadata(server, initialPassword, status,
                  image, publicIps);
         } else if (serverWithDetails != null) {
            return new VServerMetadata(serverWithDetails, initialPassword,
                  status, image, publicIps);
         } else {
            // sometimes these fields are null because the server is returning a verify error
            if (id == null) id = "dummy-id";
            if (name == null) name = "dummy-name";
            return new VServerMetadata(id, name, template, status);
         }
      }
   }
}
