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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a question which vSphere issued for a VM in a stuck state(WAITING_FOR_INPUT). It has a question element, a question ID
 * element, and a list of choices with at least one element.
 *
 * <pre>
 * &lt;complexType name="VmPendingQuestion" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "VmPendingQuestion")
public class VmPendingQuestion extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVmPendingQuestion(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {

      private String question;
      private String questionId;
      private List<VmQuestionAnswerChoice> choices;

      /**
       * @see VmPendingQuestion#getQuestion()
       */
      public B question(String question) {
         this.question = question;
         return self();
      }

      /**
       * @see VmPendingQuestion#getQuestionId()
       */
      public B questionId(String questionId) {
         this.questionId = questionId;
         return self();
      }

      /**
       * @see VmPendingQuestion#getChoices()
       */
      public B choices(List<VmQuestionAnswerChoice> choices) {
         this.choices = choices;
         return self();
      }

      @Override
      public VmPendingQuestion build() {
         VmPendingQuestion vmPendingQuestion = new VmPendingQuestion(this);
         return vmPendingQuestion;
      }

      public B fromVmPendingQuestion(VmPendingQuestion in) {
         return fromResource(in).question(in.getQuestion()).questionId(in.getQuestionId()).choices(in.getChoices());
      }
   }

   @XmlElement(name = "Question", required = true)
   private String question;
   @XmlElement(name = "QuestionId", required = true)
   private String questionId;
   @XmlElement(name = "Choices", required = true)
   private List<VmQuestionAnswerChoice> choices;

   protected VmPendingQuestion() {
      // For JAXB and B use
   }

   public VmPendingQuestion(Builder<?> builder) {
      super(builder);
      this.question = builder.question;
      this.questionId = builder.questionId;
      this.choices = builder.choices;
   }

   /**
    * Gets the value of the question property.
    */
   public String getQuestion() {
      return question;
   }

   /**
    * Gets the value of the questionId property.
    */
   public String getQuestionId() {
      return questionId;
   }

   /**
    * Gets the value of the choices property.
    */
   public List<VmQuestionAnswerChoice> getChoices() {
      return choices;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VmPendingQuestion that = VmPendingQuestion.class.cast(o);
      return super.equals(that) &&
            equal(this.question, that.question) && equal(this.questionId, that.questionId) && equal(this.choices, that.choices);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), question, questionId, choices);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("question", question).add("questionId", questionId).add("choices", choices);
   }

}
