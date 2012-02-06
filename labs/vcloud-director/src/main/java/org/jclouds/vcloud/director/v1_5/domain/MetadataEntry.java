package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@XmlRootElement(namespace = NS, name = "org/metadata")
public class MetadataEntry extends BaseResource<MetadataEntry> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMetadatum(this);
   }

   public static class Builder extends BaseResource.Builder<MetadataEntry> {

      private String key;
      private String value;
      private Set<Link> links = Sets.newLinkedHashSet();

      /**
       * @see MetadataEntry#getKey
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see MetadataEntry#getValue
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }
      
      /**
       * @see MetadataEntry#?
       */
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see MetadataEntry#?
       */
      public Builder addLink(Link org) {
         links.add(checkNotNull(org, "org"));
         return this;
      }

      public MetadataEntry build() {
         return new MetadataEntry(href, type, key, value, links);
      }

      public Builder fromMetadatum(MetadataEntry in) {
         return key(in.getKey()).value(in.getValue());
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

   }

   private MetadataEntry() {
      // For JAXB and builder use
   }

   private MetadataEntry(URI href, String type, String key, String value, Set<Link> links) {
      super(href, type);
      this.key = key;
      this.value = value;
      this.links = ImmutableSet.copyOf(links);
   }

   @XmlAttribute
   private String key;
   @XmlElement(namespace = NS, name = "Value")
   private String value;
   @XmlElement(namespace = NS, name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();

   /**
    * 
    * @return key of the entry
    */
   public String getKey() {
      return key;
   }

   /**
    * 
    * @return value of the entry
    */
   public String getValue() {
      return value;
   }
   
   /**
    * TODO
    */
   public Set<Link> getLinks() {
      return ImmutableSet.copyOf(links);
   }


   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      MetadataEntry that = MetadataEntry.class.cast(o);
      return equal(key, that.key);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(key);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("key", key).add("value", value);
   }
}
