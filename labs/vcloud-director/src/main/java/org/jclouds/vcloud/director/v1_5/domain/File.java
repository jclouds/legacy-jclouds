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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * Represents a file to be transferred (uploaded or downloaded).
 * <p/>
 * <p/>
 * <p>Java class for File complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="File">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}EntityType">
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="bytesTransferred" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "File")
public class File extends Entity {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromFile(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Entity.Builder<B> {

      private Long size;
      private Long bytesTransferred;
      private String checksum;

      /**
       * @see File#getSize()
       */
      public B size(Long size) {
         this.size = size;
         return self();
      }

      /**
       * @see File#getBytesTransferred()
       */
      public B bytesTransferred(Long bytesTransferred) {
         this.bytesTransferred = bytesTransferred;
         return self();
      }

      /**
       * @see File#getChecksum()
       */
      public B checksum(String checksum) {
         this.checksum = checksum;
         return self();
      }

      @Override
      public File build() {
         return new File(this);

      }

      public B fromFile(File in) {
         return fromEntityType(in)
               .size(in.getSize())
               .bytesTransferred(in.getBytesTransferred())
               .checksum(in.getChecksum());
      }
   }

   public File(Builder<?> builder) {
      super(builder);
      this.size = builder.size;
      this.bytesTransferred = builder.bytesTransferred;
      this.checksum = builder.checksum;
   }

   @SuppressWarnings("unused")
   private File() {
      // For JAXB
   }

   @XmlAttribute
   protected Long size;
   @XmlAttribute
   protected Long bytesTransferred;
   @XmlAttribute
   @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
   @XmlSchemaType(name = "normalizedString")
   protected String checksum;

   /**
    * Gets the value of the size property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getSize() {
      return size;
   }

   /**
    * Gets the value of the bytesTransferred property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getBytesTransferred() {
      return bytesTransferred;
   }

   /**
    * Gets the value of the checksum property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getChecksum() {
      return checksum;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      File that = File.class.cast(o);
      return super.equals(that) &&
           equal(size, that.size) && 
            equal(bytesTransferred, that.bytesTransferred) &&
            equal(checksum, that.checksum);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
           size, 
            bytesTransferred,
            checksum);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("size", size)
            .add("bytesTransferred", bytesTransferred)
            .add("checksum", checksum);
   }

}
