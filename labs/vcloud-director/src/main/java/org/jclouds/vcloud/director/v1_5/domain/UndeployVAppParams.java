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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Represents vApp/VM undeployment parameters.
 * 
 * <pre>
 * &lt;complexType name="UndeployVAppParamsType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 * @since 0.9
 */
@XmlRootElement(name = "UndeployVAppParams")
@XmlType(name = "UndeployVAppParamsType")
public class UndeployVAppParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromUndeployVAppParams(this);
   }

   public static class Builder {

      private String undeployPowerAction;

      /**
       * @see UndeployVAppParams#getUndeployPowerAction()
       */
      public Builder undeployPowerAction(String undeployPowerAction) {
         this.undeployPowerAction = undeployPowerAction;
         return this;
      }

      public UndeployVAppParams build() {
         UndeployVAppParams undeployVAppParams = new UndeployVAppParams();
         undeployVAppParams.undeployPowerAction = undeployPowerAction;
         return undeployVAppParams;
      }

      public Builder fromUndeployVAppParams(UndeployVAppParams in) {
         return undeployPowerAction(in.getUndeployPowerAction());
      }
   }

   private UndeployVAppParams() {
      // For JAXB and builder use
   }

   @XmlElement(name = "UndeployPowerAction")
   protected String undeployPowerAction;

   /**
    * Gets the value of the undeployPowerAction property.
    */
   public String getUndeployPowerAction() {
      return undeployPowerAction;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      UndeployVAppParams that = UndeployVAppParams.class.cast(o);
      return equal(undeployPowerAction, that.undeployPowerAction);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(undeployPowerAction);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("undeployPowerAction", undeployPowerAction).toString();
   }

}
