package org.jclouds.savvis.vpdc.domain.vapp;

/**
 * 
 * @author Adrian Cole
 */
public class Network {
   private final String name;
   private final String description;

   public Network(String name, String description) {
      this.name = name;
      this.description = description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      Network other = (Network) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Network [name=" + name + ", description=" + description + "]";
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }
}