/**
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
package org.jclouds.cloudstack.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.encryption.internal.Base64;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Map;

/**
 * Options used to control what disk offerings are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api/user/deployVirtualMachine.html"
 *      />
 * @author Adrian Cole
 */
public class DeployVirtualMachineOptions extends AccountInDomainOptions {

   public static final DeployVirtualMachineOptions NONE = new DeployVirtualMachineOptions();

   /**
    * the ID of the disk offering for the virtual machine. If the template is of
    * ISO format, the diskOfferingId is for the root disk volume. Otherwise this
    * parameter is used to dinidcate the offering for the data disk volume. If
    * the templateId parameter passed is from a Template object, the
    * diskOfferingId refers to a DATA Disk Volume created. If the templateId
    * parameter passed is from an ISO object, the diskOfferingId refers to a
    * ROOT Disk Volume created.
    * 
    * @param diskofferingid
    *           the ID of the disk offering
    */
   public DeployVirtualMachineOptions diskOfferingId(long diskofferingid) {
      checkArgument(!queryParameters.containsKey("size"), "Mutually exclusive with size");
      this.queryParameters.replaceValues("diskofferingid", ImmutableSet.of(diskofferingid + ""));
      return this;
   }

   /**
    * @param displayName
    *           an optional user generated name for the virtual machine
    */
   public DeployVirtualMachineOptions displayName(String displayName) {
      this.queryParameters.replaceValues("displayname", ImmutableSet.of(displayName));
      return this;
   }

   /**
    * @param group
    *           an optional group for the virtual machine
    */
   public DeployVirtualMachineOptions group(String group) {
      this.queryParameters.replaceValues("group", ImmutableSet.of(group));
      return this;
   }

