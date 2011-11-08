package org.jclouds.trmk.enterprisecloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.trmk.enterprisecloud.domain.internal.BaseNamedResource;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class Link extends BaseNamedResource<Link> {
   public static enum Relationship {
      /**
       * The entity in the link owns the entity in the response
       */
      UP,
      /**
       * The entity in the response owns the entity in the link
       */
      DOWN,
      /**
       * The entity in the link is an alternate view of the entity in the
       * response
       */
      ALTERNATE,
      /**
       * The link is a path to the first page in the pages of responses
       */
      FIRST,
      /**
       * The link is a path to the previous page in the pages of responses
       */
      PREVIOUS,
      /**
       * The link is a path to the next page in the pages of responses
       */
      NEXT,
      /**
       * The link is a path to the last page in the pages of responses
       */
      LAST,
      /**
       * Relationship was not parsed by jclouds.
       */
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Relationship fromValue(String rel) {
         try {
            return valueOf(checkNotNull(rel, "rel").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromLink(this);
   }

   public static class Builder extends BaseNamedResource.Builder<Link> {

      protected Relationship rel;

      /**
       * @see Link#getRelationship
       */
      public Builder rel(Relationship rel) {
         this.rel = rel;
         return this;
      }

      @Override
      public Link build() {
         return new Link(href, name, type, rel);
      }

      public Builder fromLink(Link in) {
         return fromNamedResource(in).rel(in.getRelationship());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<Link> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         super.fromAttributes(attributes);
         if (attributes.containsKey("rel"))
            rel(Relationship.fromValue(attributes.get("rel")));
         return this;
      }
   }

   protected final Relationship rel;

   public Link(URI href, String type, String name, Relationship rel) {
      super(href, type, name);
      this.rel = checkNotNull(rel, "rel");
   }

   /**
    * 
    * @return
    */
   public Relationship getRelationship() {
      return rel;
   }

}