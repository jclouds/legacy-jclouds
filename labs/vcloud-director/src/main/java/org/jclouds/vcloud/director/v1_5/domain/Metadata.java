package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@XmlRootElement(namespace = NS, name = "MetaDataList")
public class Metadata {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMetadataList(this);
   }

   public static class Builder {

      private Set<MetadataEntry> metadata = Sets.newLinkedHashSet();

      /**
       * @see OrgList#getOrgs
       */
      public Builder metadata(Set<MetadataEntry> orgs) {
         this.metadata = Sets.newLinkedHashSet(checkNotNull(orgs, "metadata"));
         return this;
      }

      /**
       * @see OrgList#getOrgs
       */
      public Builder addMetadata(MetadataEntry org) {
         metadata.add(checkNotNull(org, "metadatum"));
         return this;
      }

      public Metadata build() {
         return new Metadata(metadata);
      }

      public Builder fromMetadataList(Metadata in) {
         return metadata(in.getMetadata());
      }
   }

   private Metadata() {
      // For JAXB and builder use
   }

   private Metadata(Set<MetadataEntry> orgs) {
      this.metadata = ImmutableSet.copyOf(orgs);
   }

   @XmlElement(namespace = NS, name = "MetaData")
   private Set<MetadataEntry> metadata = Sets.newLinkedHashSet();

   public Set<MetadataEntry> getMetadata() {
      return ImmutableSet.copyOf(metadata);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Metadata that = Metadata.class.cast(o);
      return equal(metadata, that.metadata);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(metadata);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("metadata", metadata).toString();
   }

}
