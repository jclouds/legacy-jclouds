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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairClient;
import org.jclouds.util.Multimaps2;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
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
               NodeState.UNRECOGNIZED), PAUSED(NodeState.SUSPENDED);

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


   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServer(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      private String uuid;
      private String tenantId;
      private String userId;
      private Date updated;
      private Date created;
      private String hostId;
      private String accessIPv4;
      private String accessIPv6;
      private Server.Status status;
      private Resource image;
      private Resource flavor;
      private String adminPass;
      private String keyName;
      private String configDrive;
      private Multimap<String, Address> addresses = ImmutableMultimap.of();
      private Map<String, String> metadata = ImmutableMap.of();
      private String taskState;
      private String vmState;
      private String powerState;
      private String instanceName;
      private String hostName;
      private String hypervisorName;
      private String diskConfig;

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
       * @see Server#getAdminPass()
       */
      public T adminPass(String adminPass) {
         this.adminPass = adminPass;
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
         this.metadata = metadata;
         return self();
      }

      /**
       * @see Server#getTaskState()
       */
      public T taskState(String taskState) {
         this.taskState = taskState;
         return self();
      }

      /**
       * @see Server#getVmState()
       */
      public T vmState(String vmState) {
         this.vmState = vmState;
         return self();
      }

      /**
       * @see Server#getPowerState()
       */
      public T powerState(String powerState) {
         this.powerState = powerState;
         return self();
      }

      /**
       * @see Server#getInstanceName()
       */
      public T instanceName(String instanceName) {
         this.instanceName = instanceName;
         return self();
      }

      /**
       * @see Server#getHostName()
       */
      public T hostName(String hostName) {
         this.hostName = hostName;
         return self();
      }

      /**
       * @see Server#getHypervisorName()
       */
      public T hypervisorName(String hypervisorName) {
         this.hypervisorName = hypervisorName;
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
         return new Server(this);
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
               .adminPass(in.getAdminPass())
               .keyName(in.getKeyName())
               .configDrive(in.getConfigDrive())
               .addresses(in.getAddresses())
               .metadata(in.getMetadata())
               .taskState(in.getTaskState())
               .vmState(in.getVmState())
               .powerState(in.getPowerState())
               .instanceName(in.getInstanceName())
               .hostName(in.getHostName())
               .hypervisorName(in.getHypervisorName())
               .diskConfig(in.getDiskConfig());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
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
   protected final Map<String, Set<Address>> addresses;
   protected final Map<String, String> metadata;

   // Extended status extension
   @SerializedName("OS-EXT-STS:task_state")
   protected final String taskState;
   @SerializedName("OS-EXT-STS:vm_state")
   protected final String vmState;
   @SerializedName("OS-EXT-STS:power_state")
   protected final String powerState;

   // Extended server attributes extension
   @SerializedName("OS-EXT-SRV-ATTR:instance_name")
   protected final String instanceName;
   @SerializedName("OS-EXT-SRV-ATTR:host")
   protected final String hostName;
   @SerializedName("OS-EXT-SRV-ATTR:hypervisor_hostname")
   protected final String hypervisorName;

   // Disk Config extension
   @SerializedName("OS-DCF:diskConfig")
   protected final String diskConfig;

   protected Server(Builder<?> builder) {
      super(builder);
      this.uuid = builder.uuid; // TODO: see what version this came up in
      this.tenantId = checkNotNull(builder.tenantId, "tenantId");
      this.userId = checkNotNull(builder.userId, "userId");
      this.updated = checkNotNull(builder.updated, "updated");
      this.created = checkNotNull(builder.created, "created");
      this.hostId = builder.hostId;
      this.accessIPv4 = builder.accessIPv4;
      this.accessIPv6 = builder.accessIPv6;
      this.status = checkNotNull(builder.status, "status");
      this.configDrive = builder.configDrive;
      this.image = checkNotNull(builder.image, "image");
      this.flavor = checkNotNull(builder.flavor, "flavor");
      this.metadata = Maps.newHashMap(builder.metadata);
      this.addresses = Multimaps2.toOldSchool(ImmutableMultimap.copyOf(checkNotNull(builder.addresses, "addresses")));
      this.adminPass = builder.adminPass;
      this.keyName = builder.keyName;
      this.taskState = builder.taskState;
      this.vmState = builder.vmState;
      this.powerState = builder.powerState;
      this.instanceName = builder.instanceName;
      this.hostName = builder.hostName;
      this.hypervisorName = builder.hypervisorName;
      this.diskConfig = builder.diskConfig;
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
    * @return host identifier, or null if in {@link Status#BUILD}
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
    * @return the ip addresses assigned to the server
    */
   public Multimap<String, Address> getAddresses() {
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


   /**
    * State of task running against this instance (e.g. "suspending")
    * <p/>
    * NOTE: This field is only present if the Extended Status extension is installed.
    */
   @Nullable
   public String getTaskState() {
      return this.taskState;
   }

   /**
    * State of task running against this instance (e.g. "suspending")
    * <p/>
    * NOTE: This field is only present if the Extended Status extension is installed.
    */
   @Nullable
   public String getVmState() {
      return this.vmState;
   }

   /**
    * State of task running against this instance (e.g. "suspending")
    * <p/>
    * NOTE: This field is only present if the Extended Status extension is installed.
    */
   @Nullable
   public String getPowerState() {
      return this.powerState;
   }

   /**
    * The name of the instance?
    * <p/>
    * NOTE: This field is only present if the The Extended Server Attributes API extension is installed.
    */
   @Nullable
   public String getInstanceName() {
      return this.instanceName;
   }

   /**
    * The host name of the host this Server is running on
    * <p/>
    * NOTE: This field is only present if the The Extended Server Attributes API extension is installed.
    * @see #getHostId()
    */
   @Nullable
   public String getHostName() {
      return this.hostName;
   }

   /**
    * The name of the hypervisor this Server is running on
    * <p/>
    * NOTE: This field is only present if the The Extended Server Attributes API extension is installed.
    */
   @Nullable
   public String getHypervisorName() {
      return this.hypervisorName;
   }

   /**
    * State of task running against this instance (e.g. "suspending")
    * <p/>
    * NOTE: This field is only present if the Disk Config extension is installed.
    */
   @Nullable
   public String getDiskConfig() {
      return this.diskConfig;
   }


   // hashCode/equals from super is ok

   @Override
   protected ToStringHelper string() {
      return super.string().add("uuid", uuid).add("tenantId", tenantId).add(
               "userId", userId).add("hostId", getHostId()).add("updated", updated).add("created", created).add(
               "accessIPv4", getAccessIPv4()).add("accessIPv6", getAccessIPv6()).add("status", status).add(
               "configDrive", getConfigDrive()).add("image", image).add("flavor", flavor).add("metadata", metadata)
               .add("addresses", getAddresses()).add("adminPass", adminPass);
   }
}
