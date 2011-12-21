package org.jclouds.tmrk.enterprisecloud.domain.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Protocol {

   /**
    * HyperText Transfer Protocol
    */
   @XmlEnumValue("HTTP")
   HTTP,

   /**
    * HyperText Transfer Protocol Secure
    */
   @XmlEnumValue("HTTPS")
   HTTPS,

   /**
    * Transmission Control Protocol
    */
   @XmlEnumValue("TCP")
   TCP,

   /**
    * User Datagram Protocol
    */
   @XmlEnumValue("UDP")
   UDP,

   /**
    * Internet Protocol security
    */
   @XmlEnumValue("IPSEC")
   IPSEC,

   /**
    * File Transfer Protocol
    */
   @XmlEnumValue("FTP")
   FTP,

   @XmlEnumValue("Any")
   Any;

   @Override
   public String toString() {
      return name();
   }
   
   public String value() {
      return name();
   }
}