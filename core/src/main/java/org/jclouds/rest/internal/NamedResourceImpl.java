package org.jclouds.rest.internal;

import java.net.URI;

import org.jclouds.rest.domain.NamedResource;
import org.jclouds.rest.domain.internal.NamedLinkImpl;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class NamedResourceImpl extends NamedLinkImpl implements NamedResource {
   private final String id;

   public NamedResourceImpl(String id, String name, String type, URI location) {
      super(name, type, location);
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public int compareTo(NamedResource that) {
      return (this == that) ? 0 : this.id.compareTo(that.getId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      NamedResourceImpl other = (NamedResourceImpl) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "NamedResourceImpl [id=" + id + ", name=" + getName() + ", location="
               + getLocation() + ", type=" + getType() + "]";
   }
}