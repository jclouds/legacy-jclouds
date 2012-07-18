package org.jclouds.smartos.compute.domain;


import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        protected String quota = "10";

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

        public Builder nics(Collection<VmNIC> nic) {
            this.nics.addAll(nics);
            return this;
        }

        public Builder nic(VmNIC nic) {
            this.nics.add(nic);
            return this;
        }

        public VmSpecification build() {
            return new VmSpecification(alias, brand, dataset, dnsDomain, quota, nics);
        }

        public Builder fromVmSpecification(VmSpecification in) {
            return alias     (in.getAlias())
                   .brand    (in.getBrand())
                   .dataset  (in.getDataset())
                   .dnsDomain(in.getDnsDomain())
                   .quota    (in.getQuota())
                   .nics(in.getNics());
        }
    }

    protected VmSpecification(String alias, String brand, DataSet dataset, String dnsDomain, String quota, List<VmNIC> nics) {
        this.alias = alias;
        this.brand = brand;
        this.dataset = dataset;
        this.dnsDomain = dnsDomain;
        this.quota = quota;
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

    public List<VmNIC> getNics() {
        return ImmutableList.copyOf(nics);
    }

    public String toJSONSpecification() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(DataSet.class, new FlattenDataset() );
        Gson g = gson.create();

        return g.toJson(this);
    }

    public class FlattenDataset implements JsonSerializer<DataSet>
    {
        @Override
        public JsonElement serialize(DataSet vmSpecification, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dataset.getUuid().toString());
        }
    }
}
