/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.twitter.domain;

import java.util.Arrays;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class Location {
   private String type;
   private double[] coordinates;

   public Location() {

   }

   public Location(String type, double[] coordinates) {
      this.type = type;
      this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
   }

   public String getType() {
      return type;
   }

   public double[] getCoordinates() {
      return coordinates;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(coordinates);
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
      Location other = (Location) obj;
      if (!Arrays.equals(coordinates, other.coordinates))
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
      return "Location [coordinates=" + Arrays.toString(coordinates) + ", type=" + type + "]";
   }

}
