package org.jclouds.vcloud.hostingdotcom.domain.internal;

import java.net.InetAddress;
import java.net.URI;
import java.util.SortedSet;

import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;

import com.google.common.collect.ListMultimap;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class HostingDotComVAppImpl extends VAppImpl implements HostingDotComVApp {

   private final String username;
   private final String password;;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public HostingDotComVAppImpl(String id, String name, URI location, VAppStatus status, Long size,
            ListMultimap<String, InetAddress> networkToAddresses,
            String operatingSystemDescription, VirtualSystem system,
            SortedSet<ResourceAllocation> resourceAllocations, String username, String password) {
      super(id, name, location, status, size, networkToAddresses, operatingSystemDescription,
               system, resourceAllocations);
      this.username = username;
      this.password = password;

   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + ((username == null) ? 0 : username.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      HostingDotComVAppImpl other = (HostingDotComVAppImpl) obj;
      if (password == null) {
         if (other.password != null)
            return false;
      } else if (!password.equals(other.password))
         return false;
      if (username == null) {
         if (other.username != null)
            return false;
      } else if (!username.equals(other.username))
         return false;
      return true;
   }
}