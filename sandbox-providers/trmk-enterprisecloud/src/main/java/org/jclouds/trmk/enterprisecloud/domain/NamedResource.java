package org.jclouds.trmk.enterprisecloud.domain;

import java.net.URI;
import java.util.Map;

import org.jclouds.trmk.enterprisecloud.domain.internal.BaseNamedResource;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class NamedResource extends BaseNamedResource<NamedResource> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromNamedResource(this);
   }

   public static class Builder extends BaseNamedResource.Builder<NamedResource> {

      /**
       * {@inheritDoc}
       */
      @Override
      public NamedResource build() {
         return new NamedResource(href, type, name);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<NamedResource> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> in) {
         return Builder.class.cast(super.fromAttributes(in));
      }

   }

   public NamedResource(URI href, String type, String name) {
      super(href, type, name);
   }
}