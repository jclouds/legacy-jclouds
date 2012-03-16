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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.google.common.base.Objects;

/**
 * The ticket for accessing the console of a VM.
 *
 * <pre>
 * &lt;complexType name="ScreenTicket" /&gt;
 * </pre>
 */
@XmlRootElement(name = "ScreenTicket")
@XmlType(name = "ScreenTicketType")
public class ScreenTicket {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromScreenTicket(this);
   }

   public static class Builder {
      private String value;

      /**
       * @see VmQuestionAnswer#getChoiceId()
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }

      public ScreenTicket build() {
         return new ScreenTicket(value);
      }

      public Builder fromScreenTicket(ScreenTicket in) {
         return value(in.getValue());
      }
   }

   protected ScreenTicket() {
      // For JAXB
   }
   
   public ScreenTicket(String value) {
      this.value = value;
   }

   @XmlValue
   private String value;

   /**
    * Gets the value of the value property.
    */
   public String getValue() {
      return value;
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ScreenTicket that = ScreenTicket.class.cast(o);
      return equal(this.value, that.value);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(value);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("value", value).toString();
   }
}
