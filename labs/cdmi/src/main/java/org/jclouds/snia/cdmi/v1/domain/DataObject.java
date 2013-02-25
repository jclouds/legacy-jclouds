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
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.io.BaseEncoding;


/**
 * CDMI Data Object
 * @author Kenneth Nagin
 */
public class DataObject extends CDMIObject {
   final String BASE64 = "base64";

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromDataObject(this);
   }

   public static class Builder<B extends Builder<B>> extends CDMIObject.Builder<B> {
      private String mimetype = new String();
      private String valuetransferencoding = new String();
      private String value = new String();
      private Payload payload;

      /**
       * @see DataObject#getMimetype()
       */
      public B mimetype(String mimetype) {
         this.mimetype = mimetype;
         return self();
      }

      /**
       * @see DataObject#getValuetransferencoding()
       */
      public B valuetransferencoding(String valuetransferencoding) {
         this.valuetransferencoding = valuetransferencoding;
         return self();
      }

      /**
       * @see DataObject#getValue()
       */
      public B value(String value) {
         this.value = value;
         return self();
      }

      /**
       * @see DataObject#getPayload()
       */
      public B payload(Payload payload) {
         this.payload = payload;
         return self();
      }

      @Override
      public DataObject build() {
         return new DataObject(this);
      }

      public B fromDataObject(DataObject in) {
         return fromCDMIObject(in).mimetype(in.getMimetype()).valuetransferencoding(in.getValuetransferencoding());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   private final String mimetype;
   private final String valuetransferencoding;
   private final String value;
   private final Payload payload;

   protected DataObject(Builder<?> builder) {
      super(builder);
      this.mimetype = checkNotNull(builder.mimetype, "mimetype");
      this.valuetransferencoding = checkNotNull(builder.valuetransferencoding, "valuetransferencoding");
      this.value = checkNotNull(builder.value, "value");
      this.payload = checkNotNull(builder.payload, "payload");
   }

   /**
    * get dataObject's mimetype.
    * @return mimetype
    */
   public String getMimetype() {
      return mimetype;
   }

   /**
    * get dataObject's valuetransferencoding
    * 
    * @return valuetransferencoding
    */
   public String getValuetransferencoding() {
      return valuetransferencoding;
   }

   /**
    * get dataObject's value as a String
    * 
    * @return value
    */
   public String getValue() {
      return value;
   }

   /**
    * get dataObject's value as a payload. When valuetransferencoding == base64
    * this method converts the value accordingly.
    * @return payload
    */
   public Payload getPayload() {
      Payload payloadout = payload;
      if (payload == null) {
         if (value != null && valuetransferencoding != null && BASE64.matches(valuetransferencoding)) {
         	payloadout = Payloads.newByteArrayPayload(BaseEncoding.base64().withSeparator("\n", 61).decode(value));
         } else {
            payloadout = Payloads.newStringPayload(value);
         }
      }
      return payloadout;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      DataObject that = DataObject.class.cast(o);
      return super.equals(that) && equal(this.mimetype, that.mimetype)
               && equal(this.valuetransferencoding, that.valuetransferencoding) && equal(this.value, that.value)
               && equal(this.payload, that.payload);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), mimetype, valuetransferencoding, value, payload);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("mimetype", mimetype).add("valuetransferencoding", valuetransferencoding);
   }

}
