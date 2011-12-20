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
package org.jclouds.tmrk.enterprisecloud.domain.service.internet;

import org.jclouds.javax.annotation.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import static com.google.common.base.CaseFormat.*;

/**
 * <xs:complexType name="InternetServiceType">
 * @author Jason King
 */
public class InternetServicePersistenceType {

   @XmlEnum
   public static enum PersistenceType {

      @XmlEnumValue("None")
      NONE,

      @XmlEnumValue("SourceIp")
      SOURCE_IP;

      public String value() {
         String lower = UPPER_UNDERSCORE.to(LOWER_CAMEL,name());
         return LOWER_CAMEL.to(UPPER_CAMEL,lower);
      }
   }
   
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNodeServices(this);
   }

   public static class Builder {

       private PersistenceType persistenceType;
       private int timeout = -1;
             
       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType#getPersistenceType
        */
       public Builder persistenceType(PersistenceType persistenceType) {
          this.persistenceType = persistenceType;
          return this;
       }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType#getTimeout
       */
      public Builder timeout(int timeout) {
          this.timeout = timeout;
          return this;
       }

       public InternetServicePersistenceType build() {
           return new InternetServicePersistenceType(persistenceType,timeout);
       }

       public Builder fromNodeServices(InternetServicePersistenceType in) {
          return persistenceType(in.getPersistenceType()).timeout(in.getTimeout());
       }
   }

   private InternetServicePersistenceType() {
      //For JAXB and builder use
   }

   private InternetServicePersistenceType(@Nullable PersistenceType persistenceType, int timeout ) {
      this.persistenceType = persistenceType;
      this.timeout = timeout;
   }

   @XmlElement(name = "Type")
   private PersistenceType persistenceType;

   @XmlElement(name = "Timeout")
   private int timeout = -1;

   public PersistenceType getPersistenceType() {
      return persistenceType;
   }

   public int getTimeout() {
      return timeout;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      InternetServicePersistenceType that = (InternetServicePersistenceType) o;

      if (timeout != that.timeout) return false;
      if (persistenceType != that.persistenceType) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = persistenceType != null ? persistenceType.hashCode() : 0;
      result = 31 * result + timeout;
      return result;
   }

   public String toString() {
      return "[persistenceType="+persistenceType+", timeout="+timeout+"]";
   }
}
