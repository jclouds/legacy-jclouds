package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.domain.JsonBall;

/**
 * 
 * @author Adrian Cole
 */
public class DataBagItem extends JsonBall {

   private static final long serialVersionUID = 7905637919304343493L;
   private final String id;

   public DataBagItem(String id, String value) {
      super(value);
      this.id = checkNotNull(id, "id");
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
      DataBagItem other = (DataBagItem) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   public String getId() {
      return id;
   }
}