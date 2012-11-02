/*
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
package org.jclouds.openstack.nova.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A server is a virtual machine instance in the OpenStack Nova system. Flavor and image are
 * requisite elements when creating a server.
 *
 * @author Adrian Cole
 */
public class Server extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServer(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {
      protected String name;
      protected Map<String, String> metadata = ImmutableMap.of();
      protected Addresses addresses;
      protected String accessIPv4;
      protected String accessIPv6;
      protected String adminPass;
      protected String flavorRef;
      protected String hostId;
      protected String imageRef;
      protected String affinityId;
      protected String uuid;
      protected Flavor flavor;
      protected Image image;
      protected String keyName;
      protected Set<SecurityGroup> securityGroups = ImmutableSet.of();
      protected Date created;
      protected Date updated;
      protected Integer progress;
      protected ServerStatus status;

      /**
       * @see Server#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Server#getMetadata()
       */
      public T metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
         return self();
      }

      /**
       * @see Server#getAddresses()
       */
      public T addresses(Addresses addresses) {
         this.addresses = addresses;
         return self();
      }

      /**
       * @see Server#getAccessIPv4()
       */
      public T accessIPv4(String accessIPv4) {
         this.accessIPv4 = accessIPv4;
         return self();
      }

      /**
       * @see Server#getAccessIPv6()
       */
      public T accessIPv6(String accessIPv6) {
         this.accessIPv6 = accessIPv6;
         return self();
      }

      /**
       * @see Server#getAdminPass()
       */
      public T adminPass(String adminPass) {
         this.adminPass = adminPass;
         return self();
      }

      /**
       * @see Server#getFlavorRef()
       */
      public T flavorRef(String flavorRef) {
         this.flavorRef = flavorRef;
         return self();
      }

      /**
       * @see Server#getHostId()
       */
      public T hostId(String hostId) {
         this.hostId = hostId;
         return self();
      }

      /**
       * @see Server#getImageRef()
       */
      public T imageRef(String imageRef) {
         this.imageRef = imageRef;
         return self();
      }

      /**
       * @see Server#getAffinityId()
       */
      public T affinityId(String affinityId) {
         this.affinityId = affinityId;
         return self();
      }

      /**
       * @see Server#getUuid()
       */
      public T uuid(String uuid) {
         this.uuid = uuid;
         return self();
      }

      /**
       * @see Server#getFlavor()
       */
      public T flavor(Flavor flavor) {
         this.flavor = flavor;
         return self();
      }

      /**
       * @see Server#getImage()
       */
      public T image(Image image) {
         this.image = image;
         return self();
      }

      /**
       * @see Server#getKeyName()
       */
      public T keyName(String keyName) {
         this.keyName = keyName;
         return self();
      }

      /**
       * @see Server#getSecurityGroups()
       */
      public T securityGroups(Set<SecurityGroup> securityGroups) {
         this.securityGroups = ImmutableSet.copyOf(checkNotNull(securityGroups, "securityGroups"));
         return self();
      }

      public T securityGroups(SecurityGroup... in) {
         return securityGroups(ImmutableSet.copyOf(in));
      }

      /**
       * @see Server#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Server#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      /**
       * @see Server#getProgress()
       */
      public T progress(Integer progress) {
         this.progress = progress;
         return self();
      }

      /**
       * @see Server#getStatus()
       */
      public T status(ServerStatus status) {
         this.status = status;
         return self();
      }

      public Server build() {
         return new Server(id, links, orderedSelfReferences, name, metadata, addresses, accessIPv4, accessIPv6, adminPass,
               flavorRef, hostId, imageRef, affinityId, uuid, flavor, image, keyName, securityGroups, created, updated,
               progress, status);
      }

      public T fromServer(Server in) {
         return super.fromResource(in)
               .id(in.getId())
               .name(in.getName())
               .metadata(in.getMetadata())
               .addresses(in.getAddresses())
               .accessIPv4(in.getAccessIPv4())
               .accessIPv6(in.getAccessIPv6())
               .adminPass(in.getAdminPass())
               .flavorRef(in.getFlavorRef())
               .hostId(in.getHostId())
               .imageRef(in.getImageRef())
               .affinityId(in.getAffinityId())
               .uuid(in.getUuid())
               .flavor(in.getFlavor())
               .image(in.getImage())
               .keyName(in.getKeyName())
               .securityGroups(in.getSecurityGroups())
               .created(in.getCreated())
               .updated(in.getUpdated())
               .progress(in.getProgress())
               .status(in.getStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final Map<String, String> metadata;
   private final Addresses addresses;
   private final String accessIPv4;
   private final String accessIPv6;
   private final String adminPass;
   private final String flavorRef;
   private final String hostId;
   private final String imageRef;
   private final String affinityId;
   private final String uuid;
   private final Flavor flavor;
   private final Image image;
   private final String keyName;
   private final Set<SecurityGroup> securityGroups;
   private final Date created;
   private final Date updated;
   private final Integer progress;
   private final ServerStatus status;

   @ConstructorProperties({
         "id", "links", "orderedSelfReferences", "name", "metadata", "addresses", "accessIPv4", "accessIPv6", "adminPass",
         "flavorRef", "hostId", "imageRef", "affinityId", "uuid", "flavor", "image", "key_name", "security_groups",
         "created", "updated", "progress", "status"
   })
   protected Server(int id, List<Map<String, String>> links, Map<LinkType, URI> orderedSelfReferences, String name,
                    @Nullable Map<String, String> metadata, @Nullable Addresses addresses, @Nullable String accessIPv4,
                    @Nullable String accessIPv6, @Nullable String adminPass, @Nullable String flavorRef, @Nullable String hostId,
                    @Nullable String imageRef, @Nullable String affinityId, @Nullable String uuid, @Nullable Flavor flavor,
                    @Nullable Image image, @Nullable String keyName, @Nullable Set<SecurityGroup> securityGroups,
                    @Nullable Date created, @Nullable Date updated, @Nullable Integer progress, @Nullable ServerStatus status) {
      super(id, links, orderedSelfReferences);
      this.name = checkNotNull(name, "name");
      this.metadata = metadata == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(metadata);
      this.addresses = addresses;
      this.accessIPv4 = accessIPv4;
      this.accessIPv6 = accessIPv6;
      this.adminPass = adminPass;
      this.flavorRef = flavorRef;
      this.hostId = hostId;
      this.imageRef = imageRef;
      this.affinityId = affinityId;
      this.uuid = uuid;
      this.flavor = flavor;
      this.image = image;
      this.keyName = keyName;
      this.securityGroups = securityGroups == null ? ImmutableSet.<SecurityGroup>of() : ImmutableSet.copyOf(securityGroups);
      this.created = created;
      this.updated = updated;
      this.progress = progress;
      this.status = status == null ? ServerStatus.UNKNOWN : status;
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   @Nullable
   public Addresses getAddresses() {
      return this.addresses;
   }

   /**
    * @return the accessIPv4
    */
   @Nullable
   public String getAccessIPv4() {
      return this.accessIPv4;
   }

   /**
    * @return the accessIPv6
    */
   @Nullable
   public String getAccessIPv6() {
      return this.accessIPv6;
   }

   @Nullable
   public String getAdminPass() {
      return this.adminPass;
   }

   /**
    * @deprecated in nova 1.1 api at the Diablo release, replaced by {@link #getFlavor()}
    */
   @Nullable
   public String getFlavorRef() {
      return this.flavorRef;
   }

   /**
    * The OpenStack Nova provisioning algorithm has an anti-affinity property that attempts to spread
    * out customer VMs across hosts. Under certain situations, VMs from the same customer may be
    * placed on the same host. hostId represents the host your cloud server runs on and can be used
    * to determine this scenario if it's relevant to your application.
    * <p/>
    * Note: hostId is unique PER ACCOUNT and is not globally unique.
    */
   @Nullable
   public String getHostId() {
      return this.hostId;
   }

   /**
    * @deprecated in nova 1.1 api at the Diablo release, replaced by {@link #getImage()}.
    */
   @Nullable
   public String getImageRef() {
      return this.imageRef;
   }

   @Nullable
   public String getAffinityId() {
      return this.affinityId;
   }

   @Nullable
   public String getUuid() {
      return this.uuid;
   }

   @Nullable
   public Flavor getFlavor() {
      return this.flavor;
   }

   public Image getImage() {
      return this.image;
   }

   @Nullable
   public String getKeyName() {
      return this.keyName;
   }

   /**
    * Actually, security groups are not returned by nova on server query but is
    * needed when creating a server to specify a set of groups
    */
   public Set<SecurityGroup> getSecurityGroups() {
      return this.securityGroups;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   @Nullable
   public Integer getProgress() {
      return this.progress;
   }

   /**
    * Servers contain a status attribute that can be used as an indication of the current server
    * state. Servers with an ACTIVE status are available for use.
    */
   public ServerStatus getStatus() {
      return this.status;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, metadata, addresses, accessIPv4, accessIPv6, adminPass, flavorRef,
            hostId, imageRef, affinityId, uuid, flavor, image, keyName, securityGroups, created, updated);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Server that = Server.class.cast(obj);
      return super.equals(that)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.metadata, that.metadata)
            && Objects.equal(this.addresses, that.addresses)
            && Objects.equal(this.accessIPv4, that.accessIPv4)
            && Objects.equal(this.accessIPv6, that.accessIPv6)
            && Objects.equal(this.adminPass, that.adminPass)
            && Objects.equal(this.flavorRef, that.flavorRef)
            && Objects.equal(this.hostId, that.hostId)
            && Objects.equal(this.imageRef, that.imageRef)
            && Objects.equal(this.affinityId, that.affinityId)
            && Objects.equal(this.uuid, that.uuid)
            && Objects.equal(this.flavor, that.flavor)
            && Objects.equal(this.image, that.image)
            && Objects.equal(this.keyName, that.keyName)
            && Objects.equal(this.securityGroups, that.securityGroups)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.updated, that.updated);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("name", name).add("metadata", metadata).add("addresses", addresses)
            .add("accessIPv4", accessIPv4).add("accessIPv6", accessIPv6).add("adminPass", adminPass)
            .add("flavorRef", flavorRef).add("hostId", hostId).add("imageRef", imageRef).add("affinityId", affinityId)
            .add("uuid", uuid).add("flavor", flavor).add("image", image).add("keyName", keyName)
            .add("securityGroups", securityGroups).add("created", created).add("updated", updated)
            .add("progress", progress).add("status", status);
   }

}
