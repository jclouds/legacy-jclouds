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
package org.jclouds.vcloud.director.v1_5.domain.ovf.environment;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * Abstract type for all sections in
 *             environment
 *
 * <p>Java class for Section_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Section_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "Section_Type")
@XmlSeeAlso({
    PlatformSectionType.class,
    PropertySectionType.class
})
public abstract class SectionType<T extends SectionType<T>> {

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = Maps.newLinkedHashMap();

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public int hashCode() {
       return Objects.hashCode(otherAttributes);
    }

    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       SectionType<?> that = (SectionType<?>) obj;
       return Objects.equal(this.otherAttributes, that.otherAttributes);
    }

    @Override
    public String toString() {
       return string().toString();
    }

    protected Objects.ToStringHelper string() {
       return Objects.toStringHelper("").add("otherAttributes", otherAttributes);
    }

}
