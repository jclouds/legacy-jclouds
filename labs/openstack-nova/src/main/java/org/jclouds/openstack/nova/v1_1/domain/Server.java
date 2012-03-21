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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.domain.Address.Type;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairClient;
import org.jclouds.util.Multimaps2;

import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;

/**
 * A server is a virtual machine instance in the compute system. Flavor and image are requisite
 * elements when creating a server.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
 *      />
 */
public class Server extends Resource {

   /**
    * Servers contain a status attribute that can be used as an indication of the current server
    * state. Servers with an ACTIVE status are available for use.
    * 
    * Other possible values for the status attribute include: BUILD, REBUILD, SUSPENDED, RESIZE,
    * VERIFY_RESIZE, REVERT_RESIZE, PASSWORD, REBOOT, HARD_REBOOT, DELETED, UNKNOWN, and ERROR.
    * 
    * @author Adrian Cole
    */
   public static enum Status {

      ACTIVE(NodeState.RUNNING), BUILD(NodeState.PENDING), REBUILD(NodeState.PENDING), SUSPENDED(NodeState.SUSPENDED), RESIZE(
               NodeState.PENDING), VERIFY_RESIZE(NodeState.PENDING), REVERT_RESIZE(NodeState.PENDING), PASSWORD(
               NodeState.PENDING), REBOOT(NodeState.PENDING), HARD_REBOOT(NodeState.PENDING), DELETED(
               NodeState.TERMINATED), UNKNOWN(NodeState.UNRECOGNIZED), ERROR(NodeState.ERROR), UNRECOGNIZED(
               NodeState.UNRECOGNIZED);

      private final NodeState nodeState;

      Status(NodeState nodeState) {
         this.nodeState = nodeState;
      }

      public String value() {
         return name();
      }

