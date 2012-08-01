package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "HostCaching")
@XmlEnum
public enum HostCaching {
   @XmlEnumValue("ReadOnly")
   ReadOnly, @XmlEnumValue("ReadWrite")
   ReadWrite;
}
