package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * <xs:complexType name="LinkType">
 * 
 * @author Adrian Cole
 * 
 */
public class Link extends BaseNamedResource<Link> {

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

      protected String rel;

      /**
       * @see Link#getString
       */
      public Builder rel(String rel) {
         this.rel = rel;
         return this;
      }

      @Override
      public Link build() {
         return new Link(href, type, name, rel);
      }

      public Builder fromLink(Link in) {
         return fromNamedResource(in).rel(in.getRel());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<Link> in) {
         return Builder.class.cast(super.fromBaseResource(in));
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
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
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
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         super.fromAttributes(attributes);
         rel(attributes.get("rel"));
         return this;
      }
   }

   @XmlAttribute
   protected String rel;

   private Link(URI href, String type, String name, String rel) {
      super(href, type, name);
      this.rel = checkNotNull(rel, "rel");
   }

   private Link() {
      // For JAXB
   }

   /**
    * Defines the relationship of the link to the object that contains it. A relationship can be the
    * name of an operation on the object, a reference to a contained or containing object, or a
    * reference to an alternate representation of the object. The relationship value implies the
    * HTTP verb to use when you use the link's href value as a request URL.
    * 
    * @return relationship of the link to the object that contains it.
    */
   public String getRel() {
      return rel;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Link that = (Link) o;
      return equal(this.rel, that.rel);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(rel);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("rel", rel);
   }
}