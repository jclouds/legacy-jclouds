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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Container of sections for a specific entity
 *
 * <pre>
 * &lt;complexType name="Entity_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://schemas.dmtf.org/ovf/environment/1}Section" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "Entity_Type")
public class EntityType {

    @XmlElementRef(name = "Section", namespace = OVF_ENV_NS)
    protected Set<SectionType<?>> sections = Sets.newLinkedHashSet();
    @XmlAnyElement(lax = true)
    protected Set<Object> any = Sets.newLinkedHashSet();
    @XmlAttribute(namespace = OVF_ENV_NS, required = true)
    protected String id;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = Maps.newLinkedHashMap();

    /**
     * Gets the value of the sections property.
     */
    public Set<SectionType<?>> getSection() {
        return sections;
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
        return id;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}
