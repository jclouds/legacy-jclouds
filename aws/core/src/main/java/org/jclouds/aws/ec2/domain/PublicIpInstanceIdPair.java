package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;

import com.google.inject.internal.Nullable;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-DescribeAddressesResponseInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class PublicIpInstanceIdPair implements Comparable<PublicIpInstanceIdPair> {
   @Nullable
   private final String instanceId;
   private final InetAddress publicIp;

   public PublicIpInstanceIdPair(InetAddress publicIp, @Nullable String instanceId) {
      this.instanceId = instanceId;
      this.publicIp = checkNotNull(publicIp, "publicIp");
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(PublicIpInstanceIdPair o) {
      return (this == o) ? 0 : getPublicIp().getHostAddress().compareTo(
               o.getPublicIp().getHostAddress());
   }

   /**
    * The ID of the instance.
    */
   public String getInstanceId() {
      return instanceId;
   }

   /**
    * The public IP address.
    */
   public InetAddress getPublicIp() {
      return publicIp;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((publicIp == null) ? 0 : publicIp.hashCode());
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
      PublicIpInstanceIdPair other = (PublicIpInstanceIdPair) obj;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (publicIp == null) {
         if (other.publicIp != null)
            return false;
      } else if (!publicIp.equals(other.publicIp))
         return false;
      return true;
   }

}