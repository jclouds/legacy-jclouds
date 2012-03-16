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

import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.domain.Address.Type;
import org.jclouds.util.InetAddresses2;
import org.jclouds.util.Multimaps2;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;

/**
 * A server is a virtual machine instance in the compute system. Flavor and
 * image are requisite elements when creating a server.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
 *      />
 */
public class Server extends Resource {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromServer(this);
   }

   public static class Builder extends Resource.Builder {
      private String tenantId;
      private String userId;
      private Date updated;
      private Date created;
      private String hostId;
      private String accessIPv4;
      private String accessIPv6;
      private ServerStatus status;
      private int progress;
      private Resource image;
      private Resource flavor;
      private Map<String, String> metadata = Maps.newHashMap();
      // TODO: get gson multimap ad
      private Multimap<Address.Type, Address> addresses = LinkedHashMultimap.create();
      private String adminPass;

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
      public Builder hostId(String hostId) {
         this.hostId = hostId;
         return this;
      }

      /**
       * @see Server#getAccessIPv4()
       */
      public Builder accessIPv4(String accessIPv4) {
         this.accessIPv4 = accessIPv4;
         return this;
      }

      /**
       * @see Server#getAccessIPv6()
       */
      public Builder accessIPv6(String accessIPv6) {
         this.accessIPv6 = accessIPv6;
         return this;
      }

      /**
       * @see Server#getStatus()
       */
      public Builder status(ServerStatus status) {
         this.status = status;
         return this;
      }

      /**
       * @see Server#getProgress()
       */
      public Builder progress(int progress) {
         this.progress = progress;
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
         this.addresses.replaceValues(Address.Type.PRIVATE,
               ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses")));
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
         this.addresses.replaceValues(Address.Type.PUBLIC,
               ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses")));
         return this;
      }

      /**
       * @see Server#getAdminPass()
       */
      public Builder adminPass(String adminPass) {
         this.adminPass = adminPass;
         return this;
      }

      public Server build() {
         // return new Server(id, name, links, addresses);
         return new Server(id, name, links, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6, status,
               progress, image, flavor, adminPass, addresses, metadata);
      }

      public Builder fromServer(Server in) {
         return fromResource(in).addresses(in.getAddresses());
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
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }

   @SerializedName("tenant_id")
   protected String tenantId;
   @SerializedName("user_id")
   protected String userId;
   protected Date updated;
   protected Date created;
   protected String hostId;
   protected String accessIPv4;
   protected String accessIPv6;
   protected ServerStatus status;
   protected int progress;
   protected Resource image;
   protected Resource flavor;
   protected final String adminPass;
   // TODO: get gson multimap adapter!
   protected final Map<Address.Type, Set<Address>> addresses;
   protected Map<String, String> metadata;

   protected Server(String id, String name, Set<Link> links, String tenantId, String userId, Date updated,
         Date created, String hostId, String accessIPv4, String accessIPv6, ServerStatus status, int progress,
         Resource image, Resource flavor, String adminPass, Multimap<Address.Type, Address> addresses,
         Map<String, String> metadata) {
      super(id, name, links);
      this.tenantId = tenantId;
      this.userId = userId;
      this.updated = updated;
      this.created = created;
      this.hostId = hostId;
      this.accessIPv4 = accessIPv4;
      this.accessIPv6 = accessIPv6;
      this.status = status;
      this.progress = progress;
      this.image = image;
      this.flavor = flavor;
      this.metadata = Maps.newHashMap(metadata);
      this.addresses = Multimaps2.toOldSchool(ImmutableMultimap.copyOf(checkNotNull(addresses, "addresses")));
      this.adminPass = adminPass;
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

   public String getHostId() {
      return this.hostId;
   }

   public String getAccessIPv4() {
      return this.accessIPv4;
   }

   public String getAccessIPv6() {
      return this.accessIPv6;
   }

   public ServerStatus getStatus() {
      return this.status;
   }

   public int getProgress() {
      return this.progress;
   }

   public Resource getImage() {
      return this.image;
   }

   public Resource getFlavor() {
      return this.flavor;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
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
      ImmutableSetMultimap.Builder<Type, Address> returnMapBuilder = new ImmutableSetMultimap.Builder<Type, Address>();

      Set<Address> publicAddresses = addresses.get(Address.Type.PUBLIC);
      Set<Address> privateAddresses = addresses.get(Address.Type.PRIVATE);
      if (publicAddresses != null) {
         returnMapBuilder.putAll(Address.Type.PUBLIC,
               Iterables.filter(publicAddresses, Predicates.not(IsPrivateAddress.INSTANCE)));
      }
      if (privateAddresses != null) {
         returnMapBuilder.putAll(Address.Type.PRIVATE, Iterables.filter(privateAddresses, IsPrivateAddress.INSTANCE));
         returnMapBuilder.putAll(Address.Type.PUBLIC,
               Iterables.filter(privateAddresses, Predicates.not(IsPrivateAddress.INSTANCE)));
      }
      ImmutableSetMultimap<Type, Address> returnMap = returnMapBuilder.build();

      return returnMap.size() > 0 ? returnMap : Multimaps2.fromOldSchool(addresses);
   }

   private static enum IsPrivateAddress implements Predicate<Address> {
      INSTANCE;
      public boolean apply(Address in) {
         return InetAddresses2.IsPrivateIPAddress.INSTANCE.apply(in.getAddr());
      }
   }

   /**
    * @return the administrative password for this server.
    */
   public String getAdminPass() {
      return adminPass;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("tenantId", tenantId).add("userId", userId)
            .add("hostId", hostId).add("updated", updated).add("created", created).add("accessIPv4", accessIPv4)
            .add("accessIPv6", accessIPv6).add("status", status).add("progress", progress).add("image", image)
            .add("flavor", flavor).add("metadata", metadata).add("links", links).add("addresses", addresses)
            .add("adminPass", adminPass).toString();
   }
}
