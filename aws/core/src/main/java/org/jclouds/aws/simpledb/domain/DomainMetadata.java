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

package org.jclouds.aws.simpledb.domain;

import java.util.Date;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonSimpleDB/2009-04-15/DeveloperGuide/index.html?SDB_API_CreateDomain.html"
 *      />
 * @author Adrian Cole
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class DomainMetadata {
   private final String region;
   private final String name;
   private final Date timestamp;
   private final long itemCount;
   private final long attributeValueCount;
   private final long attributeNameCount;
   private final long itemNamesSizeBytes;
   private final long attributeValuesSizeBytes;
   private final long attributeNamesSizeBytes;

   public DomainMetadata(String region, String name, Date timestamp, long itemCount, long attributeValueCount,
         long attributeNameCount, long itemNamesSizeBytes, long attributeValuesSizeBytes, long attributeNamesSizeBytes) {
      this.region = region;
      this.name = name;
      this.timestamp = timestamp;
      this.itemCount = itemCount;
      this.attributeValueCount = attributeValueCount;
      this.attributeNameCount = attributeNameCount;
      this.itemNamesSizeBytes = itemNamesSizeBytes;
      this.attributeValuesSizeBytes = attributeValuesSizeBytes;
      this.attributeNamesSizeBytes = attributeNamesSizeBytes;
   }

   /**
    * 
    * @return region the domain belongs to
    */
   public String getRegion() {
      return region;
   }

   /**
    * 
    * @return name of the domain
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return The number of all items in the domain.
    */
   public Date getTimestamp() {
      return timestamp;
   }

   /**
    * 
    * @return
    */
   public long getItemCount() {
      return itemCount;
   }

   /**
    * 
    * @return The number of all attribute name/value pairs in the domain.
    */
   public long getAttributeValueCount() {
      return attributeValueCount;
   }

   /**
    * 
    * @return The number of unique attribute names in the domain.
    */
   public long getAttributeNameCount() {
      return attributeNameCount;
   }

   /**
    * 
    * @return The total size of all item names in the domain, in bytes.
    */
   public long getItemNamesSizeBytes() {
      return itemNamesSizeBytes;
   }

   /**
    * 
    * @return The total size of all attribute values, in bytes.
    */
   public long getAttributeValuesSizeBytes() {
      return attributeValuesSizeBytes;
   }

   /**
    * 
    * @return The total size of all unique attribute names, in bytes.
    */
   public long getAttributeNamesSizeBytes() {
      return attributeNamesSizeBytes;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (attributeNameCount ^ (attributeNameCount >>> 32));
      result = prime * result + (int) (attributeNamesSizeBytes ^ (attributeNamesSizeBytes >>> 32));
      result = prime * result + (int) (attributeValueCount ^ (attributeValueCount >>> 32));
      result = prime * result + (int) (attributeValuesSizeBytes ^ (attributeValuesSizeBytes >>> 32));
      result = prime * result + (int) (itemCount ^ (itemCount >>> 32));
      result = prime * result + (int) (itemNamesSizeBytes ^ (itemNamesSizeBytes >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
      DomainMetadata other = (DomainMetadata) obj;
      if (attributeNameCount != other.attributeNameCount)
         return false;
      if (attributeNamesSizeBytes != other.attributeNamesSizeBytes)
         return false;
      if (attributeValueCount != other.attributeValueCount)
         return false;
      if (attributeValuesSizeBytes != other.attributeValuesSizeBytes)
         return false;
      if (itemCount != other.itemCount)
         return false;
      if (itemNamesSizeBytes != other.itemNamesSizeBytes)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (timestamp == null) {
         if (other.timestamp != null)
            return false;
      } else if (!timestamp.equals(other.timestamp))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", name=" + name + ", timestamp=" + timestamp + ", itemCount=" + itemCount
            + ", attributeValueCount=" + attributeValueCount + ", attributeNameCount=" + attributeNameCount
            + ", itemNamesSizeBytes=" + itemNamesSizeBytes + ", attributeValuesSizeBytes=" + attributeValuesSizeBytes
            + ", attributeNamesSizeBytes=" + attributeNamesSizeBytes + "]";
   }

}
