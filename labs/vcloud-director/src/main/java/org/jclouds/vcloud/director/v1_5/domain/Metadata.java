package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@XmlRootElement(namespace = NS, name = "Metadata")
public class Metadata extends ResourceType<Metadata>{

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMetadataList(this);
   }

   public static class Builder extends ResourceType.Builder<Metadata> {

      private Set<MetadataEntry> metadataEntries = Sets.newLinkedHashSet();

      /**
       * @see Metadata#getMetadata()
       */
      public Builder metadata(Set<MetadataEntry> metadataEntries) {
         this.metadataEntries = Sets.newLinkedHashSet(checkNotNull(metadataEntries, "metadataEntries"));
         return this;
      }

      /**
       * @see Metadata#getMetadata()
       */
      public Builder entry(MetadataEntry metadataEntry) {
         metadataEntries.add(checkNotNull(metadataEntry, "metadataEntry"));
         return this;
      }

      public Metadata build() {
         Metadata metadata = new Metadata(href, metadataEntries);
         metadata.setType(type);
         metadata.setLinks(links);
         return metadata;
      }
      
      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      public Builder fromMetadataList(Metadata in) {
         return metadata(in.getMetadata());
      }
   }

   private Metadata() {
      // For JAXB and builder use
   }

   private Metadata(URI href, Set<MetadataEntry> metadataEntries) {
      super(href);
      this.metadata = ImmutableSet.copyOf(metadataEntries);
   }

   @XmlElement(namespace = NS, name = "MetadataEntry")
   private Set<MetadataEntry> metadata = Sets.newLinkedHashSet();

   public Set<MetadataEntry> getMetadata() {
      return ImmutableSet.copyOf(metadata);
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Metadata that = Metadata.class.cast(o);
      return super.equals(that) && equal(metadata, that.metadata);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(metadata);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("metadata", metadata);
   }

}
