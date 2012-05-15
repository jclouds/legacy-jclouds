package org.jclouds.joyent.sdc.v6_5.domain;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Listing of a dataset.
 * 
 * @author Gerald Pereira
 * @see <a href= "http://apidocs.joyent.com/sdcapidoc/cloudapi/#datasets" />
 */
public class Dataset implements Comparable<Dataset> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private Type type;
      private String version;
      private String urn;
      private boolean isDefault;
      private Date created;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder version(String version) {
         this.version = version;
         return this;
      }

      public Builder urn(String urn) {
         this.urn = urn;
         return this;
      }

      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Dataset build() {
         return new Dataset(id, name, type, version, urn, isDefault, created);
      }

      public Builder fromDataset(Dataset in) {
         return id(in.getId()).name(in.getName()).type(in.getType()).version(in.getVersion()).urn(in.getUrn())
               .isDefault(in.isDefault()).created(in.getCreated());
      }
   }

   // The globally unique id for this dataset
   protected final String id;
   // The "friendly" name for this dataset
   protected final String name;
   // Whether this is a smartmachine or virtualmachine
   protected final Type type;
   // The version for this dataset
   protected final String version;
   // The full URN for this dataset
   protected final String urn;
   // Whether this is the default dataset in this datacenter
   @SerializedName("default")
   protected final boolean isDefault;
   // Date (ISO8601) When this dataset was created
   protected final Date created;

   public Dataset(String id, String name, Type type, String version, String urn, boolean isDefault, Date created) {
      super();
      this.id = id;
      this.name = name;
      this.type = type;
      this.version = version;
      this.urn = urn;
      this.isDefault = isDefault;
      this.created = created;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public Type getType() {
      return type;
   }

   public String getVersion() {
      return version;
   }

   public String getUrn() {
      return urn;
   }

   public boolean isDefault() {
      return isDefault;
   }

   public Date getCreated() {
      return created;
   }

   @Override
   public int compareTo(Dataset other) {
      return id.compareTo(other.getId());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Machine) {
         return Objects.equal(id, ((Machine) object).id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public String toString() {
      return String.format("[id=%s, name=%s, type=%s, version=%s, urn=%s, default=%s, created=%s]", id, name,
            type.name(), type.name(), version, urn, isDefault, created);
   }
}
