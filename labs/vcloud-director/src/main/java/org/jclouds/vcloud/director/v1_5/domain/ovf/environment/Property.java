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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_ENV_NS;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * Java class for anonymous complex type.
 */
@XmlType(name = "")
public class Property {

    @XmlAttribute(namespace = VCLOUD_OVF_ENV_NS, required = true)
    protected String key;
    @XmlAttribute(namespace = VCLOUD_OVF_ENV_NS, required = true)
    protected String value;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the key property.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
