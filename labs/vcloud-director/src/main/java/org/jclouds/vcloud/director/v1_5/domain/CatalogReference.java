package org.jclouds.vcloud.director.v1_5.domain;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CatalogReference")
public class CatalogReference extends ReferenceType<CatalogReference> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromCatalogReference(this);
   }

   public static class Builder extends ReferenceType.Builder<CatalogReference> {

      @Override
      public CatalogReference build() {
         return new CatalogReference(href, id, name, type);
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ReferenceType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      @Override
      protected Builder fromReferenceType(ReferenceType<CatalogReference> in) {
         return Builder.class.cast(super.fromReferenceType(in));
      }

      protected Builder fromCatalogReference(CatalogReference in) {
         return fromReferenceType(in);
      }
   }

   public CatalogReference(URI href, String id, String name, String type) {
      super(href, id, name, type);
   }

   protected CatalogReference() {
      // For JAXB
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CatalogReference that = CatalogReference.class.cast(o);
      return super.equals(that);
   }
}
