/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.domain.Instance.NetworkInterface.AccessConfig.Type;

/**
 * Optional information for creating an instance.
 *
 * @author David Alves
 */
public class InstanceTemplate {

   protected String name;
   protected String description;
   protected URI machineType;
   protected URI zone;
   protected URI image;
   protected Set<String> tags = Sets.newLinkedHashSet();
   protected Set<Instance.ServiceAccount> serviceAccounts = Sets.newLinkedHashSet();

   protected transient Set<PersistentDisk> disks = Sets.newLinkedHashSet();
   protected transient Set<NetworkInterface> networkInterfaces = Sets.newLinkedHashSet();
   protected transient Map<String, String> metadata = Maps.newLinkedHashMap();
   protected transient String machineTypeName;
   protected transient String zoneName;


   protected InstanceTemplate(URI machineType) {
      this.machineType = checkNotNull(machineType, "machineType");
   }

   protected InstanceTemplate(String machineTypeName) {
      this.machineTypeName = checkNotNull(machineTypeName, "machineTypeName");
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getName()
    */
   public InstanceTemplate name(String name) {
      this.name = name;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getDescription()
    */
   public InstanceTemplate description(String description) {
      this.description = description;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getImage()
    */
   public InstanceTemplate image(URI image) {
      this.image = image;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMachineType()
    */
   public InstanceTemplate machineType(URI machineType) {
      this.machineType = machineType;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMachineType()
    */
   public InstanceTemplate machineType(String machineTypeName) {
      this.machineTypeName = machineTypeName;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getZone()
    */
   public InstanceTemplate zone(String zoneName) {
      this.zoneName = zoneName;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getZone()
    */
   public InstanceTemplate zone(URI zone) {
      this.zone = zone;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getTags()
    */
   public InstanceTemplate addTag(String tag) {
      this.tags.add(checkNotNull(tag, "tag"));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getTags()
    */
   public InstanceTemplate tags(Set<String> tags) {
      this.tags = Sets.newLinkedHashSet();
      this.tags.addAll(checkNotNull(tags, "tags"));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getDisks()
    */
   public InstanceTemplate addDisk(PersistentDisk.Mode mode, URI source) {
      this.disks.add(new PersistentDisk(mode, source, null, null));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getDisks()
    */
   public InstanceTemplate addDisk(PersistentDisk.Mode mode, URI source, String deviceName, Boolean deleteOnTerminate) {
      this.disks.add(new PersistentDisk(mode, source, deviceName, deleteOnTerminate));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getDisks()
    */
   public InstanceTemplate disks(Set<PersistentDisk> disks) {
      this.disks = Sets.newLinkedHashSet();
      this.disks.addAll(checkNotNull(disks, "disks"));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getNetworkInterfaces()
    */
   public InstanceTemplate addNetworkInterface(URI network) {
      this.networkInterfaces.add(new NetworkInterface(checkNotNull(network, "network"), null,null));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getNetworkInterfaces()
    */
   public InstanceTemplate addNetworkInterface(URI network, Type type) {
      this.networkInterfaces.add(new NetworkInterface(checkNotNull(network, "network"), null,
              ImmutableSet.of(Instance.NetworkInterface.AccessConfig.builder()
                      .type(type)
                      .build())));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getNetworkInterfaces()
    */
   public InstanceTemplate addNetworkInterface(NetworkInterface networkInterface) {
      this.networkInterfaces.add(networkInterface);
      return this;
   }

   public InstanceTemplate networkInterfaces(Set<NetworkInterface> networkInterfaces) {
      this.networkInterfaces = Sets.newLinkedHashSet(networkInterfaces);
      return this;
   }


   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMetadata()
    */
   public InstanceTemplate addMetadata(String key, String value) {
      this.metadata.put(checkNotNull(key, "key"), checkNotNull(value, "value of %", key));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMetadata()
    */
   public InstanceTemplate metadata(Map<String, String> metadata) {
      this.metadata = Maps.newLinkedHashMap();
      this.metadata.putAll(checkNotNull(metadata, "metadata"));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getServiceAccounts()
    */
   public InstanceTemplate addServiceAccount(Instance.ServiceAccount serviceAccount) {
      this.serviceAccounts.add(checkNotNull(serviceAccount, "serviceAccount"));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getServiceAccounts()
    */
   public InstanceTemplate serviceAccounts(Set<Instance.ServiceAccount> serviceAccounts) {
      this.serviceAccounts = Sets.newLinkedHashSet();
      this.serviceAccounts.addAll(checkNotNull(serviceAccounts, "serviceAccounts"));
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getDescription()
    */
   public String getDescription() {
      return description;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getDisks()
    */
   public Set<PersistentDisk> getDisks() {
      return disks;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getImage()
    */
   public URI getImage() {
      return image;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMachineType()
    */
   public URI getMachineType() {
      return machineType;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMachineType()
    */
   public String getMachineTypeName() {
      return machineTypeName;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getMetadata()
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getNetworkInterfaces()
    */
   public Set<NetworkInterface> getNetworkInterfaces() {
      return networkInterfaces;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getServiceAccounts()
    */
   public Set<Instance.ServiceAccount> getServiceAccounts() {
      return serviceAccounts;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getTags()
    */
   public Set<String> getTags() {
      return tags;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getName()
    */
   public String getName() {
      return name;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getZone()
    */
   public URI getZone() {
      return zone;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Instance#getZone()
    */
   public String getZoneName() {
      return zoneName;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static InstanceTemplate fromInstanceTemplate(InstanceTemplate instanceTemplate) {
      return Builder.fromInstanceTemplate(instanceTemplate);
   }

   public static class Builder {

      public InstanceTemplate forMachineType(URI machineType) {
         return new InstanceTemplate(machineType);
      }

      public InstanceTemplate forMachineType(String machineTypeName) {
         return new InstanceTemplate(machineTypeName);
      }

      public static InstanceTemplate fromInstanceTemplate(InstanceTemplate instanceTemplate) {
         return InstanceTemplate.builder()
                 .forMachineType(instanceTemplate.getMachineType())
                 .networkInterfaces(instanceTemplate.getNetworkInterfaces())
                 .name(instanceTemplate.getName())
                 .description(instanceTemplate.getDescription())
                 .zone(instanceTemplate.getZone())
                 .image(instanceTemplate.getImage())
                 .tags(instanceTemplate.getTags())
                 .disks(instanceTemplate.getDisks())
                 .metadata(instanceTemplate.getMetadata())
                 .serviceAccounts(instanceTemplate.getServiceAccounts());
      }
   }


   public static class PersistentDisk {

      public enum Mode {
         READ_WRITE,
         READ_ONLY
      }

      public PersistentDisk(Mode mode, URI source, String deviceName, Boolean deleteOnTerminate) {
         this.mode = checkNotNull(mode, "mode");
         this.source = checkNotNull(source, "source");
         this.deviceName = deviceName;
         this.deleteOnTerminate = deleteOnTerminate;
      }

      private final Mode mode;
      private final URI source;
      private final Boolean deleteOnTerminate;
      private final String deviceName;

      /**
       * @return the mode in which to attach this disk, either READ_WRITE or READ_ONLY.
       */
      public Mode getMode() {
         return mode;
      }

      /**
       * @return the URL of the persistent disk resource.
       */
      public URI getSource() {
         return source;
      }

      /**
       * @return Must be unique within the instance when specified. This represents a unique
       *         device name that is reflected into the /dev/ tree of a Linux operating system running within the
       *         instance. If not specified, a default will be chosen by the system.
       */
      public String getDeviceName() {
         return deviceName;
      }


      /**
       * @return If true, delete the disk and all its data when the associated instance is deleted.
       */
      public boolean isDeleteOnTerminate() {
         return deleteOnTerminate;
      }
   }

   public static class NetworkInterface {

      private final URI network;
      private final String networkIP;
      private final Set<Instance.NetworkInterface.AccessConfig> accessConfigs;

      public NetworkInterface(URI network, String networkIP, Set<Instance.NetworkInterface.AccessConfig>
              accessConfigs) {
         this.networkIP = networkIP;
         this.network = network;
         this.accessConfigs = accessConfigs != null ? accessConfigs : ImmutableSet.<Instance.NetworkInterface.AccessConfig>of();
      }

      public Set<Instance.NetworkInterface.AccessConfig> getAccessConfigs() {
         return accessConfigs;
      }

      public URI getNetwork() {
         return network;
      }

      public String getNetworkIP() {
         return networkIP;
      }
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof InstanceTemplate) {
         final InstanceTemplate other = InstanceTemplate.class.cast(object);
         return equal(description, other.description)
                 && equal(tags, other.tags)
                 && equal(image, other.image)
                 && equal(disks, other.disks)
                 && equal(networkInterfaces, other.networkInterfaces)
                 && equal(metadata, other.metadata)
                 && equal(serviceAccounts, other.serviceAccounts);
      } else {
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(description, tags, image, disks, networkInterfaces, metadata, serviceAccounts);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      Objects.ToStringHelper toString = Objects.toStringHelper("")
              .omitNullValues();
      toString.add("description", description);
      if (tags.size() > 0)
         toString.add("tags", tags);
      if (disks.size() > 0)
         toString.add("disks", disks);
      if (metadata.size() > 0)
         toString.add("metadata", metadata);
      if (serviceAccounts.size() > 0)
         toString.add("serviceAccounts", serviceAccounts);
      toString.add("image", image);
      toString.add("networkInterfaces", networkInterfaces);
      return toString;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
