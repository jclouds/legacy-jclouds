/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 
 * An Offering for Storage
 * 
 * @author Adrian Cole
 */
public class StorageOffering extends Offering {
   public static class Format {
      private String label;
      private String id;

      Format() {

      }

      public Format(String label, String id) {
         this.label = label;
         this.id = id;
      }

      public String getLabel() {
         return label;
      }

      public String getId() {
         return id;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((id == null) ? 0 : id.hashCode());
         result = prime * result + ((label == null) ? 0 : label.hashCode());
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
         Format other = (Format) obj;
         if (id == null) {
            if (other.id != null)
               return false;
         } else if (!id.equals(other.id))
            return false;
         if (label == null) {
            if (other.label != null)
               return false;
         } else if (!label.equals(other.label))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[id=" + id + ", label=" + label + "]";
      }
   }

   private long capacity;
   private String name;
   private Set<Format> formats = Sets.newLinkedHashSet();

   StorageOffering() {

   }

   public StorageOffering(String location, Price price, long capacity, String name, String id, Set<Format> formats) {
      this.location = location;
      this.price = price;
      this.capacity = capacity;
      this.name = name;
      this.id = id;
      this.formats = formats;
   }

   public long getCapacity() {
      return capacity;
   }

   public String getName() {
      return name;
   }

   public Set<? extends Format> getFormats() {
      return formats;
   }

   @Override
   public String toString() {
      return "[capacity=" + capacity + ", formats=" + formats + ", id=" + id + ", location=" + location + ", name="
            + name + ", price=" + price + "]";
   }

}
