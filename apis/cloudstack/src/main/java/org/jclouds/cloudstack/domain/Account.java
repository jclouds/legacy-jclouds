/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Class Account
 *
 * @author Adrian Cole
 */
public class Account extends ForwardingSet<User> {

   /**
    */
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

   /**
    */
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
         Integer code = Integer.valueOf(checkNotNull(type, "type"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAccount(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected Account.Type type;
      protected String networkDomain;
      protected String domain;
      protected String domainId;
      protected Long IPsAvailable;
      protected Long IPLimit;
      protected long IPs;
      protected boolean cleanupRequired;
      protected String name;
      protected long receivedBytes;
      protected long sentBytes;
      protected Long snapshotsAvailable;
      protected Long snapshotLimit;
      protected long snapshots;
      protected Account.State state;
      protected Long templatesAvailable;
      protected Long templateLimit;
      protected long templates;
      protected Long VMsAvailable;
      protected Long VMLimit;
      protected long VMsRunning;
      protected long VMsStopped;
      protected long VMs;
      protected Long volumesAvailable;
      protected Long volumeLimit;
      protected long volumes;
      protected Set<User> users = ImmutableSet.of();

      /**
       * @see Account#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Account#getType()
       */
      public T type(Account.Type type) {
         this.type = type;
         return self();
      }

      /**
       * @see Account#getNetworkDomain()
       */
      public T networkDomain(String networkDomain) {
         this.networkDomain = networkDomain;
         return self();
      }

      /**
       * @see Account#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Account#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Account#getIPsAvailable()
       */
      public T IPsAvailable(Long IPsAvailable) {
         this.IPsAvailable = IPsAvailable;
         return self();
      }

      /**
       * @see Account#getIPLimit()
       */
      public T IPLimit(Long IPLimit) {
         this.IPLimit = IPLimit;
         return self();
      }

      /**
       * @see Account#getIPs()
       */
      public T IPs(long IPs) {
         this.IPs = IPs;
         return self();
      }

      /**
       * @see Account#isCleanupRequired()
       */
      public T cleanupRequired(boolean cleanupRequired) {
         this.cleanupRequired = cleanupRequired;
         return self();
      }

      /**
       * @see Account#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Account#getReceivedBytes()
       */
      public T receivedBytes(long receivedBytes) {
         this.receivedBytes = receivedBytes;
         return self();
      }

      /**
       * @see Account#getSentBytes()
       */
      public T sentBytes(long sentBytes) {
         this.sentBytes = sentBytes;
         return self();
      }

      /**
       * @see Account#getSnapshotsAvailable()
       */
      public T snapshotsAvailable(Long snapshotsAvailable) {
         this.snapshotsAvailable = snapshotsAvailable;
         return self();
      }

      /**
       * @see Account#getSnapshotLimit()
       */
      public T snapshotLimit(Long snapshotLimit) {
         this.snapshotLimit = snapshotLimit;
         return self();
      }

      /**
       * @see Account#getSnapshots()
       */
      public T snapshots(long snapshots) {
         this.snapshots = snapshots;
         return self();
      }

      /**
       * @see Account#getState()
       */
      public T state(Account.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see Account#getTemplatesAvailable()
       */
      public T templatesAvailable(Long templatesAvailable) {
         this.templatesAvailable = templatesAvailable;
         return self();
      }

      /**
       * @see Account#getTemplateLimit()
       */
      public T templateLimit(Long templateLimit) {
         this.templateLimit = templateLimit;
         return self();
      }

      /**
       * @see Account#getTemplates()
       */
      public T templates(long templates) {
         this.templates = templates;
         return self();
      }

      /**
       * @see Account#getVMsAvailable()
       */
      public T VMsAvailable(Long VMsAvailable) {
         this.VMsAvailable = VMsAvailable;
         return self();
      }

      /**
       * @see Account#getVMLimit()
       */
      public T VMLimit(Long VMLimit) {
         this.VMLimit = VMLimit;
         return self();
      }

      /**
       * @see Account#getVMsRunning()
       */
      public T VMsRunning(long VMsRunning) {
         this.VMsRunning = VMsRunning;
         return self();
      }

      /**
       * @see Account#getVMsStopped()
       */
      public T VMsStopped(long VMsStopped) {
         this.VMsStopped = VMsStopped;
         return self();
      }

      /**
       * @see Account#getVMs()
       */
      public T VMs(long VMs) {
         this.VMs = VMs;
         return self();
      }

      /**
       * @see Account#getVolumesAvailable()
       */
      public T volumesAvailable(Long volumesAvailable) {
         this.volumesAvailable = volumesAvailable;
         return self();
      }

      /**
       * @see Account#getVolumeLimit()
       */
      public T volumeLimit(Long volumeLimit) {
         this.volumeLimit = volumeLimit;
         return self();
      }

      /**
       * @see Account#getVolumes()
       */
      public T volumes(long volumes) {
         this.volumes = volumes;
         return self();
      }

      /**
       * @see Account#getUsers()
       */
      public T users(Set<User> users) {
         this.users = ImmutableSet.copyOf(checkNotNull(users, "users"));
         return self();
      }

      public T users(User... in) {
         return users(ImmutableSet.copyOf(in));
      }

      public Account build() {
         return new Account(id, type, networkDomain, domain, domainId, IPsAvailable, IPLimit, IPs, cleanupRequired, name, receivedBytes, sentBytes, snapshotsAvailable, snapshotLimit, snapshots, state, templatesAvailable, templateLimit, templates, VMsAvailable, VMLimit, VMsRunning, VMsStopped, VMs, volumesAvailable, volumeLimit, volumes, users);
      }

      public T fromAccount(Account in) {
         return this
               .id(in.getId())
               .type(in.getType())
               .networkDomain(in.getNetworkDomain())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .IPsAvailable(in.getIPsAvailable())
               .IPLimit(in.getIPLimit())
               .IPs(in.getIPs())
               .cleanupRequired(in.isCleanupRequired())
               .name(in.getName())
               .receivedBytes(in.getReceivedBytes())
               .sentBytes(in.getSentBytes())
               .snapshotsAvailable(in.getSnapshotsAvailable())
               .snapshotLimit(in.getSnapshotLimit())
               .snapshots(in.getSnapshots())
               .state(in.getState())
               .templatesAvailable(in.getTemplatesAvailable())
               .templateLimit(in.getTemplateLimit())
               .templates(in.getTemplates())
               .VMsAvailable(in.getVMsAvailable())
               .VMLimit(in.getVMLimit())
               .VMsRunning(in.getVMsRunning())
               .VMsStopped(in.getVMsStopped())
               .VMs(in.getVMs())
               .volumesAvailable(in.getVolumesAvailable())
               .volumeLimit(in.getVolumeLimit())
               .volumes(in.getVolumes())
               .users(in.getUsers());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final Account.Type type;
   private final String networkDomain;
   private final String domain;
   private final String domainId;
   private final Long IPsAvailable;
   private final Long IPLimit;
   private final long IPs;
   private final boolean cleanupRequired;
   private final String name;
   private final long receivedBytes;
   private final long sentBytes;
   private final Long snapshotsAvailable;
   private final Long snapshotLimit;
   private final long snapshots;
   private final Account.State state;
   private final Long templatesAvailable;
   private final Long templateLimit;
   private final long templates;
   private final Long VMsAvailable;
   private final Long VMLimit;
   private final long VMsRunning;
   private final long VMsStopped;
   private final long VMs;
   private final Long volumesAvailable;
   private final Long volumeLimit;
   private final long volumes;
   private final Set<User> users;

   @ConstructorProperties({
         "id", "accounttype", "networkdomain", "domain", "domainid", "ipavailable", "iplimit", "iptotal", "iscleanuprequired",
         "name", "receivedbytes", "sentbytes", "snapshotavailable", "snapshotlimit", "snapshottotal", "state", "templateavailable",
         "templatelimit", "templatetotal", "vmavailable", "vmlimit", "vmrunning", "vmstopped", "vmtotal", "volumeavailable", "volumelimit",
         "volumetotal", "user"
   })
   private Account(String id, @Nullable Type type, @Nullable String networkDomain, @Nullable String domain,
                   @Nullable String domainId, @Nullable String IPsAvailable, @Nullable String IPLimit, long IPs,
                   boolean cleanupRequired, @Nullable String name, long receivedBytes, long sentBytes,
                   @Nullable String snapshotsAvailable, @Nullable String snapshotLimit, long snapshots,
                   @Nullable State state, @Nullable String templatesAvailable, @Nullable String templateLimit,
                   long templates, @Nullable String VMsAvailable, @Nullable String VMLimit, long VMsRunning,
                   long VMsStopped, long VMs, @Nullable String volumesAvailable, @Nullable String volumeLimit,
                   long volumes, @Nullable Set<User> users) {
      this(id, type, networkDomain, domain, domainId, toLongNullIfUnlimited(IPsAvailable), toLongNullIfUnlimited(IPLimit), IPs,
            cleanupRequired, name, receivedBytes, sentBytes, toLongNullIfUnlimited(snapshotsAvailable), toLongNullIfUnlimited(snapshotLimit),
            snapshots, state, toLongNullIfUnlimited(templatesAvailable), toLongNullIfUnlimited(templateLimit), templates,
            toLongNullIfUnlimited(VMsAvailable), toLongNullIfUnlimited(VMLimit), VMsRunning, VMsStopped, VMs,
            toLongNullIfUnlimited(volumesAvailable), toLongNullIfUnlimited(volumeLimit), volumes, users);
   }

   private static Long toLongNullIfUnlimited(String in) {
      return in == null || "Unlimited".equals(in) ? null : Long.valueOf(in);
   }

   protected Account(String id, @Nullable Account.Type type, @Nullable String networkDomain, @Nullable String domain,
                     @Nullable String domainId, @Nullable Long IPsAvailable, @Nullable Long IPLimit, long IPs,
                     boolean cleanupRequired, @Nullable String name, long receivedBytes, long sentBytes, @Nullable Long snapshotsAvailable,
                     @Nullable Long snapshotLimit, long snapshots, @Nullable Account.State state, @Nullable Long templatesAvailable,
                     @Nullable Long templateLimit, long templates, @Nullable Long VMsAvailable, @Nullable Long VMLimit, long VMsRunning,
                     long VMsStopped, long VMs, @Nullable Long volumesAvailable, @Nullable Long volumeLimit, long volumes,
                     @Nullable Set<User> users) {
      this.id = checkNotNull(id, "id");
      this.type = type;
      this.networkDomain = networkDomain;
      this.domain = domain;
      this.domainId = domainId;
      this.IPsAvailable = IPsAvailable;
      this.IPLimit = IPLimit;
      this.IPs = IPs;
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
      this.VMsRunning = VMsRunning;
      this.VMsStopped = VMsStopped;
      this.VMs = VMs;
      this.volumesAvailable = volumesAvailable;
      this.volumeLimit = volumeLimit;
      this.volumes = volumes;
      this.users = users == null ? ImmutableSet.<User>of() : ImmutableSet.copyOf(users);
   }

   /**
    * @return the id of the account
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return account type (admin, domain-admin, user)
    */
   @Nullable
   public Account.Type getType() {
      return this.type;
   }

   /**
    * @return the network domain
    */
   @Nullable
   public String getNetworkDomain() {
      return this.networkDomain;
   }

   /**
    * @return name of the Domain the account belongs to
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return id of the Domain the account belongs to
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the total number of public ip addresses available for this account
    *         to acquire, or null if unlimited
    */
   @Nullable
   public Long getIPsAvailable() {
      return this.IPsAvailable;
   }

   /**
    * @return the total number of public ip addresses this account can acquire,
    *         or null if unlimited
    */
   @Nullable
   public Long getIPLimit() {
      return this.IPLimit;
   }

   /**
    * @return the total number of public ip addresses allocated for this account
    */
   public long getIPs() {
      return this.IPs;
   }

   /**
    * @return true if the account requires cleanup
    */
   public boolean isCleanupRequired() {
      return this.cleanupRequired;
   }

   /**
    * @return the name of the account
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the total number of network traffic bytes received
    */
   public long getReceivedBytes() {
      return this.receivedBytes;
   }

   /**
    * @return the total number of network traffic bytes sent
    */
   public long getSentBytes() {
      return this.sentBytes;
   }

   /**
    * @return the total number of snapshots available for this account, or null
    *         if unlimited
    */
   @Nullable
   public Long getSnapshotsAvailable() {
      return this.snapshotsAvailable;
   }

   /**
    * @return the total number of snapshots which can be stored by this account,
    *         or null if unlimited
    */
   @Nullable
   public Long getSnapshotLimit() {
      return this.snapshotLimit;
   }

   /**
    * @return the total number of snapshots stored by this account
    */
   public long getSnapshots() {
      return this.snapshots;
   }

   /**
    * @return the state of the account
    */
   @Nullable
   public State getState() {
      return this.state;
   }

   /**
    * @return the total number of templates available to be created by this
    *         account, or null if unlimited
    */
   @Nullable
   public Long getTemplatesAvailable() {
      return this.templatesAvailable;
   }

   /**
    * @return the total number of templates which can be created by this
    *         account, or null if unlimited
    */
   @Nullable
   public Long getTemplateLimit() {
      return this.templateLimit;
   }

   /**
    * @return the total number of templates which have been created by this
    *         account
    */
   public long getTemplates() {
      return this.templates;
   }

   /**
    * @return the total number of virtual machines available for this account to
    *         acquire, or null if unlimited
    */
   @Nullable
   public Long getVMsAvailable() {
      return this.VMsAvailable;
   }

   /**
    * @return the total number of virtual machines that can be deployed by this
    *         account, or null if unlimited
    */
   @Nullable
   public Long getVMLimit() {
      return this.VMLimit;
   }

   /**
    * @return the total number of virtual machines running for this account
    */
   public long getVMsRunning() {
      return this.VMsRunning;
   }

   /**
    * @return the total number of virtual machines stopped for this account
    */
   public long getVMsStopped() {
      return this.VMsStopped;
   }

   /**
    * @return the total number of virtual machines deployed by this account
    */
   public long getVMs() {
      return this.VMs;
   }

   /**
    * @return the total volume available for this account, or null if unlimited
    */
   @Nullable
   public Long getVolumesAvailable() {
      return this.volumesAvailable;
   }

   /**
    * @return the total volume which can be used by this account, or null if
    *         unlimited
    */
   @Nullable
   public Long getVolumeLimit() {
      return this.volumeLimit;
   }

   /**
    * @return the total volume being used by this account
    */
   public long getVolumes() {
      return this.volumes;
   }

   /**
    * @return the list of users associated with account
    */
   public Set<User> getUsers() {
      return this.users;
   }

   @Override
   protected Set<User> delegate() {
      return this.users;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, type, networkDomain, domain, domainId, IPsAvailable, IPLimit, IPs, cleanupRequired, name, receivedBytes, sentBytes, snapshotsAvailable, snapshotLimit, snapshots, state, templatesAvailable, templateLimit, templates, VMsAvailable, VMLimit, VMsRunning, VMsStopped, VMs, volumesAvailable, volumeLimit, volumes, users);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Account that = Account.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.networkDomain, that.networkDomain)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.IPsAvailable, that.IPsAvailable)
            && Objects.equal(this.IPLimit, that.IPLimit)
            && Objects.equal(this.IPs, that.IPs)
            && Objects.equal(this.cleanupRequired, that.cleanupRequired)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.receivedBytes, that.receivedBytes)
            && Objects.equal(this.sentBytes, that.sentBytes)
            && Objects.equal(this.snapshotsAvailable, that.snapshotsAvailable)
            && Objects.equal(this.snapshotLimit, that.snapshotLimit)
            && Objects.equal(this.snapshots, that.snapshots)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.templatesAvailable, that.templatesAvailable)
            && Objects.equal(this.templateLimit, that.templateLimit)
            && Objects.equal(this.templates, that.templates)
            && Objects.equal(this.VMsAvailable, that.VMsAvailable)
            && Objects.equal(this.VMLimit, that.VMLimit)
            && Objects.equal(this.VMsRunning, that.VMsRunning)
            && Objects.equal(this.VMsStopped, that.VMsStopped)
            && Objects.equal(this.VMs, that.VMs)
            && Objects.equal(this.volumesAvailable, that.volumesAvailable)
            && Objects.equal(this.volumeLimit, that.volumeLimit)
            && Objects.equal(this.volumes, that.volumes)
            && Objects.equal(this.users, that.users);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("type", type).add("networkDomain", networkDomain).add("domain", domain).add("domainId", domainId).add("IPsAvailable", IPsAvailable).add("IPLimit", IPLimit).add("IPs", IPs).add("cleanupRequired", cleanupRequired).add("name", name).add("receivedBytes", receivedBytes).add("sentBytes", sentBytes).add("snapshotsAvailable", snapshotsAvailable).add("snapshotLimit", snapshotLimit).add("snapshots", snapshots).add("state", state).add("templatesAvailable", templatesAvailable).add("templateLimit", templateLimit).add("templates", templates).add("VMsAvailable", VMsAvailable).add("VMLimit", VMLimit).add("VMsRunning", VMsRunning).add("VMsStopped", VMsStopped).add("VMs", VMs).add("volumesAvailable", volumesAvailable).add("volumeLimit", volumeLimit).add("volumes", volumes).add("users", users);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
