package org.jclouds.nodepool.internal;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.ResourceMetadata;

public class PoolNodeMetadata implements NodeMetadata {

   private NodeMetadata backingNodeMetadata;
   private String groupName;

   public PoolNodeMetadata(NodeMetadata backingNodeMetadata, String groupName) {
      this.backingNodeMetadata = backingNodeMetadata;
      this.groupName = groupName;
   }

   @Override
   public String getGroup() {
      return this.groupName;
   }

   @Override
   public Status getStatus() {
      return backingNodeMetadata.getStatus();
   }

   @Override
   public String getBackendStatus() {
      return backingNodeMetadata.getBackendStatus();
   }

   @Override
   public ComputeType getType() {
      return backingNodeMetadata.getType();
   }

   @Override
   public String getProviderId() {
      return backingNodeMetadata.getProviderId();
   }

   @Override
   public String getName() {
      return backingNodeMetadata.getName();
   }

   @Override
   public String getId() {
      return backingNodeMetadata.getId();
   }

   @Override
   public Set<String> getTags() {
      return backingNodeMetadata.getTags();
   }

   @Override
   public Location getLocation() {
      return backingNodeMetadata.getLocation();
   }

   @Override
   public URI getUri() {
      return backingNodeMetadata.getUri();
   }

   @Override
   public Map<String, String> getUserMetadata() {
      return backingNodeMetadata.getUserMetadata();
   }

   @Override
   public int compareTo(ResourceMetadata<ComputeType> o) {
      return backingNodeMetadata.compareTo(o);
   }

   @Override
   public String getHostname() {
      return backingNodeMetadata.getHostname();
   }

   @Override
   public Hardware getHardware() {
      return backingNodeMetadata.getHardware();
   }

   @Override
   public String getImageId() {
      return backingNodeMetadata.getImageId();
   }

   @Override
   public OperatingSystem getOperatingSystem() {
      return backingNodeMetadata.getOperatingSystem();
   }

   @Override
   public NodeState getState() {
      return backingNodeMetadata.getState();
   }

   @Override
   public int getLoginPort() {
      return backingNodeMetadata.getLoginPort();
   }

   @Override
   public String getAdminPassword() {
      return backingNodeMetadata.getAdminPassword();
   }

   @Override
   public LoginCredentials getCredentials() {
      return backingNodeMetadata.getCredentials();
   }

   @Override
   public Set<String> getPublicAddresses() {
      return backingNodeMetadata.getPublicAddresses();
   }

   @Override
   public Set<String> getPrivateAddresses() {
      return backingNodeMetadata.getPrivateAddresses();
   }

}