   /**
    * @param hypervisor
    *           the hypervisor on which to deploy the virtual machine
    */
   public DeployVirtualMachineOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.of(hypervisor));
      return this;
   }

   /**
    * @param keyPair
    *           name of the ssh key pair used to login to the virtual machine
    */
   public DeployVirtualMachineOptions keyPair(String keyPair) {
      this.queryParameters.replaceValues("keypair", ImmutableSet.of(keyPair));
      return this;
   }

   /**
    * @param name
    *           host name for the virtual machine
    */
   public DeployVirtualMachineOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param ipOnDefaultNetwork
    *          the requested ip address (2.2.12 only option)
    */
   public DeployVirtualMachineOptions ipOnDefaultNetwork(String ipOnDefaultNetwork) {
      this.queryParameters.replaceValues("ipaddress", ImmutableSet.of(ipOnDefaultNetwork));
      return this;
   }

   /**
    * @param ipsToNetworks
    *          mapping ip addresses to network ids (2.2.12 only option)
    */
   public DeployVirtualMachineOptions ipsToNetworks(Map<String, Long> ipsToNetworks) {
      int count = 0;
      for(String ip : ipsToNetworks.keySet()) {
         this.queryParameters.replaceValues(
            String.format("ipnetworklist[%d].ip", count),
            ImmutableSet.of(ip)
         );
         this.queryParameters.replaceValues(
            String.format("ipnetworklist[%d].networkid", count),
            ImmutableSet.of("" + ipsToNetworks.get(ip))
         );
         count += 1;
      }
      return this;
   }

   /**
    * @param networkId
    *           network id used by virtual machine
    */
   public DeployVirtualMachineOptions networkId(long networkId) {
      this.queryParameters.replaceValues("networkids", ImmutableSet.of(networkId + ""));
      return this;
   }

   /**
    * @param networkIds
    *           network ids used by virtual machine
    */
   public DeployVirtualMachineOptions networkIds(Iterable<Long> networkIds) {
      this.queryParameters.replaceValues("networkids", ImmutableSet.of(Joiner.on(',').join(networkIds)));
      return this;
   }

   public Iterable<Long> getNetworkIds() {
      if (queryParameters.get("networkids").size() == 1) {
         return Iterables.transform(
               Splitter.on(",").split(Iterables.getOnlyElement(queryParameters.get("networkids"))),
               new Function<String, Long>() {

                  @Override
                  public Long apply(String arg0) {
                     return Long.parseLong(arg0);
                  }

               });
      } else {
         return ImmutableSet.<Long> of();
      }
   }

   /**
    * @param securityGroupId
    *           security group applied to the virtual machine. Should be passed
    *           only when vm is created from a zone with Basic Network support
    */
   public DeployVirtualMachineOptions securityGroupId(long securityGroupId) {
      this.queryParameters.replaceValues("securitygroupids", ImmutableSet.of(securityGroupId + ""));
      return this;
   }

   /**
    * @param securityGroupIds
    *           security groups applied to the virtual machine. Should be passed
    *           only when vm is created from a zone with Basic Network support
    */
   public DeployVirtualMachineOptions securityGroupIds(Iterable<Long> securityGroupIds) {
      this.queryParameters.replaceValues("securitygroupids", ImmutableSet.of(Joiner.on(',').join(securityGroupIds)));
      return this;
   }

   /**
    * @param dataDiskSize
    *           the arbitrary size for the DATADISK volume. Mutually exclusive
    *           with diskOfferingId
    */
   public DeployVirtualMachineOptions dataDiskSize(long dataDiskSize) {
      checkArgument(!queryParameters.containsKey("diskofferingid"), "Mutually exclusive with diskOfferingId");
      this.queryParameters.replaceValues("size", ImmutableSet.of(dataDiskSize + ""));
      return this;
   }

   /**
    * @param unencodedData
    *           an optional binary data that can be sent to the virtual machine
    *           upon a successful deployment. This binary data must be base64
    *           encoded before adding it to the request. Currently only HTTP GET
    *           is supported. Using HTTP GET (via querystring), you can send up
    *           to 2KB of data after base64 encoding.
    */
   public DeployVirtualMachineOptions userData(byte[] unencodedData) {
      int length = checkNotNull(unencodedData, "unencodedData").length;
      checkArgument(length > 0, "userData cannot be empty");
      checkArgument(length <= 2 * 1024, "userData cannot be larger than 2kb");
      this.queryParameters.replaceValues("userdata", ImmutableSet.of(Base64.encodeBytes(unencodedData)));
      return this;
   }

   public static class Builder {

      /**
       * @see DeployVirtualMachineOptions#diskOfferingId
       */
      public static DeployVirtualMachineOptions diskOfferingId(long diskOfferingId) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.diskOfferingId(diskOfferingId);
      }

      /**
       * @see DeployVirtualMachineOptions#displayName
       */
      public static DeployVirtualMachineOptions displayName(String displayName) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.displayName(displayName);
      }

      /**
       * @see DeployVirtualMachineOptions#group
       */
      public static DeployVirtualMachineOptions group(String group) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.group(group);
      }

      /**
       * @see DeployVirtualMachineOptions#hypervisor
       */
      public static DeployVirtualMachineOptions hypervisor(String hypervisor) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.hypervisor(hypervisor);
      }

      /**
       * @see DeployVirtualMachineOptions#keyPair
       */
      public static DeployVirtualMachineOptions keyPair(String keyPair) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.keyPair(keyPair);
      }

      /**
       * @see DeployVirtualMachineOptions#name
       */
      public static DeployVirtualMachineOptions name(String name) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.name(name);
      }

      /**
       * @see DeployVirtualMachineOptions#ipOnDefaultNetwork
       */
      public static DeployVirtualMachineOptions ipOnDefaultNetwork(String ipOnDefaultNetwork) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.ipOnDefaultNetwork(ipOnDefaultNetwork);
      }

      /**
       * @see DeployVirtualMachineOptions#ipsToNetworks
       */
      public static DeployVirtualMachineOptions ipsToNetworks(Map<String, Long> ipsToNetworks) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.ipsToNetworks(ipsToNetworks);
      }

      /**
       * @see DeployVirtualMachineOptions#networkId
       */
      public static DeployVirtualMachineOptions networkId(long id) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.networkId(id);
      }

      /**
       * @see DeployVirtualMachineOptions#networkIds
       */
      public static DeployVirtualMachineOptions networkIds(Iterable<Long> networkIds) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.networkIds(networkIds);
      }

      /**
       * @see DeployVirtualMachineOptions#securityGroupId
       */
      public static DeployVirtualMachineOptions securityGroupId(long id) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.securityGroupId(id);
      }

      /**
       * @see DeployVirtualMachineOptions#securityGroupIds
       */
      public static DeployVirtualMachineOptions securityGroupIds(Iterable<Long> securityGroupIds) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.securityGroupIds(securityGroupIds);
      }

      /**
       * @see DeployVirtualMachineOptions#dataDiskSize
       */
      public static DeployVirtualMachineOptions dataDiskSize(long id) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.dataDiskSize(id);
      }

      /**
       * @see DeployVirtualMachineOptions#userData
       */
      public static DeployVirtualMachineOptions userData(byte[] unencodedData) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.userData(unencodedData);
      }

      /**
       * @see DeployVirtualMachineOptions#accountInDomain
       */
      public static DeployVirtualMachineOptions accountInDomain(String account, long domain) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see DeployVirtualMachineOptions#domainId
       */
      public static DeployVirtualMachineOptions domainId(long domainId) {
         DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeployVirtualMachineOptions accountInDomain(String account, long domain) {
      return DeployVirtualMachineOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeployVirtualMachineOptions domainId(long domainId) {
      return DeployVirtualMachineOptions.class.cast(super.domainId(domainId));
   }
}
