package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 * 
 */
public enum IpProtocol {

   TCP, UDP, ICMP;

   public String value() {
      return name().toLowerCase();
   }

   @Override
   public String toString() {
      return value();
   }

   public static IpProtocol fromValue(String protocol) {
      return valueOf(checkNotNull(protocol, "protocol").toUpperCase());
   }

}