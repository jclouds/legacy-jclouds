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
import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Parameters to be used for vm relocation.
 * <p/>
 * <p/>
 * <p>Java class for RelocateParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="RelocateParams">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Datastore" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "RelocateParams")
@XmlType(propOrder = {
      "datastore"
})
public class RelocateParams {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Reference datastore;

      public Builder datastore(Reference dataStore) {
         this.datastore = dataStore;
         return this;
      }

      public RelocateParams build() {
         return new RelocateParams(datastore);
      }
   }

   @XmlElement(name = "Datastore", required = true)
   private Reference datastore;

   private RelocateParams(Reference datastore) {
      this.datastore = checkNotNull(datastore);
   }
   
   private RelocateParams() {
      // for JAXB
   }

   /**
    * Gets the value of the datastore property.
    *
    * @return possible object is
    *         {@link ReferenceType }
    */
   public ReferenceType getDatastore() {
      return datastore;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RelocateParams that = RelocateParams.class.cast(o);
      return equal(datastore, that.datastore);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(datastore);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("datastore", datastore).toString();
   }
}
