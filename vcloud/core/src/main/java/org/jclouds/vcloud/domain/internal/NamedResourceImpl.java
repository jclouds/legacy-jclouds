package org.jclouds.vcloud.domain.internal;

import java.net.URI;

import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.vcloud.domain.NamedResource;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class NamedResourceImpl extends NamedLinkImpl implements NamedResource {
   private final int id;

   public NamedResourceImpl(int id, String name, String type, URI location) {
      super(name, type, location);
      this.id = id;
   }

   public int getId() {
      return id;
   }

   public int compareTo(NamedResource that) {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if (this == that)
         return EQUAL;

      if (this.id < that.getId())
         return BEFORE;
      if (this.id > that.getId())
         return AFTER;
      return EQUAL;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + id;
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
      if (id != other.id)
         return false;
      return true;
   }

}