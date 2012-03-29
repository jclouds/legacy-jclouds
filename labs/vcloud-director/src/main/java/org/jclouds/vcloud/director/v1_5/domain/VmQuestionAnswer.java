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
 * Represents a VM answer to a question when the VM is in a stuck
 * {@link ResourceEntityType.Status#WAITING_FOR_INPUT} state.
 *
 * <pre>
 * &lt;complexType name="VmQuestionAnswer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChoiceId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="QuestionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "VmQuestionAnswer", propOrder = {
    "choiceId",
    "questionId"
})
public class VmQuestionAnswer {
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVmQuestionAnswer(this);
   }

   public static class Builder {
      private int choiceId;
      private String questionId;

      /**
       * @see VmQuestionAnswer#getChoiceId()
       */
      public Builder choiceId(int choiceId) {
         this.choiceId = choiceId;
         return this;
      }

      /**
       * @see VmQuestionAnswer#getQuestionId()
       */
      public Builder questionId(String questionId) {
         this.questionId = questionId;
         return this;
      }

      public VmQuestionAnswer build() {
         return new VmQuestionAnswer(choiceId, questionId);
      }

      public Builder fromVmQuestionAnswer(VmQuestionAnswer in) {
         return choiceId(in.getChoiceId()).questionId(in.getQuestionId());
      }
   }

    @XmlElement(name = "ChoiceId")
    private int choiceId;
    @XmlElement(name = "QuestionId", required = true)
    private String questionId;

    protected VmQuestionAnswer() {
       // For JAXB
    }

    public VmQuestionAnswer(int choiceId, String questionId) {
       this.choiceId = choiceId;
       this.questionId = questionId;
    }

    /**
     * Gets the value of the choiceId property.
     *
     */
    public int getChoiceId() {
        return choiceId;
    }

    /**
     * Gets the value of the questionId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuestionId() {
        return questionId;
    }
    
    @Override
    public boolean equals(Object o) {
       if (this == o)
          return true;
       if (o == null || getClass() != o.getClass())
          return false;
       VmQuestionAnswer that = VmQuestionAnswer.class.cast(o);
       return equal(this.choiceId, that.choiceId) && equal(this.questionId, that.questionId);
    }

    @Override
    public int hashCode() {
       return Objects.hashCode(choiceId, questionId);
    }

    @Override
    public String toString() {
       return Objects.toStringHelper("").add("choiceId", choiceId).add("questionId", questionId).toString();
    }
}
