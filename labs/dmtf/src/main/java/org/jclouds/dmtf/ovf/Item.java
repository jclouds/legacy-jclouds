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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Objects.equal;

import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * Java class for anonymous complex type.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="order" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *       &lt;attribute name="startDelay" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" default="0" />
 *       &lt;attribute name="waitingForGuest" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="stopDelay" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" default="0" />
 *       &lt;attribute name="startAction" type="{http://www.w3.org/2001/XMLSchema}string" default="powerOn" />
 *       &lt;attribute name="stopAction" type="{http://www.w3.org/2001/XMLSchema}string" default="powerOff" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType
@XmlSeeAlso({
    StartupSectionItem.class
})
public class Item {
   
   // TODO Builder

    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "unsignedShort")
    protected int order;
    @XmlAttribute
    @XmlSchemaType(name = "unsignedShort")
    protected Integer startDelay;
    @XmlAttribute
    protected Boolean waitingForGuest;
    @XmlAttribute
    @XmlSchemaType(name = "unsignedShort")
    protected Integer stopDelay;
    @XmlAttribute
    protected String startAction;
    @XmlAttribute
    protected String stopAction;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = Maps.newHashMap();

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the value of the order property.
     */
    public int getOrder() {
        return order;
    }

    /**
     * Gets the value of the startDelay property.
     */
    public int getStartDelay() {
        if (startDelay == null) {
            return  0;
        } else {
            return startDelay;
        }
    }

    /**
     * Gets the value of the waitingForGuest property.
     */
    public boolean isWaitingForGuest() {
        if (waitingForGuest == null) {
            return false;
        } else {
            return waitingForGuest;
        }
    }

    /**
     * Gets the value of the stopDelay property.
     */
    public int getStopDelay() {
        if (stopDelay == null) {
            return  0;
        } else {
            return stopDelay;
        }
    }

    /**
     * Gets the value of the startAction property.
     */
    public String getStartAction() {
        if (startAction == null) {
            return "powerOn";
        } else {
            return startAction;
        }
    }

    /**
     * Gets the value of the stopAction property.
     */
    public String getStopAction() {
        if (stopAction == null) {
            return "powerOff";
        } else {
            return stopAction;
        }
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public int hashCode() {
       return Objects.hashCode(id, order, startDelay, waitingForGuest, stopDelay, startAction, stopAction);
    }

    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       Item that = Item.class.cast(obj);
       return equal(this.id, that.id) &&
             equal(this.order, that.order) &&
             equal(this.startDelay, that.startDelay) &&
             equal(this.waitingForGuest, that.waitingForGuest) &&
             equal(this.stopDelay, that.stopDelay) &&
             equal(this.startAction, that.startAction) &&
             equal(this.stopAction, that.stopAction);
    }

    @Override
    public String toString() {
       return Objects.toStringHelper("")
             .add("id", id)
             .add("order", order)
             .add("startDelay", startDelay)
             .add("waitingForGuest", waitingForGuest)
             .add("stopDelay", stopDelay)
             .add("startAction", startAction)
             .add("stopAction", stopAction)
             .toString();
    }
}
