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

import org.jclouds.javax.annotation.Nullable;

/**
 * An OperatingSystemSection specifies the operating system installed on a virtual machine.
 * 
 * @author Adrian Cole
 */
public class OperatingSystemSection extends Section<OperatingSystemSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromOperatingSystemSection(this);
   }

   public static class Builder extends Section.Builder<OperatingSystemSection> {
      protected Integer id;
      protected String description;

      /**
       * @see OperatingSystemSection#getID
       */
      public Builder id(Integer id) {
         this.id = id;
         return this;
      }

      /**
       * @see OperatingSystemSection#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public OperatingSystemSection build() {
         return new OperatingSystemSection(id, info, description);
      }

      public Builder fromOperatingSystemSection(OperatingSystemSection in) {
         return id(in.getId()).info(in.getInfo()).description(in.getDescription());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<OperatingSystemSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   protected final Integer id;
   protected final String description;

   public OperatingSystemSection(@Nullable Integer id, @Nullable String info, @Nullable String description) {
      super(info);
      this.id = id;
      this.description = description;
   }

   /**
    * 
    * @return ovf id
    * @see OSType#getCode()
    */
   public Integer getId() {
      return id;
   }

   /**
    * 
    * @return description or null
    */
   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((info == null) ? 0 : info.hashCode());
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
      OperatingSystemSection other = (OperatingSystemSection) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, id=%s, description=%s]", info, id, description);
   }

}
