package org.jclouds.tmrk.enterprisecloud.domain.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Protocol {

   @XmlEnumValue("HTTP")
   HTTP,

   @XmlEnumValue("HTTPS")
   HTTPS,

   @XmlEnumValue("TCP")
   TCP,

   @XmlEnumValue("UDP")
   UDP,

   @XmlEnumValue("IPSEC")
   IPSEC,

   @XmlEnumValue("FTP")
   FTP,

   @XmlEnumValue("Any")
   Any;

   @Override
   public String toString() {
      return name();
   }
}