      public static Status fromValue(String v) {
         try {
            return valueOf(v.replaceAll("\\(.*", ""));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

      public NodeState getNodeState() {
         return nodeState;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromServer(this);
   }

   public static class Builder extends Resource.Builder {
      private String uuid;
      private String tenantId;
      private String userId;
      private Date updated;
      private Date created;
      private String hostId;
      private String accessIPv4;
      private String accessIPv6;
      private Status status;
      private String configDrive;
      private Resource image;
      private Resource flavor;
      private Map<String, String> metadata = Maps.newHashMap();
      // TODO: get gson multimap ad
      private Multimap<Address.Type, Address> addresses = LinkedHashMultimap.create();
      private String adminPass;
      private String keyName;

      /**
       * @see Server#getUuid()
       */
      public Builder uuid(@Nullable String uuid) {
         this.uuid = uuid;
         return this;
      }

      /**
       * @see Server#getTenantId()
       */
      public Builder tenantId(String tenantId) {
         this.tenantId = tenantId;
         return this;
      }

      /**
       * @see Server#getUserId()
       */
      public Builder userId(String userId) {
         this.userId = userId;
         return this;
      }

      /**
       * @see Server#getUpdated()
       */
      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      /**
       * @see Server#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      /**
       * @see Server#getHostId()
       */
      public Builder hostId(@Nullable String hostId) {
         this.hostId = hostId;
         return this;
      }

      /**
       * @see Server#getAccessIPv4()
       */
      public Builder accessIPv4(@Nullable String accessIPv4) {
         this.accessIPv4 = accessIPv4;
         return this;
      }

      /**
       * @see Server#getAccessIPv6()
       */
      public Builder accessIPv6(@Nullable String accessIPv6) {
         this.accessIPv6 = accessIPv6;
         return this;
      }

      /**
       * @see Server#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see Server#getConfigDrive()
       */
      public Builder configDrive(@Nullable String configDrive) {
         this.configDrive = configDrive;
         return this;
      }

      /**
       * @see Server#getImage()
       */
      public Builder image(Resource image) {
         this.image = image;
         return this;
      }

      /**
       * @see Server#getImage()
       */
      public Builder flavor(Resource flavor) {
         this.flavor = flavor;
         return this;
      }

      /**
       * @see Server#getMetadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(metadata);
         return this;
      }

      /**
       * @see Server#getAddresses()
       */
      public Builder addresses(Multimap<Address.Type, Address> addresses) {
         this.addresses = ImmutableMultimap.copyOf(checkNotNull(addresses, "addresses"));
         return this;
      }

      /**
       * @see Server#getPrivateAddresses()
       */
      public Builder privateAddresses(Address... privateAddresses) {
         return privateAddresses(ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses")));
      }

      /**
       * @see Server#getPrivateAddresses()
       */
      public Builder privateAddresses(Set<Address> privateAddresses) {
         this.addresses.replaceValues(Address.Type.PRIVATE, ImmutableSet.copyOf(checkNotNull(privateAddresses,
                  "privateAddresses")));
         return this;
      }

      /**
       * @see Server#getPublicAddresses()
       */
      public Builder publicAddresses(Address... publicAddresses) {
         return publicAddresses(ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses")));
      }

      /**
       * @see Server#getPublicAddresses()
       */
      public Builder publicAddresses(Set<Address> publicAddresses) {
         this.addresses.replaceValues(Address.Type.PUBLIC, ImmutableSet.copyOf(checkNotNull(publicAddresses,
                  "publicAddresses")));
         return this;
      }

      /**
       * @see Server#getAdminPass()
       */
      public Builder adminPass(String adminPass) {
         this.adminPass = adminPass;
         return this;
      }

      /**
       * @see Server#getKeyName()
       */
      public Builder keyName(@Nullable String keyName) {
         this.keyName = keyName;
         return this;
      }

      @Override
      public Server build() {
         return new Server(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6,
                  status, configDrive, image, flavor, adminPass, keyName, addresses, metadata);
      }

      public Builder fromServer(Server in) {
         return fromResource(in).uuid(in.getUuid()).tenantId(in.getTenantId()).userId(in.getUserId()).updated(
                  in.getUpdated()).created(in.getCreated()).hostId(in.getHostId()).accessIPv4(in.getAccessIPv4())
                  .accessIPv6(in.getAccessIPv6()).status(in.getStatus()).configDrive(in.getConfigDrive()).image(
                           in.getImage()).flavor(in.getFlavor()).adminPass(in.getAdminPass()).keyName(in.getKeyName())
                  .addresses(in.getAddresses()).metadata(in.getMetadata());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
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
      public Builder links(Link... links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }

   protected final String uuid;
   @SerializedName("tenant_id")
   protected final String tenantId;
   @SerializedName("user_id")
   protected final String userId;
   protected final Date updated;
   protected final Date created;
   protected final String hostId;
   protected final String accessIPv4;
   protected final String accessIPv6;
   protected final Status status;
   protected final Resource image;
   protected final Resource flavor;
   protected final String adminPass;
   @SerializedName("key_name")
   protected final String keyName;
   @SerializedName("config_drive")
   protected final String configDrive;
   // TODO: get gson multimap adapter!
   protected final Map<Address.Type, Set<Address>> addresses;
   protected final Map<String, String> metadata;

   protected Server(String id, String name, Set<Link> links, @Nullable String uuid, String tenantId, String userId,
            Date updated, Date created, @Nullable String hostId, @Nullable String accessIPv4,
            @Nullable String accessIPv6, Status status, @Nullable String configDrive, Resource image, Resource flavor,
            String adminPass, @Nullable String keyName, Multimap<Address.Type, Address> addresses,
            Map<String, String> metadata) {
      super(id, name, links);
      this.uuid = uuid; // TODO: see what version this came up in
      this.tenantId = checkNotNull(tenantId, "tenantId");
      this.userId = checkNotNull(userId, "userId");
      this.updated = checkNotNull(updated, "updated");
      this.created = checkNotNull(created, "created");
      this.hostId = hostId;
      this.accessIPv4 = accessIPv4;
      this.accessIPv6 = accessIPv6;
      this.status = checkNotNull(status, "status");
      this.configDrive = configDrive;
      this.image = checkNotNull(image, "image");
      this.flavor = checkNotNull(flavor, "flavor");
      this.metadata = Maps.newHashMap(metadata);
      this.addresses = Multimaps2.toOldSchool(ImmutableMultimap.copyOf(checkNotNull(addresses, "addresses")));
      this.adminPass = adminPass;
      this.keyName = keyName;
   }

   /**
    * only present until the id is in uuid form
    * 
    * @return uuid, if id is an integer val
    */
   @Nullable
   public String getUuid() {
      return this.uuid;
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public String getUserId() {
      return this.userId;
   }

   public Date getUpdated() {
      return this.updated;
   }

   public Date getCreated() {
      return this.created;
   }

   /**
    * 
    * @return host identifier, or null if in {@link ServerState#BUILD}
    */
   @Nullable
   public String getHostId() {
      return Strings.emptyToNull(this.hostId);
   }

   @Nullable
   public String getAccessIPv4() {
      return Strings.emptyToNull(this.accessIPv4);
   }

   @Nullable
   public String getAccessIPv6() {
      return Strings.emptyToNull(this.accessIPv6);
   }

   public Status getStatus() {
      return this.status;
   }

   @Nullable
   public String getConfigDrive() {
      return Strings.emptyToNull(this.configDrive);
   }

   public Resource getImage() {
      return this.image;
   }

   public Resource getFlavor() {
      return this.flavor;
   }

   public Map<String, String> getMetadata() {
      // in case this was assigned in gson
      return ImmutableMap.copyOf(Maps.filterValues(this.metadata, Predicates.notNull()));
   }

   /**
    * @return the private ip addresses assigned to the server
    */
   public Set<Address> getPrivateAddresses() {
      Collection<Address> privateAddresses = getAddresses().get(Address.Type.PRIVATE);
      if (privateAddresses == null) {
         return ImmutableSet.<Address> of();
      } else {
         return ImmutableSet.copyOf(privateAddresses);
      }
   }

   /**
    * @return the public ip addresses assigned to the server
    */
   public Set<Address> getPublicAddresses() {
      Collection<Address> publicAddrs = getAddresses().get(Address.Type.PUBLIC);
      if (publicAddrs == null) {
         return ImmutableSet.<Address> of();
      } else {
         return ImmutableSet.copyOf(publicAddrs);
      }
   }

   /**
    * @return the ip addresses assigned to the server
    */
   public Multimap<Type, Address> getAddresses() {
      return Multimaps2.fromOldSchool(addresses);
   }

   /**
    * @return the administrative password for this server; only present on first request.
    */
   @Nullable
   public String getAdminPass() {
      return adminPass;
   }

   /**
    * @return keyName if extension is present and there is a valur for this server
    * @see KeyPairClient
    */
   @Nullable
   public String getKeyName() {
      return keyName;
   }

   // hashCode/equals from super is ok

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("uuid", uuid).add("name", name).add("tenantId", tenantId).add(
               "userId", userId).add("hostId", getHostId()).add("updated", updated).add("created", created).add(
               "accessIPv4", getAccessIPv4()).add("accessIPv6", getAccessIPv6()).add("status", status).add(
               "configDrive", getConfigDrive()).add("image", image).add("flavor", flavor).add("metadata", metadata)
               .add("links", links).add("addresses", addresses).add("adminPass", adminPass).toString();
   }
}
