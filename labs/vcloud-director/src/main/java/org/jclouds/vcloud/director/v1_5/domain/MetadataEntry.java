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
import com.google.common.collect.Sets;

@XmlRootElement(namespace = NS, name = "org/metadata")
public class MetadataEntry extends ResourceType<MetadataEntry> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMetadata(this);
   }

   public static class Builder extends ResourceType.Builder<MetadataEntry> {
      private String key;
      private String value;

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
      
      public MetadataEntry build() {
         MetadataEntry metadataEntry = new MetadataEntry(href, key, value);
         metadataEntry.setType(type);
         metadataEntry.setLinks(links);
         return metadataEntry;
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

      public Builder fromMetadata(MetadataEntry in) {
         return key(in.getKey()).value(in.getValue());
      }

   }

   private MetadataEntry() {
      // For JAXB and builder use
   }

   private MetadataEntry(URI href, String key, String value) {
      super(href);
      this.key = checkNotNull(key, "key");
      this.value = checkNotNull(value, "value");
   }

   @XmlAttribute
   private String key;
   @XmlElement(namespace = NS, name = "Value")
   private String value;

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
   

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      MetadataEntry that = MetadataEntry.class.cast(o);
      return super.equals(that) && equal(key, that.key);
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
