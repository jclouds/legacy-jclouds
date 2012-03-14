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

import java.net.URI;
import java.util.List;
import java.util.Set;

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
public class VmPendingQuestion extends ResourceType<VmPendingQuestion> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVmPendingQuestion(this);
   }

   public static class Builder extends ResourceType.Builder<VmPendingQuestion> {

      private String question;
      private String questionId;
      private List<VmQuestionAnswerChoice> choices;

      /**
       * @see VmPendingQuestion#getQuestion()
       */
      public Builder question(String question) {
         this.question = question;
         return this;
      }

      /**
       * @see VmPendingQuestion#getQuestionId()
       */
      public Builder questionId(String questionId) {
         this.questionId = questionId;
         return this;
      }

      /**
       * @see VmPendingQuestion#getChoices()
       */
      public Builder choices(List<VmQuestionAnswerChoice> choices) {
         this.choices = choices;
         return this;
      }

      @Override
      public VmPendingQuestion build() {
         VmPendingQuestion vmPendingQuestion = new VmPendingQuestion(href, type, links, question, questionId, choices);
         return vmPendingQuestion;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         return Builder.class.cast(super.link(link));
      }

      @Override
      public Builder fromResourceType(ResourceType<VmPendingQuestion> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder fromVmPendingQuestion(VmPendingQuestion in) {
         return fromResourceType(in).question(in.getQuestion()).questionId(in.getQuestionId()).choices(in.getChoices());
      }
   }

   protected VmPendingQuestion() {
      // For JAXB and builder use
   }

   public VmPendingQuestion(URI href, String type, Set<Link> links, String question, String questionId, List<VmQuestionAnswerChoice> choices) {
      super(href, type, links);
      this.question = question;
      this.questionId = questionId;
      this.choices = choices;
   }

   @XmlElement(name = "Question", required = true)
   protected String question;
   @XmlElement(name = "QuestionId", required = true)
   protected String questionId;
   @XmlElement(name = "Choices", required = true)
   protected List<VmQuestionAnswerChoice> choices;

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
