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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class Account extends ForwardingSet<User> implements Comparable<Account> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private Type type;
      private String networkDomain;
      private String domain;
      private long domainId;
      private Long IPsAvailable;
      private Long IPLimit;
      private long IPs;
      private boolean cleanupRequired;
      private String name;
      private long receivedBytes;
      private long sentBytes;
      private Long snapshotsAvailable;
      private Long snapshotLimit;
      private long snapshots;
      private State state;
      private Long templatesAvailable;
      private Long templateLimit;
      private long templates;
      private Long VMsAvailable;
      private Long VMLimit;
      private long VMsRunning;
      private long VMsStopped;
      private long VMs;
      private Long volumesAvailable;
      private Long volumeLimit;
      private long volumes;
      private Set<User> users = ImmutableSet.of();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder networkDomain(String networkDomain) {
         this.networkDomain = networkDomain;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder IPsAvailable(Long IPsAvailable) {
         this.IPsAvailable = IPsAvailable;
         return this;
      }

      public Builder IPLimit(Long IPLimit) {
         this.IPLimit = IPLimit;
         return this;
      }

      public Builder IPs(long IPs) {
         this.IPs = IPs;
         return this;
      }

      public Builder cleanupRequired(boolean cleanupRequired) {
         this.cleanupRequired = cleanupRequired;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder receivedBytes(long receivedBytes) {
         this.receivedBytes = receivedBytes;
         return this;
      }

      public Builder sentBytes(long sentBytes) {
         this.sentBytes = sentBytes;
         return this;
      }

      public Builder snapshotsAvailable(Long snapshotsAvailable) {
         this.snapshotsAvailable = snapshotsAvailable;
         return this;
      }

      public Builder snapshotLimit(Long snapshotLimit) {
         this.snapshotLimit = snapshotLimit;
         return this;
      }

      public Builder snapshots(long snapshots) {
         this.snapshots = snapshots;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder templatesAvailable(Long templatesAvailable) {
         this.templatesAvailable = templatesAvailable;
         return this;
      }

      public Builder templateLimit(Long templateLimit) {
         this.templateLimit = templateLimit;
         return this;
      }

      public Builder templates(long templates) {
         this.templates = templates;
         return this;
      }

      public Builder VMsAvailable(Long VMsAvailable) {
         this.VMsAvailable = VMsAvailable;
         return this;
      }

      public Builder VMLimit(Long VMLimit) {
         this.VMLimit = VMLimit;
         return this;
      }

      public Builder VMsRunning(long VMsRunning) {
         this.VMsRunning = VMsRunning;
         return this;
      }

      public Builder VMsStopped(long VMsStopped) {
         this.VMsStopped = VMsStopped;
         return this;
      }

      public Builder VMs(long VMs) {
         this.VMs = VMs;
         return this;
      }

      public Builder volumesAvailable(Long volumesAvailable) {
         this.volumesAvailable = volumesAvailable;
         return this;
      }

      public Builder volumeLimit(Long volumeLimit) {
         this.volumeLimit = volumeLimit;
         return this;
      }

      public Builder volumes(long volumes) {
         this.volumes = volumes;
         return this;
      }

      public Builder users(Set<User> users) {
         this.users = ImmutableSet.copyOf(checkNotNull(users, "users"));
         return this;
      }

      public Account build() {
         return new Account(id, type, networkDomain, domain, domainId, IPsAvailable, IPLimit, IPs, cleanupRequired, name,
               receivedBytes, sentBytes, snapshotsAvailable, snapshotLimit, snapshots, state, templatesAvailable,
               templateLimit, templates, VMsAvailable, VMLimit, VMsRunning, VMsStopped, VMs, volumesAvailable,
               volumeLimit, volumes, users);
      }

   }

   public static enum State {
      ENABLED, DISABLED, LOCKED, UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static enum Type {
      /**
       * API access for all the resources associated with their account. There
       * may be many users in a domain, many domains in a deployment, and many
       * users in a deployment. This is typically the end user
       */
      USER(0),
      /**
       * full API access. This is typically a service administrator or code that
       * executes with complete trust in the service operator's environment.
       */
      ADMIN(1),
      /**
       * full API access within a domain. This is the most privileged user that
       * a given customer has. This may be a reseller for the service provider.
       */
      DOMAIN_ADMIN(2), UNRECOGNIZED(Integer.MAX_VALUE);

      private int code;

      private static final Map<Integer, Type> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(Type.values()),
            new Function<Type, Integer>() {

               @Override
               public Integer apply(Type input) {
                  return input.code;
               }

            });

      Type(int code) {
         this.code = code;
      }

      @Override
      public String toString() {
         return "" + code;
      }

      public static Type fromValue(String type) {
         Integer code = new Integer(checkNotNull(type, "type"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

   private long id;
   @SerializedName("accounttype")
   private Type type;
   @SerializedName("networkdomain")
   private String networkDomain;
   private String domain;
   @SerializedName("domainId")
   private long domainId;
   @SerializedName("ipsavailable")
   private Long IPsAvailable;
   @SerializedName("iplimit")
   private Long IPLimit;
   @SerializedName("iptotal")
   private long IPs;
   @SerializedName("iscleanuprequired")
   private boolean cleanupRequired;
   private String name;
   @SerializedName("receivedbytes")
   private long receivedBytes;
   @SerializedName("sentbytes")
   private long sentBytes;
   @SerializedName("snapshotavailable")
   private Long snapshotsAvailable;
   @SerializedName("snapshotLimit")
   private Long snapshotLimit;
   @SerializedName("snapshottotal")
   private long snapshots;
   private State state;
   @SerializedName("templateavailable")
   private Long templatesAvailable;
   @SerializedName("templatelimit")
   private Long templateLimit;
   @SerializedName("templatetotal")
   private long templates;
   @SerializedName("vmavailable")
   private Long VMsAvailable;
   @SerializedName("vmlimit")
   private Long VMLimit;
   @SerializedName("vmrunning")
   private long VMsRunning;
   @SerializedName("vmstopped")
   private long VMsStopped;
   @SerializedName("vmtotal")
   private long VMs;
   @SerializedName("volumeavailable")
   private Long volumesAvailable;
   @SerializedName("volumelimit")
   private Long volumeLimit;
   @SerializedName("volumetotal")
   private long volumes;
   private Set<User> users;

   public Account(long id, Type type, String networkDomain, String domain, long domainId, Long IPsAvailable, Long IPLimit, long iPs,
                  boolean cleanupRequired, String name, long receivedBytes, long sentBytes, Long snapshotsAvailable,
                  Long snapshotLimit, long snapshots, org.jclouds.cloudstack.domain.Account.State state,
                  Long templatesAvailable, Long templateLimit, long templates, Long VMsAvailable, Long VMLimit, long vMsRunning,
                  long vMsStopped, long vMs, Long volumesAvailable, Long volumeLimit, long volumes, Set<User> users) {
      this.id = id;
      this.type = type;
      this.networkDomain = networkDomain;
      this.domain = domain;
      this.domainId = domainId;
      this.IPsAvailable = IPsAvailable;
      this.IPLimit = IPLimit;
      this.IPs = iPs;
      this.cleanupRequired = cleanupRequired;
      this.name = name;
      this.receivedBytes = receivedBytes;
      this.sentBytes = sentBytes;
      this.snapshotsAvailable = snapshotsAvailable;
      this.snapshotLimit = snapshotLimit;
      this.snapshots = snapshots;
      this.state = state;
      this.templatesAvailable = templatesAvailable;
      this.templateLimit = templateLimit;
      this.templates = templates;
      this.VMsAvailable = VMsAvailable;
      this.VMLimit = VMLimit;
      this.VMsRunning = vMsRunning;
      this.VMsStopped = vMsStopped;
      this.VMs = vMs;
      this.volumesAvailable = volumesAvailable;
      this.volumeLimit = volumeLimit;
      this.volumes = volumes;
      this.users = ImmutableSet.copyOf(checkNotNull(users, "users"));
   }

   /**
    * present only for serializer
    */
   Account() {

   }

   /**
    * @return the id of the account
    */
   public long getId() {
      return id;
   }

   /**
    * @return the name of the account
    */

   public String getName() {
      return name;
   }

   /**
    * @return account type (admin, domain-admin, user)
    */
   public Type getType() {
      return type;
   }

   /**
    * @return the network domain
    */
   public String getNetworkDomain() {
      return networkDomain;
   }

   /**
    * @return name of the Domain the account belongs to
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return id of the Domain the account belongs to
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return true if the account requires cleanup
    */
   public boolean isCleanupRequired() {
      return cleanupRequired;
   }

   /**
    * @return the list of users associated with account
    */
   public Set<User> getUsers() {
      return users;
   }

   /**
    * @return the total number of public ip addresses available for this account
    *         to acquire, or null if unlimited
    */
   @Nullable
   public Long getIPsAvailable() {
      return IPsAvailable;
   }

   /**
    * @return the total number of public ip addresses this account can acquire,
    *         or null if unlimited
    */
   @Nullable
   public Long getIPLimit() {
      return IPLimit;
   }

   /**
    * @return the total number of public ip addresses allocated for this account
    */
   public long getIPs() {
      return IPs;
   }

   /**
    * @return the total number of network traffic bytes received
    */
   public long getReceivedBytes() {
      return receivedBytes;
   }

   /**
    * @return the total number of network traffic bytes sent
    */
   public long getSentBytes() {
      return sentBytes;
   }

   /**
    * @return the total number of snapshots available for this account, or null
    *         if unlimited
    */
   @Nullable
   public Long getSnapshotsAvailable() {
      return snapshotsAvailable;
   }

   /**
    * @return the total number of snapshots which can be stored by this account,
    *         or null if unlimited
    */
   @Nullable
   public Long getSnapshotLimit() {
      return snapshotLimit;
   }

   /**
    * @return the total number of snapshots stored by this account
    */
   public long getSnapshots() {
      return snapshots;
   }

   /**
    * @return the state of the account
    */
   public State getState() {
      return state;
   }

   /**
    * @return the total number of templates available to be created by this
    *         account, or null if unlimited
    */
   @Nullable
   public Long getTemplatesAvailable() {
      return templatesAvailable;
   }

   /**
    * @return the total number of templates which can be created by this
    *         account, or null if unlimited
    */
   @Nullable
   public Long getTemplateLimit() {
      return templateLimit;
   }

   /**
    * @return the total number of templates which have been created by this
    *         account
    */
   public long getTemplates() {
      return templates;
   }

   /**
    * @return the total number of virtual machines available for this account to
    *         acquire, or null if unlimited
    */
   @Nullable
   public Long getVMsAvailable() {
      return VMsAvailable;
   }

   /**
    * @return the total number of virtual machines that can be deployed by this
    *         account, or null if unlimited
    */
   @Nullable
   public Long getVMLimit() {
      return VMLimit;
   }

   /**
    * @return the total number of virtual machines running for this account
    */
   public long getVMsRunning() {
      return VMsRunning;
   }

   /**
    * @return the total number of virtual machines stopped for this account
    */
   public long getVMsStopped() {
      return VMsStopped;
   }

   /**
    * @return the total number of virtual machines deployed by this account
    */
   public long getVMs() {
      return VMs;
   }

   /**
    * @return the total volume available for this account, or null if unlimited
    */
   @Nullable
   public Long getVolumesAvailable() {
      return volumesAvailable;
   }

   /**
    * @return the total volume which can be used by this account, or null if
    *         unlimited
    */
   @Nullable
   public Long getVolumeLimit() {
      return volumeLimit;
   }

   /**
    * @return the total volume being used by this account
    */
   public long getVolumes() {
      return volumes;
   }

   @Override
   public int compareTo(Account arg0) {
      return new Long(id).compareTo(arg0.getId());
   }


   @Override
   public String toString() {
      return "Account{" +
            "id=" + id +
            ", type=" + type +
            ", networkDomain='" + networkDomain + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", IPsAvailable=" + IPsAvailable +
            ", IPLimit=" + IPLimit +
            ", IPs=" + IPs +
            ", cleanupRequired=" + cleanupRequired +
            ", name='" + name + '\'' +
            ", receivedBytes=" + receivedBytes +
            ", sentBytes=" + sentBytes +
            ", snapshotsAvailable=" + snapshotsAvailable +
            ", snapshotLimit=" + snapshotLimit +
            ", snapshots=" + snapshots +
            ", state=" + state +
            ", templatesAvailable=" + templatesAvailable +
            ", templateLimit=" + templateLimit +
            ", templates=" + templates +
            ", VMsAvailable=" + VMsAvailable +
            ", VMLimit=" + VMLimit +
            ", VMsRunning=" + VMsRunning +
            ", VMsStopped=" + VMsStopped +
            ", VMs=" + VMs +
            ", volumesAvailable=" + volumesAvailable +
            ", volumeLimit=" + volumeLimit +
            ", volumes=" + volumes +
            ", users=" + users +
            '}';
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      Account other = (Account) obj;
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   protected Set<User> delegate() {
      return users;
   }
}
