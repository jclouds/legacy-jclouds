/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf;

import java.net.URI;

/**
 * 
 * @author Adrian Cole
 */
public class Disk implements Comparable<Disk>{
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private Long capacity;
      private String parentRef;
      private String fileRef;
      private URI format;
      private Long populatedSize;
      private String capacityAllocationUnits;

      /**
       * @see Disk#getId
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Disk#getCapacity
       */
      public Builder capacity(Long capacity) {
         this.capacity = capacity;
         return this;
      }

      /**
       * @see Disk#getParentRef
       */
      public Builder parentRef(String parentRef) {
         this.parentRef = parentRef;
         return this;
      }

      /**
       * @see Disk#getFileRef
       */
      public Builder fileRef(String fileRef) {
         this.fileRef = fileRef;
         return this;
      }

      /**
       * @see Disk#getFormat
       */
      public Builder format(URI format) {
         this.format = format;
         return this;
      }

      /**
       * @see Disk#getPopulatedSize
       */
      public Builder populatedSize(Long populatedSize) {
         this.populatedSize = populatedSize;
         return this;
      }

      /**
       * @see Disk#getCapacityAllocationUnits
       */
      public Builder capacityAllocationUnits(String capacityAllocationUnits) {
         this.capacityAllocationUnits = capacityAllocationUnits;
         return this;
      }

      public Disk build() {
         return new Disk(id, capacity, parentRef, fileRef, format, populatedSize, capacityAllocationUnits);
      }

      public Builder fromDisk(Disk in) {
         return id(in.getId()).capacity(in.getCapacity()).parentRef(in.getParentRef()).fileRef(in.getFileRef()).format(
                  in.getFormat()).populatedSize(in.getPopulatedSize()).capacityAllocationUnits(
                  in.getCapacityAllocationUnits());
      }
   }

   private final String id;
   private final Long capacity;
   private final String parentRef;
   private final String fileRef;
   private final URI format;
   private final Long populatedSize;
   private final String capacityAllocationUnits;

   public Disk(String id, Long capacity, String parentRef, String fileRef, URI format, Long populatedSize,
            String capacityAllocationUnits) {
      this.id = id;
      this.capacity = capacity;
      this.parentRef = parentRef;
      this.fileRef = fileRef;
      this.format = format;
      this.populatedSize = populatedSize;
      this.capacityAllocationUnits = capacityAllocationUnits;
   }

   /**
    * Each virtual disk is represented by a Disk element that shall be given a identifier using the
    * {@code id} attribute, the identifier shall be unique within the {@link DiskSection}.
    */
   public String getId() {
      return id;
   }

   /**
    * The capacity of a virtual disk shall be specified by the {@code capacity} attribute with an
    * xs:long integer value. The default unit of allocation shall be bytes.
    */
   public Long getCapacity() {
      return capacity;
   }

   /**
    * OVF allows a disk image to be represented as a set of modified blocks in comparison to a
    * parent image. The use of parent disks can often significantly reduce the size of an OVF
    * package, if it contains multiple disks with similar content. For a Disk element, a parent disk
    * may optionally be specified using the {@code parentRef} attribute, which shall contain a valid
    * ovf:id reference to a different Disk element. If a disk block does not exist locally, lookup
    * for that disk block then occurs in the parent disk. In {@link DiskSection}, parent Disk
    * elements shall occur before child Disk elements that refer to them.
    */
   public String getParentRef() {
      return parentRef;
   }

   /**
    * The ovf:fileRef attribute denotes the virtual disk content by identifying an existing File
    * element in the References element, the File element is identified by matching its {@code id}
    * attribute value with the {@code fileRef} attribute value. Omitting the {@code fileRef}
    * attribute shall indicate an empty disk. In this case, the disk shall be created and the entire
    * disk content zeroed at installation time. The guest software will typically format empty disks
    * in some file system format.
    */
   public String getFileRef() {
      return fileRef;
   }

   /**
    * The format URI of a non-empty virtual disk shall be specified by the {@code format} attribute.
    */
   public URI getFormat() {
      return format;
   }

   /**
    * For non-empty disks, the actual used size of the disk may optionally be specified using the
    * {@code populatedSize} attribute. The unit of this attribute is always bytes. {@code
    * populatedSize} is allowed to be an estimate of used disk size but shall not be larger than
    * {@code capacity}.
    */
   public Long getPopulatedSize() {
      return populatedSize;
   }

   /**
    * The optional string attribute {@code ovf:capacityAllocationUnits} may be used to specify a
    * particular unit of allocation. Values for {@code ovf:capacityAllocationUnits} shall match the
    * format for programmatic units defined in DSP0004.
    */
   public String getCapacityAllocationUnits() {
      return capacityAllocationUnits;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      Disk other = (Disk) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[id=%s, capacity=%s, capacityAllocationUnits=%s, fileRef=%s, format=%s, parentRef=%s, populatedSize=%s]",
                        id, capacity, capacityAllocationUnits, fileRef, format, parentRef, populatedSize);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(Disk o) {
      if (id == null)
         return -1;
      return (this == o) ? 0 : id.compareTo(o.id);
   }
}
