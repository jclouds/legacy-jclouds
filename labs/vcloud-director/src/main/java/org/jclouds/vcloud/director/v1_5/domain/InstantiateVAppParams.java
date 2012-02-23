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



/**
 * Parameters for Instantiating a vApp
 * 
 * @author danikov
 */
public class InstantiateVAppParams
    extends InstantiateVAppParamsType<InstantiateVAppParams> {
   
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder 
      extends InstantiateVAppParamsType.Builder<InstantiateVAppParams> {

      public InstantiateVAppParams build() {
         InstantiateVAppParams instantiateVAppParams = new InstantiateVAppParams();
         instantiateVAppParams.setSource(source);
         instantiateVAppParams.setIsSourceDelete(isSourceDelete);
         instantiateVAppParams.setLinkedClone(linkedClone);
         instantiateVAppParams.setVAppParent(vAppParent);
         instantiateVAppParams.setInstantiationParams(instantiationParams);
         instantiateVAppParams.setDeploy(deploy);
         instantiateVAppParams.setPowerOn(powerOn);
         instantiateVAppParams.setDescription(description);
         instantiateVAppParams.setName(name);
         return instantiateVAppParams;
      }
      
      /**
       * @see InstantiateVAppParams#getSource()
       */
      public Builder source(Reference source) {
         super.source(source);
         return this;
      }

      /**
       * @see InstantiateVAppParams#getIsSourceDelete()
       */
      public Builder isSourceDelete(Boolean isSourceDelete) {
         super.isSourceDelete(isSourceDelete);
         return this;
      }

      /**
       * @see InstantiateVAppParams#getLinkedClone()
       */
      public Builder linkedClone(Boolean linkedClone) {
         super.linkedClone(linkedClone);
         return this;
      }
      
      /**
       * @see ParamsType#getDescription()
       */
      public Builder description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      public Builder name(String name) {
         super.name(name);
         return this;
      }
      
      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      public Builder vAppParent(Reference vAppParent) {
         super.vAppParent(vAppParent);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      public Builder instantiationParams(InstantiationParams instantiationParams) {
         super.instantiationParams(instantiationParams);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getDeploy()
       */
      public Builder deploy(Boolean deploy) {
         super.deploy(deploy);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getPowerOn()
       */
      public Builder powerOn(Boolean powerOn) {
         super.powerOn(powerOn);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromInstantiateVAppParamsType(
            InstantiateVAppParamsType<InstantiateVAppParams> in) {
          return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }
      public Builder fromInstantiateVAppParams(InstantiateVAppParams in) {
         return fromInstantiateVAppParamsType(in);
      }
   }

   protected InstantiateVAppParams() {
      // For JAXB and builder use
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateVAppParams that = InstantiateVAppParams.class.cast(o);
      return super.equals(that);
   }

}
