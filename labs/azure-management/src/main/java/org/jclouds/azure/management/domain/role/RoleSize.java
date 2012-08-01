package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "RoleSize")
@XmlEnum
public enum RoleSize {
   @XmlEnumValue("ExtraSmall")
   ExtraSmall, 
   @XmlEnumValue("Small")
   Small, 
   @XmlEnumValue("Medium")
   Medium, 
   @XmlEnumValue("Large")
   Large, 
   @XmlEnumValue("ExtraLarge")
   ExtraLarge;
}
