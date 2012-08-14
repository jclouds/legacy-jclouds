package org.jclouds.smartos.compute.domain;

import java.beans.ConstructorProperties;
import java.util.List;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Specification of a VM to build, based on a dataset.
 */
public class VmSpecification {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVmSpecification(this);
   }

   public static class Builder {

      private String alias;
      private String brand = "joyent";
      private DataSet dataset;
      private String dnsDomain = "local";
      private String quota = "0";

      private int maxPhysicalMemory = 256;
      private int maxLockedMemory = 256;
      private int maxSwap = 256;
      private int tmpFs = 256;

      private ImmutableList.Builder<VmNIC> nics = ImmutableList.<VmNIC> builder();

      public Builder alias(String alias) {
         this.alias = alias;
         return this;
      }

      public Builder brand(String brand) {
         this.brand = brand;
         return this;
      }

      public Builder dataset(DataSet dataset) {
         this.dataset = dataset;
         return this;
      }

      public Builder dnsDomain(String dnsDomain) {
         this.dnsDomain = dnsDomain;
         return this;
      }

      public Builder quota(String quota) {
         this.quota = quota;
         return this;
      }

      public Builder nics(Iterable<VmNIC> nics) {
         this.nics.addAll(nics);
         return this;
      }

      public Builder nic(VmNIC nic) {
         this.nics.add(nic);
         return this;
      }

      public Builder maxPhysicalMemory(int maxPhysicalMemory) {
         this.maxPhysicalMemory = maxPhysicalMemory;
         return this;
      }

      public Builder maxLockedMemory(int maxLockedMemory) {
         this.maxLockedMemory = maxLockedMemory;
         return this;
      }

      public Builder maxSwap(int maxSwap) {
         this.maxSwap = maxSwap;
         return this;
      }

      public Builder tmpFs(int tmpFs) {
         this.tmpFs = tmpFs;
         return this;
      }

      public Builder ram(int ram) {
         this.maxPhysicalMemory = ram;
         this.maxLockedMemory = ram;
         this.maxSwap = ram;
         this.tmpFs = ram;
         return this;
      }

      public VmSpecification build() {
         return new VmSpecification(alias, brand, dataset, dnsDomain, quota, maxPhysicalMemory, maxLockedMemory,
                  maxSwap, tmpFs, nics.build());
      }

      public Builder fromVmSpecification(VmSpecification in) {
         return alias(in.getAlias()).brand(in.getBrand()).dataset(in.getDataset()).dnsDomain(in.getDnsDomain())
                  .quota(in.getQuota()).maxPhysicalMemory(in.getMaxPhysicalMemory())
                  .maxLockedMemory(in.getMaxLockedMemory()).maxSwap(in.getMaxSwap()).tmpFs(in.getTmpFs())
                  .nics(in.getNics());
      }
   }

   private final String alias;
   private final String brand;
   @Named("dataset_uuid")
   private final DataSet dataset;
   @Named("dns_domain")
   private final String dnsDomain;
   private final String quota;
   @Named("max_physical_memory")
   private final int maxPhysicalMemory;
   @Named("max_locked_memory")
   private final int maxLockedMemory;
   @Named("max_swap")
   private final int maxSwap;
   @Named("tmpfs")
   private final int tmpFs;
   private final List<VmNIC> nics;

   @ConstructorProperties({ "alias", "brand", "dataset_uuid", "dns_domain", "quota", "max_physical_memory",
            "max_locked_memory", "max_swap", "tmpfs", "nics" })
   protected VmSpecification(String alias, String brand, DataSet dataset, String dnsDomain, String quota,
            int maxPhysicalMemory, int maxLockedMemory, int maxSwap, int tmpFs, List<VmNIC> nics) {
      this.alias = alias;
      this.brand = brand;
      this.dataset = dataset;
      this.dnsDomain = dnsDomain;
      this.quota = quota;
      this.maxPhysicalMemory = maxPhysicalMemory;
      this.maxLockedMemory = maxLockedMemory;
      this.maxSwap = maxSwap;
      this.tmpFs = tmpFs;
      this.nics = ImmutableList.copyOf(nics);
   }

   public String getAlias() {
      return alias;
   }

   public String getBrand() {
      return brand;
   }

   public DataSet getDataset() {
      return dataset;
   }

   public String getDnsDomain() {
      return dnsDomain;
   }

   public String getQuota() {
      return quota;
   }

   public int getMaxPhysicalMemory() {
      return maxPhysicalMemory;
   }

   public int getMaxLockedMemory() {
      return maxLockedMemory;
   }

   public int getMaxSwap() {
      return maxSwap;
   }

   public int getTmpFs() {
      return tmpFs;
   }

   public List<VmNIC> getNics() {
      return nics;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(alias, brand, dataset, dnsDomain, quota, maxPhysicalMemory, maxLockedMemory, maxSwap,
               tmpFs, nics);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      VmSpecification that = VmSpecification.class.cast(obj);
      return Objects.equal(this.alias, that.alias) && Objects.equal(this.brand, that.brand)
               && Objects.equal(this.dataset, that.dataset) && Objects.equal(this.dnsDomain, that.dnsDomain)
               && Objects.equal(this.quota, that.quota)
               && Objects.equal(this.maxPhysicalMemory, that.maxPhysicalMemory)
               && Objects.equal(this.maxLockedMemory, that.maxLockedMemory)
               && Objects.equal(this.maxSwap, that.maxSwap) && Objects.equal(this.tmpFs, that.tmpFs)
               && Objects.equal(this.nics, that.nics);

   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("alias", alias).add("brand", brand)
               .add("dataset_uuid", dataset != null ? dataset.getUuid() : null).add("dns_domain", dnsDomain)
               .add("quota", quota).add("max_physical_memory", maxPhysicalMemory)
               .add("max_locked_memory", maxLockedMemory).add("max_swap", maxSwap).add("tmpfs", tmpFs)
               .add("nics", nics).toString();
   }
}
