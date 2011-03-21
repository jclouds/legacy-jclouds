package org.jclouds.savvis.vpdc.domain;

import java.net.URI;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class Link extends Resource {
   protected final String rel;

   public Link( String id,String name, String type, URI href, String rel) {
      super(id, name, type, href);
      this.rel = rel;
   }

   public String getRel() {
      return rel;
   }

   public int compareTo(Link that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((rel == null) ? 0 : rel.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      Link other = (Link) obj;
      if (rel == null) {
         if (other.rel != null)
            return false;
      } else if (!rel.equals(other.rel))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + ", rel=" + rel + "]";
   }
}