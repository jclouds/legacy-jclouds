package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DnsServer")
public class DNSServer {

   /**
    * Specifies the name of the DNS server.
    */
   @XmlElement(required = true, name = "Name")
   private String name;

   /**
    * Specifies the IP address of the DNS server.
    */
   @XmlElement(required = true, name = "Address")
   private String address;

   public DNSServer() {
      super();
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DNSServer other = (DNSServer) obj;
      if (address == null) {
         if (other.address != null)
            return false;
      } else if (!address.equals(other.address))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "DNSServer [name=" + name + ", address=" + address + "]";
   }

}
