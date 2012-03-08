//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.02.08 at 02:47:44 PM GMT
//


package org.jclouds.vcloud.director.v1_5.domain.ovf;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
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
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlSeeAlso({
    StartupSectionItem.class
})
public class Item {

    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1", required = true)
    protected String id;
    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1", required = true)
    @XmlSchemaType(name = "unsignedShort")
    protected int order;
    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer startDelay;
    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    protected Boolean waitingForGuest;
    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer stopDelay;
    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    protected String startAction;
    @XmlAttribute(namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    protected String stopAction;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the order property.
     *
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     *
     */
    public void setOrder(int value) {
        this.order = value;
    }

    /**
     * Gets the value of the startDelay property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public int getStartDelay() {
        if (startDelay == null) {
            return  0;
        } else {
            return startDelay;
        }
    }

    /**
     * Sets the value of the startDelay property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setStartDelay(Integer value) {
        this.startDelay = value;
    }

    /**
     * Gets the value of the waitingForGuest property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isWaitingForGuest() {
        if (waitingForGuest == null) {
            return false;
        } else {
            return waitingForGuest;
        }
    }

    /**
     * Sets the value of the waitingForGuest property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setWaitingForGuest(Boolean value) {
        this.waitingForGuest = value;
    }

    /**
     * Gets the value of the stopDelay property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public int getStopDelay() {
        if (stopDelay == null) {
            return  0;
        } else {
            return stopDelay;
        }
    }

    /**
     * Sets the value of the stopDelay property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setStopDelay(Integer value) {
        this.stopDelay = value;
    }

    /**
     * Gets the value of the startAction property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartAction() {
        if (startAction == null) {
            return "powerOn";
        } else {
            return startAction;
        }
    }

    /**
     * Sets the value of the startAction property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartAction(String value) {
        this.startAction = value;
    }

    /**
     * Gets the value of the stopAction property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStopAction() {
        if (stopAction == null) {
            return "powerOff";
        } else {
            return stopAction;
        }
    }

    /**
     * Sets the value of the stopAction property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStopAction(String value) {
        this.stopAction = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
