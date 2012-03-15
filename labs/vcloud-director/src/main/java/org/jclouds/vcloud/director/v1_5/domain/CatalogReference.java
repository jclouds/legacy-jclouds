package org.jclouds.vcloud.director.v1_5.domain;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CatalogReference")
public class CatalogReference extends ReferenceType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromReferenceType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends ReferenceType.Builder<B> {

      @Override
      public CatalogReference build() {
         return new CatalogReference(this);
      }

      protected B fromCatalogReference(CatalogReference in) {
         return fromReferenceType(in);
      }
   }

   public CatalogReference(Builder<?> builder) {
      super(builder);
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
   
   // Note: hashcode inheritted from ReferenceType
}
