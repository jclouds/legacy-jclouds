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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Represents the set of limits (quotas) returned by the Quota Extension
 *
 * @see org.jclouds.openstack.nova.v1_1.extensions.QuotaClient
 */
public class Quotas {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromQuotas(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String id;
      private int metadataItems;
      private int injectedFileContentBytes;
      private int volumes;
      private int gigabytes;
      private int ram;
      private int floatingIps;
      private int instances;
      private int injectedFiles;
      private int cores;
      private int securityGroups;
      private int securityGroupRules;
      private int keyPairs;

      /**
       * @see Quotas#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Quotas#getMetadataItems()
       */
      public T metadataItems(int metadataItems) {
         this.metadataItems = metadataItems;
         return self();
      }

      /**
       * @see Quotas#getInjectedFileContentBytes()
       */
      public T injectedFileContentBytes(int injectedFileContentBytes) {
         this.injectedFileContentBytes = injectedFileContentBytes;
         return self();
      }

      /**
       * @see Quotas#getVolumes()
       */
      public T volumes(int volumes) {
         this.volumes = volumes;
         return self();
      }

      /**
       * @see Quotas#getGigabytes()
       */
      public T gigabytes(int gigabytes) {
         this.gigabytes = gigabytes;
         return self();
      }

      /**
       * @see Quotas#getRam()
       */
      public T ram(int ram) {
         this.ram = ram;
         return self();
      }

      /**
       * @see Quotas#getFloatingIps()
       */
      public T floatingIps(int floatingIps) {
         this.floatingIps = floatingIps;
         return self();
      }

      /**
       * @see Quotas#getInstances()
       */
      public T instances(int instances) {
         this.instances = instances;
         return self();
      }

      /**
       * @see Quotas#getInjectedFiles()
       */
      public T injectedFiles(int injectedFiles) {
         this.injectedFiles = injectedFiles;
         return self();
      }

      /**
       * @see Quotas#getCores()
       */
      public T cores(int cores) {
         this.cores = cores;
         return self();
      }

      /**
       * @see Quotas#getSecurityGroups()
       */
      public T securityGroups(int securityGroups) {
         this.securityGroups = securityGroups;
         return self();
      }

      /**
       * @see Quotas#getSecurityGroupRules()
       */
      public T securityGroupRules(int securityGroupRules) {
         this.securityGroupRules = securityGroupRules;
         return self();
      }

      /**
       * @see Quotas#getKeyPairs()
       */
      public T keyPairs(int keyPairs) {
         this.keyPairs = keyPairs;
         return self();
      }

      public Quotas build() {
         return new Quotas(this);
      }

      public T fromQuotas(Quotas in) {
         return this.id(in.getId())
               .metadataItems(in.getMetadataItems())
               .injectedFileContentBytes(in.getInjectedFileContentBytes())
               .volumes(in.getVolumes())
               .gigabytes(in.getGigabytes())
               .ram(in.getRam())
               .floatingIps(in.getFloatingIps())
               .instances(in.getInstances())
               .injectedFiles(in.getInjectedFiles())
               .cores(in.getCores())
               .securityGroups(in.getSecurityGroups())
               .securityGroupRules(in.getSecurityGroupRules())
               .keyPairs(in.getKeyPairs());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected Quotas() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   @SerializedName("id")
   private String id;
   @SerializedName("metadata_items")
   private int metadataItems;
   @SerializedName("injected_file_content_bytes")
   private int injectedFileContentBytes;
   private int volumes;
   private int gigabytes;
   private int ram;
   @SerializedName("floating_ips")
   private int floatingIps;
   private int instances;
   @SerializedName("injected_files")
   private int injectedFiles;
   private int cores;
   @SerializedName("security_groups")
   private int securityGroups;
   @SerializedName("security_group_rules")
   private int securityGroupRules;
   @SerializedName("key_pairs")
   private int keyPairs;

   protected Quotas(Builder<?> builder) {
      this.id = checkNotNull(builder.id, "id");
      this.metadataItems = checkNotNull(builder.metadataItems, "metadataItems");
      this.injectedFileContentBytes = checkNotNull(builder.injectedFileContentBytes, "injectedFileContentBytes");
      this.volumes = checkNotNull(builder.volumes, "volumes");
      this.gigabytes = checkNotNull(builder.gigabytes, "gigabytes");
      this.ram = checkNotNull(builder.ram, "ram");
      this.floatingIps = checkNotNull(builder.floatingIps, "floatingIps");
      this.instances = checkNotNull(builder.instances, "instances");
      this.injectedFiles = checkNotNull(builder.injectedFiles, "injectedFiles");
      this.cores = checkNotNull(builder.cores, "cores");
      this.securityGroups = checkNotNull(builder.securityGroups, "securityGroups");
      this.securityGroupRules = checkNotNull(builder.securityGroupRules, "securityGroupRules");
      this.keyPairs = checkNotNull(builder.keyPairs, "keyPairs");
   }

   /**
    * The id of the tenant this set of limits applies to
    */
   public String getId() {
      return this.id;
   }

   /**
    * The limit of the number of metadata items for the tenant
    */
   public int getMetadataItems() {
      return this.metadataItems;
   }

   public int getInjectedFileContentBytes() {
      return this.injectedFileContentBytes;
   }

   /**
    * The limit of the number of volumes that can be created for the tenant
    */
   public int getVolumes() {
      return this.volumes;
   }

   /**
    * The limit of the total size of all volumes for the tenant
    */
   public int getGigabytes() {
      return this.gigabytes;
   }

   /**
    * The limit of total ram available to the tenant
    */
   public int getRam() {
      return this.ram;
   }

   /**
    * The limit of the number of floating ips for the tenant
    */
   public int getFloatingIps() {
      return this.floatingIps;
   }

   /**
    * The limit of the number of instances that can be created for the tenant
    */
   public int getInstances() {
      return this.instances;
   }

   public int getInjectedFiles() {
      return this.injectedFiles;
   }

   /**
    * The limit of the number of cores that can be used by the tenant
    */
   public int getCores() {
      return this.cores;
   }

   /**
    * @return the limit of the number of security groups that can be created for the tenant
    *
    * @see org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupClient
    */
   public int getSecurityGroups() {
      return this.securityGroups;
   }

   /**
    * @return the limit of the number of security group rules that can be created for the tenant
    *
    * @see org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupClient
    */
   public int getSecurityGroupRules() {
      return this.securityGroupRules;
   }

   /**
    * @return the limit of the number of key pairs that can be created for the tenant
    *
    * @see org.jclouds.openstack.nova.v1_1.extensions.KeyPairClient
    */
   public int getKeyPairs() {
      return this.keyPairs;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, metadataItems, injectedFileContentBytes, volumes, gigabytes, ram, floatingIps, instances, injectedFiles, cores, securityGroups, securityGroupRules, keyPairs);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Quotas that = Quotas.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.metadataItems, that.metadataItems)
            && Objects.equal(this.injectedFileContentBytes, that.injectedFileContentBytes)
            && Objects.equal(this.volumes, that.volumes)
            && Objects.equal(this.gigabytes, that.gigabytes)
            && Objects.equal(this.ram, that.ram)
            && Objects.equal(this.floatingIps, that.floatingIps)
            && Objects.equal(this.instances, that.instances)
            && Objects.equal(this.injectedFiles, that.injectedFiles)
            && Objects.equal(this.cores, that.cores)
            && Objects.equal(this.securityGroups, that.securityGroups)
            && Objects.equal(this.securityGroupRules, that.securityGroupRules)
            && Objects.equal(this.keyPairs, that.keyPairs);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("id", id)
            .add("metadataItems", metadataItems)
            .add("injectedFileContentBytes", injectedFileContentBytes)
            .add("volumes", volumes)
            .add("gigabytes", gigabytes)
            .add("ram", ram)
            .add("floatingIps", floatingIps)
            .add("instances", instances)
            .add("injectedFiles", injectedFiles)
            .add("cores", cores)
            .add("securityGroups", securityGroups)
            .add("securityGroupRules", securityGroupRules)
            .add("keyPairs", keyPairs);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}