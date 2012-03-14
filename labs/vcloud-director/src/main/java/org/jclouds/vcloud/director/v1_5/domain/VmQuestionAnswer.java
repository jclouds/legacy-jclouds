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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 Represents a VM answer to a question when the VM is in a stuck
 *                 (WAITING_FOR_INPUT) state.
 *
 *
 * <p>Java class for VmQuestionAnswer complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
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
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VmQuestionAnswer", propOrder = {
    "choiceId",
    "questionId"
})
public class VmQuestionAnswer {
   
   // TODO builder

    @XmlElement(name = "ChoiceId")
    protected int choiceId;
    @XmlElement(name = "QuestionId", required = true)
    protected String questionId;

    /**
     * Gets the value of the choiceId property.
     *
     */
    public int getChoiceId() {
        return choiceId;
    }

    /**
     * Sets the value of the choiceId property.
     *
     */
    public void setChoiceId(int value) {
        this.choiceId = value;
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

    /**
     * Sets the value of the questionId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuestionId(String value) {
        this.questionId = value;
    }

}
