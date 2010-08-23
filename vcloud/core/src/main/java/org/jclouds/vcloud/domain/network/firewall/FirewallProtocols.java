package org.jclouds.vcloud.domain.network.firewall;

/**
 * The Protocols element specifies the protocols to which firewall rules apply.
 * 
 * @since vcloud api 0.9 emulated for 0.8
 * 
 * 
 */
public class FirewallProtocols {
   private final boolean tcp;
   private final boolean udp;

   public FirewallProtocols(boolean tcp, boolean udp) {
      this.tcp = tcp;
      this.udp = udp;
   }

   /**
    * @return true if the firewall rules apply to the TCP protocol
    */
   public boolean isTcp() {
      return tcp;
   }

   /**
    * @return true if the firewall rules apply to the UDP protocol
    */
   public boolean isUdp() {
      return udp;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (tcp ? 1231 : 1237);
      result = prime * result + (udp ? 1231 : 1237);
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
      FirewallProtocols other = (FirewallProtocols) obj;
      if (tcp != other.tcp)
         return false;
      if (udp != other.udp)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Protocols [tcp=" + tcp + ", udp=" + udp + "]";
   }

}