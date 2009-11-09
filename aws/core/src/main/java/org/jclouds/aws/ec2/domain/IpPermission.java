package org.jclouds.aws.ec2.domain;

import java.util.SortedSet;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-IpPermissionType.html"
 *      />
 * @author Adrian Cole
 */
public class IpPermission implements Comparable<IpPermission> {
   private final int fromPort;
   private final int toPort;
   private final SortedSet<UserIdGroupPair> groups;
   private final IpProtocol ipProtocol;
   private final SortedSet<String> ipRanges;

   public IpPermission(int fromPort, int toPort, SortedSet<UserIdGroupPair> groups,
            IpProtocol ipProtocol, SortedSet<String> ipRanges) {
      this.fromPort = fromPort;
      this.toPort = toPort;
      this.groups = groups;
      this.ipProtocol = ipProtocol;
      this.ipRanges = ipRanges;
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(IpPermission o) {
      return (this == o) ? 0 : getIpProtocol().compareTo(o.getIpProtocol());
   }

   /**
    * Start of port range for the TCP and UDP protocols, or an ICMP type number. An ICMP type number
    * of -1 indicates a wildcard (i.e., any ICMP type number).
    */
   public int getFromPort() {
      return fromPort;
   }

   /**
    * End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of -1 indicates
    * a wildcard (i.e., any ICMP code).
    */
   public int getToPort() {
      return toPort;
   }

   /**
    * List of security group and user ID pairs.
    */
   public SortedSet<UserIdGroupPair> getGroups() {
      return groups;
   }

   /**
    * IP protocol
    */
   public IpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * IP ranges.
    */
   public SortedSet<String> getIpRanges() {
      return ipRanges;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + fromPort;
      result = prime * result + ((groups == null) ? 0 : groups.hashCode());
      result = prime * result + ((ipProtocol == null) ? 0 : ipProtocol.hashCode());
      result = prime * result + ((ipRanges == null) ? 0 : ipRanges.hashCode());
      result = prime * result + toPort;
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
      IpPermission other = (IpPermission) obj;
      if (fromPort != other.fromPort)
         return false;
      if (groups == null) {
         if (other.groups != null)
            return false;
      } else if (!groups.equals(other.groups))
         return false;
      if (ipProtocol == null) {
         if (other.ipProtocol != null)
            return false;
      } else if (!ipProtocol.equals(other.ipProtocol))
         return false;
      if (ipRanges == null) {
         if (other.ipRanges != null)
            return false;
      } else if (!ipRanges.equals(other.ipRanges))
         return false;
      if (toPort != other.toPort)
         return false;
      return true;
   }

}