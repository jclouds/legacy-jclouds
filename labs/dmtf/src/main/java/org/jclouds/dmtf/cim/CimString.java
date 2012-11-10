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
package org.jclouds.dmtf.cim;

import static com.google.common.base.Objects.equal;

import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * Java class for cimString complex type.
 * 
 * <pre>
 * &lt;complexType name="cimString" /&gt;
 * </pre>
 */
@XmlType(name = "cimString")
public class CimString {

   public CimString() {
      // JAXB
   }

   public CimString(String value) {
      this.value = value;
   }

   @XmlValue
   protected String value;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = Maps.newHashMap();

   /**
    * Gets the value of the value property.
    */
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    */
   public Map<QName, String> getOtherAttributes() {
       return otherAttributes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(value);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CimString that = CimString.class.cast(obj);
      return equal(this.value, that.value);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("value", value).toString();
   }

}
