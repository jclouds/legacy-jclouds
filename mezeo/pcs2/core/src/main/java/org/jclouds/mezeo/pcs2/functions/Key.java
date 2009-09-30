package org.jclouds.mezeo.pcs2.functions;

public class Key {
   private final String container;
   private final String key;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getContainer() == null) ? 0 : getContainer().hashCode());
      result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
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
      Key other = (Key) obj;
      if (getContainer() == null) {
         if (other.getContainer() != null)
            return false;
      } else if (!getContainer().equals(other.getContainer()))
         return false;
      if (getKey() == null) {
         if (other.getKey() != null)
            return false;
      } else if (!getKey().equals(other.getKey()))
         return false;
      return true;
   }

   public Key(String container, String key) {
      super();
      this.container = container;
      this.key = key;
   }

   public String getContainer() {
      return container;
   }

   public String getKey() {
      return key;
   }
}