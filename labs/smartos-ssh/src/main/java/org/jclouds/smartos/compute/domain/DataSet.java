package org.jclouds.smartos.compute.domain;

import java.util.UUID;

import com.google.common.base.Objects;

/**
 * Dataset is a pre-built image ready to be cloned.
 */
public class DataSet {
   private final UUID uuid;
   private final String os;
   private final String published;
   private final String urn;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromDataSet(this);
   }

   public static class Builder {

      public UUID uuid;
      public String os;
      public String published;
      public String urn;

      public Builder uuid(UUID uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = UUID.fromString(uuid);
         return this;
      }

      public Builder os(String os) {
         this.os = os;
         return this;
      }

      public Builder published(String published) {
         this.published = published;
         return this;
      }

      public Builder urn(String urn) {
         this.urn = urn;
         return this;
      }

      public Builder fromDsadmString(String string) {
         String[] sections = string.split(" ");

         uuid(sections[0]);
         os(sections[1]);
         published(sections[2]);
         urn(sections[3]);

         return this;
      }

      public DataSet build() {
         return new DataSet(uuid, os, published, urn);
      }

      public Builder fromDataSet(DataSet in) {
         return uuid(in.getUuid()).os(in.getOs()).published(in.getPublished()).urn(in.getUrn());
      }
   }

   protected DataSet(UUID uuid, String os, String published, String urn) {
      this.uuid = uuid;
      this.os = os;
      this.published = published;
      this.urn = urn;
   }

   public UUID getUuid() {
      return uuid;
   }

   public String getOs() {
      return os;
   }

   public String getPublished() {
      return published;
   }

   public String getUrn() {
      return urn;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      // UUID is primary key
      return uuid.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      return uuid.equals(((DataSet) obj).uuid);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("uuid", uuid).add("os", os).add("published", published)
               .add("urn", urn).toString();
   }

}
