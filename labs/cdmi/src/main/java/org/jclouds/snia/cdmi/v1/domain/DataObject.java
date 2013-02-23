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
package org.jclouds.snia.cdmi.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * 
 * @author Kenneth Nagin
 */
public class DataObject extends CDMIObject {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromDataObject(this);
   }

   public static class Builder<B extends Builder<B>> extends CDMIObject.Builder<B> {
      private String mimetype = "";
      private String value = "";

      /**
       * @see DataObject#getMimetype()
       */
      public B mimetype(String mimetype) {
         this.mimetype = mimetype;
         return self();
      }

      /**
       * @see DataObject#getValueAsString()
       */
      public B value(String value) {
         this.value = value;
         return self();
      }

      @Override
      public DataObject build() {
         return new DataObject(this);
      }

      public B fromDataObject(DataObject in) {
         return fromCDMIObject(in).mimetype(in.getMimetype());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   private final String mimetype;
   private final String value;

   protected DataObject(Builder<?> builder) {
      super(builder);
      this.mimetype = checkNotNull(builder.mimetype, "mimetype");
      this.value = checkNotNull(builder.value, "value");
   }

   /**
    * get dataObject's mimetype.
    */
   public String getMimetype() {
      return mimetype;
   }

   /**
    * get dataObject's value as a String
    */
   public String getValueAsString() {
      return value;
   }

   /**
    * get dataObject's value as a InputStream value character set is Charsets.UTF_8
    * 
    * @return value
    */
   public InputSupplier<ByteArrayInputStream> getValueAsInputSupplier() {
      return ByteStreams.newInputStreamSupplier(value.getBytes(Charsets.UTF_8));
   }

   /**
    * get dataObject's value as a InputStream
    * 
    * @param charset
    *           value character set
    * @return value
    */
   public InputSupplier<ByteArrayInputStream> getValueAsInputSupplier(Charset charset) {
      return ByteStreams.newInputStreamSupplier(value.getBytes(charset));
   }

   /**
    * get dataObject's value as a ByteArray value character set is Charsets.UTF_8
    * 
    * @return value
    */
   public byte[] getValueAsByteArray() {
      return value.getBytes(Charsets.UTF_8);
   }

   /**
    * get dataObject's value as a InputStream
    * 
    * @param charset
    *           value character set
    * @return value
    */
   public byte[] getValueAsByteArray(Charset charset) {
      return value.getBytes(charset);
   }

   /**
    * get dataObject's value as a File value character set is Charsets.UTF_8
    * 
    * @param destDir
    *           destination directory
    * @return value
    */
   public File getValueAsFile(File destDir) throws IOException {
      File fileOut = new File(destDir, this.getObjectName());
      Files.copy(getValueAsInputSupplier(), fileOut);
      return fileOut;
   }

   /**
    * get dataObject's value as a File
    * 
    * @param charset
    *           value character set
    * @return value
    */
   public File getValueAsFile(File destDir, Charset charset) throws IOException {
      File fileOut = new File(destDir, this.getObjectName());
      Files.copy(getValueAsInputSupplier(charset), fileOut);
      return fileOut;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      DataObject that = DataObject.class.cast(o);
      return super.equals(that) && equal(this.mimetype, that.mimetype) && equal(this.value, that.value);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), mimetype, value);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("mimetype", mimetype);
   }

}
