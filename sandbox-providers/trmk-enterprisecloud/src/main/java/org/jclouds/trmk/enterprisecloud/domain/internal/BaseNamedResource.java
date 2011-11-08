package org.jclouds.trmk.enterprisecloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class BaseNamedResource<T extends BaseNamedResource<T>> extends BaseResource<T> {

   public static <T extends BaseNamedResource<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromNamedResource(this);
   }

   public static class Builder<T extends BaseNamedResource<T>> extends BaseResource.Builder<T> {

      protected String name;

      /**
       * @see BaseNamedResource#getName
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      public BaseNamedResource<T> build() {
         return new BaseNamedResource<T>(href, type, name);
      }

      public Builder<T> fromNamedResource(BaseNamedResource<T> in) {
         return fromResource(in).name(in.getName());
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      public Builder<T> fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes)).name(attributes.get("name"));
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromResource(BaseResource<T> in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }

   protected final String name;

   public BaseNamedResource(URI href, String type, String name) {
      super(href, type);
      this.name = checkNotNull(name, "name");
   }

   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseNamedResource<?> other = (BaseNamedResource<?>) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", name=" + name + ", type=" + type + "]";
   }
}