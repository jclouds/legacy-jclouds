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

import static org.jclouds.dmtf.DMTFConstants.OVF_ENV_NS;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Type for root OVF environment
 *
 * <pre>
 * &lt;complexType name="Environment_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://schemas.dmtf.org/ovf/environment/1}Section" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Entity" type="{http://schemas.dmtf.org/ovf/environment/1}Entity_Type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "Environment_Type")
public class EnvironmentType {

    @XmlElementRef(name = "Section", namespace = OVF_ENV_NS)
    protected Set<SectionType<?>> sections = Sets.newLinkedHashSet();
    @XmlElement(name = "Entity")
    protected Set<EntityType> entities = Sets.newLinkedHashSet();
    @XmlAnyElement(lax = true)
    protected Set<Object> any = Sets.newLinkedHashSet();
    @XmlAttribute(namespace = OVF_ENV_NS)
    protected String id;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = Maps.newLinkedHashMap();

    /**
     * Entity independent meta-data sections Gets the value of the sections property.
     */
    public Set<SectionType<?>> getSections() {
        return sections;
    }

    /**
     * Gets the value of the entities property.
     */
    public Set<EntityType> getEntities() {
        return entities;
    }

    /**
     * Gets the value of the any property.
     */
    public Set<Object> getAny() {
        return any;
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        if (id == null) {
            return "";
        } else {
            return id;
        }
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
