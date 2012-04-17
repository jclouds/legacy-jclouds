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
package org.jclouds.dmtf.ovf.environment;

import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * Key/value pairs of assigned properties for an entity
 *
 * <pre>
 * &lt;complexType name="PropertySection_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/environment/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="Property" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;anyAttribute processContents='lax'/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "PropertySection")
@XmlType(name = "PropertySection_Type")
public class PropertySectionType extends SectionType<PropertySectionType> {

    @XmlElement(name = "Property")
    protected Set<Property> properties = Sets.newLinkedHashSet();
    @XmlAnyElement(lax = true)
    protected Set<Object> any = Sets.newLinkedHashSet();

    /**
     * Gets the value of the properties property.
     */
    public Set<Property> getProperties() {
        return properties;
    }

    /**
     * Gets the value of the any property.
     */
    public Set<Object> getAny() {
        return any;
    }

    @Override
    public int hashCode() {
       return Objects.hashCode(super.hashCode(), properties, any);
    }

    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       PropertySectionType that = (PropertySectionType) obj;
       return super.equals(that) &&
             Objects.equal(this.properties, that.properties) &&
             Objects.equal(this.any, that.any);
    }

    @Override
    protected Objects.ToStringHelper string() {
       return super.string()
             .add("properties", properties).add("any", any);
    }
}
