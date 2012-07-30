package org.jclouds.smartos.compute.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

/**
 * Specification of a VM to build, based on a dataset.
 */
public class VmSpecification {
   protected final String alias;
   protected final String brand;

   @SerializedName("dataset_uuid")
   protected final DataSet dataset;
   protected final String dnsDomain;
   protected final String quota;

   @SerializedName("max_physical_memory")
   protected final int maxPhysicalMemory;

   @SerializedName("max_locked_memory")
   protected final int maxLockedMemory;

   @SerializedName("max_swap")
   protected final int maxSwap;

   @SerializedName("tmpfs")
   protected final int tmpFs;

   protected final List<VmNIC> nics;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVmSpecification(this);
   }

   public static class Builder {

      protected String alias;
      protected String brand = "joyent";
      protected DataSet dataset;
      protected String dnsDomain = "local";
      protected String quota = "0";

      protected int maxPhysicalMemory = 256;
      protected int maxLockedMemory = 256;
      protected int maxSwap = 256;
      protected int tmpFs = 256;

      protected List<VmNIC> nics = new ArrayList<VmNIC>();

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

      public Builder nics(Collection<VmNIC> nics) {
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
         return new VmSpecification(alias, brand, dataset, dnsDomain, quota, maxPhysicalMemory, maxLockedMemory, maxSwap, tmpFs, nics);
      }

      public Builder fromVmSpecification(VmSpecification in) {
         return alias(in.getAlias()).brand(in.getBrand()).dataset(in.getDataset()).dnsDomain(in.getDnsDomain())
                  .quota(in.getQuota()).maxPhysicalMemory(in.getMaxPhysicalMemory()).maxLockedMemory(in.getMaxLockedMemory())
                  .maxSwap(in.getMaxSwap()).tmpFs(in.getTmpFs()).nics(in.getNics());
      }
   }

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
      this.nics = nics;
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
      return ImmutableList.copyOf(nics);
   }

   public String toJSONSpecification() {
      GsonBuilder gson = new GsonBuilder();
      gson.registerTypeAdapter(DataSet.class, new FlattenDataset());
      Gson g = gson.create();

      return g.toJson(this);
   }

   public class FlattenDataset implements JsonSerializer<DataSet> {
      @Override
      public JsonElement serialize(DataSet vmSpecification, Type type, JsonSerializationContext jsonSerializationContext) {
         return new JsonPrimitive(dataset.getUuid().toString());
      }
   }
}
