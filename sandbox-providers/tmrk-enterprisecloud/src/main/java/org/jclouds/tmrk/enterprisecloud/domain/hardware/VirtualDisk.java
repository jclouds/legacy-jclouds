/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.tmrk.enterprisecloud.domain.hardware;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Size;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="VirtualDisk">
 * @author Jason King
 */
public class VirtualDisk {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   public Builder toBuilder() {
      return new Builder().fromVirtualDisk(this);
   }

   public static class Builder {

       private String name;
       private Size size;
       private int index;

       /**
        * @see VirtualDisk#getName
        */
       public Builder name(String name) {
          this.name = name;
          return this;
       }

       /**
        * @see VirtualDisk#getSize
        */
       public Builder size(Size size) {
          this.size = size;
          return this;
       }

       /**
        * @see VirtualDisk#getIndex
        */
       public Builder index(int index) {
          this.index = index;
          return this;
       }

       public VirtualDisk build() {
           return new VirtualDisk(name, size, index);
       }

      public Builder fromVirtualDisk(VirtualDisk in) {
        return name(in.getName())
               .size(in.getSize())
               .index(in.getIndex());
      }
   }

   @XmlElement(name = "Name", required = false)
   private String name;

   @XmlElement(name = "Size", required = false)
   private Size size;

   @XmlElement(name = "Index", required = false)
   private int index;

   public VirtualDisk(@Nullable String name, @Nullable Size size, int index) {
       this.name = name;
       this.size = size;
       this.index = index;
   }

    protected VirtualDisk() {
        //For JAXB
    }

    public String getName() {
        return name;
    }

    public Size getSize() {
        return size;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualDisk disk = (VirtualDisk) o;

        if (index != disk.index) return false;
        if (name != null ? !name.equals(disk.name) : disk.name != null)
            return false;
        if (size != null ? !size.equals(disk.size) : disk.size != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "[name="+name+", size="+size+", index="+index+"]";
    }
}
