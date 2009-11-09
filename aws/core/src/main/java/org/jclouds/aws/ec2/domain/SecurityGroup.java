package org.jclouds.aws.ec2.domain;

import java.util.SortedSet;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-SecurityGroupItemType.html"
 *      />
 * @author Adrian Cole
 */
public class SecurityGroup implements Comparable<SecurityGroup> {
   private final String name;
   private final String ownerId;
   private final String description;
   private final SortedSet<IpPermission> ipPermissions;

   public SecurityGroup(String name, String ownerId, String description,
            SortedSet<IpPermission> ipPermissions) {
      this.name = name;
      this.ownerId = ownerId;
      this.description = description;
      this.ipPermissions = ipPermissions;
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(SecurityGroup o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   /**
    * Name of the security group.
    */
   public String getName() {
      return name;
   }

   /**
    * AWS Access Key ID of the owner of the security group.
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * Description of the security group.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Set of IP permissions associated with the security group.
    */
   public SortedSet<IpPermission> getIpPermissions() {
      return ipPermissions;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((ipPermissions == null) ? 0 : ipPermissions.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
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
      SecurityGroup other = (SecurityGroup) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (ipPermissions == null) {
         if (other.ipPermissions != null)
            return false;
      } else if (!ipPermissions.equals(other.ipPermissions))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (ownerId == null) {
         if (other.ownerId != null)
            return false;
      } else if (!ownerId.equals(other.ownerId))
         return false;
      return true;
   }
}