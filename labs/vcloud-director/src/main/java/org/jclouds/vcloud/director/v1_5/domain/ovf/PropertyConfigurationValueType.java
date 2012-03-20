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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Type for alternative default values for properties when DeploymentOptionSection is used
 * 
 * <pre>
 * &lt;complexType name="PropertyConfigurationValue_Type">
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlType(name = "PropertyConfigurationValue_Type")
public class PropertyConfigurationValueType {

   // TODO Builder

   @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1", required = true)
   protected String value;
   @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1")
   protected String configuration;

   /**
    * Gets the value of the value property.
    */
   public String getValue() {
      return value;
   }

   /**
    * Gets the value of the configuration property.
    */
   public String getConfiguration() {
      return configuration;
   }

   // TODO hashCode, equals, toString
}
