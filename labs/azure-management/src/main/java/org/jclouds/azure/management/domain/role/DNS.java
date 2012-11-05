package org.jclouds.azure.management.domain.role;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

@XmlRootElement(name = "Dns")
public class DNS {
   /**
    * Contains the parameters specifying the DNS servers to use for the virtual machine.
    */
   @XmlElementWrapper(required = true, name = "DnsServers")
   @XmlElement(name = "DnsServer")
   private List<DNSServer> dnsServers = Lists.newArrayList();

   public DNS() {
      super();
   }

   public List<DNSServer> getDnsServers() {
      return dnsServers;
   }

   public void setDnsServers(List<DNSServer> dnsServers) {
      this.dnsServers = dnsServers;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dnsServers == null) ? 0 : dnsServers.hashCode());
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
      DNS other = (DNS) obj;
      if (dnsServers == null) {
         if (other.dnsServers != null)
            return false;
      } else if (!dnsServers.equals(other.dnsServers))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "DNS [dnsServers=" + dnsServers + "]";
   }

}
