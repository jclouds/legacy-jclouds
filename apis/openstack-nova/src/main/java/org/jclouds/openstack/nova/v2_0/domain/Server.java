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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

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
    * <p/>
    * Other possible values for the status attribute include: BUILD, REBUILD, SUSPENDED, RESIZE,
    * VERIFY_RESIZE, REVERT_RESIZE, PASSWORD, REBOOT, HARD_REBOOT, DELETED, UNKNOWN, and ERROR.
    *
    * @author Adrian Cole
    */
   public static enum Status {

      ACTIVE, BUILD, REBUILD, SUSPENDED, PAUSED, RESIZE, VERIFY_RESIZE, REVERT_RESIZE, PASSWORD, REBOOT, HARD_REBOOT, DELETED, UNKNOWN, ERROR, UNRECOGNIZED;

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
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServer(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T> {
      protected String uuid;
      protected String tenantId;
      protected String userId;
      protected Date updated;
      protected Date created;
      protected String hostId;
      protected String accessIPv4;
      protected String accessIPv6;
      protected Server.Status status;
      protected Resource image;
      protected Resource flavor;
      protected String keyName;
      protected String configDrive;
      protected Multimap<String, Address> addresses = ImmutableMultimap.of();
      protected Map<String, String> metadata = ImmutableMap.of();
      protected ServerExtendedStatus extendedStatus;
      protected ServerExtendedAttributes extendedAttributes;
      protected String diskConfig;

      /**
       * @see Server#getUuid()
       */
      public T uuid(String uuid) {
         this.uuid = uuid;
         return self();
      }

      /**
       * @see Server#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      /**
       * @see Server#getUserId()
       */
      public T userId(String userId) {
         this.userId = userId;
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
       * @see Server#getCreated()
       */
      public T created(Date created) {
         this.created = created;
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
       * @see Server#getStatus()
       */
      public T status(Server.Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Server#getImage()
       */
      public T image(Resource image) {
         this.image = image;
         return self();
      }

      /**
       * @see Server#getFlavor()
       */
      public T flavor(Resource flavor) {
         this.flavor = flavor;
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
       * @see Server#getConfigDrive()
       */
      public T configDrive(String configDrive) {
         this.configDrive = configDrive;
         return self();
      }

      /**
       * @see Server#getAddresses()
       */
      public T addresses(Multimap<String, Address> addresses) {
         this.addresses = addresses;
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
       * @see Server#getExtendedStatus()
       */
      public T extendedStatus(ServerExtendedStatus extendedStatus) {
         this.extendedStatus = extendedStatus;
         return self();
      }

      /**
       * @see Server#getExtendedAttributes()
       */
      public T extendedAttributes(ServerExtendedAttributes extendedAttributes) {
         this.extendedAttributes = extendedAttributes;
         return self();
      }

      /**
       * @see Server#getDiskConfig()
       */
      public T diskConfig(String diskConfig) {
         this.diskConfig = diskConfig;
         return self();
      }

      public Server build() {
         return new Server(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6,
               status, image, flavor, keyName, configDrive, addresses, metadata, extendedStatus,
               extendedAttributes, diskConfig);
      }

      public T fromServer(Server in) {
         return super.fromResource(in)
               .uuid(in.getUuid())
               .tenantId(in.getTenantId())
               .userId(in.getUserId())
               .updated(in.getUpdated())
               .created(in.getCreated())
               .hostId(in.getHostId())
               .accessIPv4(in.getAccessIPv4())
               .accessIPv6(in.getAccessIPv6())
               .status(in.getStatus())
               .image(in.getImage())
               .flavor(in.getFlavor())
               .keyName(in.getKeyName())
               .configDrive(in.getConfigDrive())
               .addresses(in.getAddresses())
               .metadata(in.getMetadata())
               .extendedStatus(in.getExtendedStatus().orNull())
               .extendedAttributes(in.getExtendedAttributes().orNull())
               .diskConfig(in.getDiskConfig().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String uuid;
   @Named("tenant_id")
   private final String tenantId;
   @Named("user_id")
   private final String userId;
   private final Date updated;
   private final Date created;
   private final String hostId;
   private final String accessIPv4;
   private final String accessIPv6;
   private final Server.Status status;
   private final Resource image;
   private final Resource flavor;
   @Named("key_name")
   private final String keyName;
   @Named("config_drive")
   private final String configDrive;
   private final Multimap<String, Address> addresses;
   private final Map<String, String> metadata;
   private final Optional<ServerExtendedStatus> extendedStatus;
   private final Optional<ServerExtendedAttributes> extendedAttributes;
   @Named("OS-DCF:diskConfig")
   private final Optional<String> diskConfig;

   @ConstructorProperties({
         "id", "name", "links", "uuid", "tenant_id", "user_id", "updated", "created", "hostId", "accessIPv4", "accessIPv6", "status", "image", "flavor", "key_name", "config_drive", "addresses", "metadata", "extendedStatus", "extendedAttributes", "OS-DCF:diskConfig"
   })
   protected Server(String id, @Nullable String name, java.util.Set<Link> links, @Nullable String uuid, String tenantId,
                    String userId, @Nullable Date updated, Date created, @Nullable String hostId, @Nullable String accessIPv4,
                    @Nullable String accessIPv6, Server.Status status, Resource image, Resource flavor, @Nullable String keyName,
                    @Nullable String configDrive, Multimap<String, Address> addresses, Map<String, String> metadata,
                    @Nullable ServerExtendedStatus extendedStatus, @Nullable ServerExtendedAttributes extendedAttributes,
                    @Nullable String diskConfig) {
      super(id, name, links);
      this.uuid = uuid;
      this.tenantId = checkNotNull(tenantId, "tenantId");
      this.userId = checkNotNull(userId, "userId");
      this.updated = updated;
      this.created = checkNotNull(created, "created");
      this.hostId = Strings.emptyToNull(hostId);
      this.accessIPv4 = Strings.emptyToNull(accessIPv4);
      this.accessIPv6 = Strings.emptyToNull(accessIPv6);
      this.status = checkNotNull(status, "status");
      this.image = checkNotNull(image, "image");
      this.flavor = checkNotNull(flavor, "flavor");
      this.keyName = Strings.emptyToNull(keyName);
      this.configDrive = Strings.emptyToNull(configDrive);
      this.addresses = ImmutableMultimap.copyOf(checkNotNull(addresses, "addresses"));
      this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
      this.extendedStatus = Optional.fromNullable(extendedStatus);
      this.extendedAttributes = Optional.fromNullable(extendedAttributes);
      this.diskConfig = Optional.fromNullable(diskConfig);
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

   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   public Date getCreated() {
      return this.created;
   }

   /**
    * @return host identifier, or null if in {@link Status#BUILD}
    */
   @Nullable
   public String getHostId() {
      return this.hostId;
   }

   @Nullable
   public String getAccessIPv4() {
      return this.accessIPv4;
   }

   @Nullable
   public String getAccessIPv6() {
      return this.accessIPv6;
   }

   public Status getStatus() {
      return this.status;
   }

   @Nullable
   public String getConfigDrive() {
      return this.configDrive;
   }

   public Resource getImage() {
      return this.image;
   }

   public Resource getFlavor() {
      return this.flavor;
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    * @return the ip addresses assigned to the server
    */
   public Multimap<String, Address> getAddresses() {
      return addresses;
   }

   /**
    * @return keyName if extension is present and there is a valur for this server
    * @see KeyPairClient
    */
   @Nullable
   public String getKeyName() {
      return keyName;
   }

   /**
    * Retrieves the extended server status fields (alias "OS-EXT-STS")
    * <p/>
    * NOTE: This field is only present if the Extended Status extension is installed.
    *
    * @see org.jclouds.openstack.nova.v2_0.features.ExtensionClient#getExtensionByAlias
    * @see org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces#EXTENDED_STATUS
    */
   public Optional<ServerExtendedStatus> getExtendedStatus() {
      return this.extendedStatus;
   }

   /**
    * Retrieves the extended server attributes fields (alias "OS-EXT-SRV-ATTR")
    * <p/>
    * NOTE: This field is only present if the The Extended Server Attributes API extension is installed.
    *
    * @see org.jclouds.openstack.nova.v2_0.features.ExtensionClient#getExtensionByAlias
    * @see org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces#EXTENDED_STATUS
    */
   public Optional<ServerExtendedAttributes> getExtendedAttributes() {
      return this.extendedAttributes;
   }

   /**
    * Disk config attribute from the Disk Config Extension (alias "OS-DCF")
    * <p/>
    * NOTE: This field is only present if the Disk Config extension is installed
    *
    * @see org.jclouds.openstack.nova.v2_0.features.ExtensionClient#getExtensionByAlias
    * @see org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces#DISK_CONFIG
    */
   public Optional<String> getDiskConfig() {
      return this.diskConfig;
   }


   // hashCode/equals from super is ok

   @Override
   protected ToStringHelper string() {
      return super.string()
            .add("uuid", uuid).add("tenantId", tenantId).add("userId", userId).add("updated", updated).add("created", created)
            .add("hostId", hostId).add("accessIPv4", accessIPv4).add("accessIPv6", accessIPv6).add("status", status).add("image", image)
            .add("flavor", flavor).add("keyName", keyName).add("configDrive", configDrive).add("addresses", addresses)
            .add("metadata", metadata).add("extendedStatus", extendedStatus).add("extendedAttributes", extendedAttributes)
            .add("diskConfig", diskConfig);
   }

}
