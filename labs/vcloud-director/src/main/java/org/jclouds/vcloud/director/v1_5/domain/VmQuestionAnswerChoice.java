/*
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Represents a pair of ID and text of an answer choice of a VM question.
 *
 * <pre>
 * &lt;complexType name="VmQuestionAnswerChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "VmQuestionAnswerChoice", propOrder = {
    "id",
    "text"
})
public class VmQuestionAnswerChoice {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVmQuestionAnswerChoice(this);
   }

   public static class Builder {
      private int id;
      private String text;

      /**
       * @see VmQuestionAnswer#getChoiceId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see VmQuestionAnswer#getQuestionId()
       */
      public Builder text(String text) {
         this.text = text;
         return this;
      }

      public VmQuestionAnswerChoice build() {
         return new VmQuestionAnswerChoice(id, text);
      }

      public Builder fromVmQuestionAnswerChoice(VmQuestionAnswerChoice in) {
         return id(in.getId()).text(in.getText());
      }
   }

   protected VmQuestionAnswerChoice() {
      // For JAXB
   }

   public VmQuestionAnswerChoice(int id, String text) {
      this.id = id;
      this.text = text;
   }

    @XmlElement(name = "Id")
    private int id;
    @XmlElement(name = "Text")
    private String text;

    /**
     * Gets the value of the id property.
     *
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the value of the text property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getText() {
        return text;
    }
    
    
    @Override
    public boolean equals(Object o) {
       if (this == o)
          return true;
       if (o == null || getClass() != o.getClass())
          return false;
       VmQuestionAnswerChoice that = VmQuestionAnswerChoice.class.cast(o);
       return equal(this.id, that.id) && equal(this.text, that.text);
    }

    @Override
    public int hashCode() {
       return Objects.hashCode(id, text);
    }

    @Override
    public String toString() {
       return Objects.toStringHelper("").add("id", id).add("text", text).toString();
    }
}
