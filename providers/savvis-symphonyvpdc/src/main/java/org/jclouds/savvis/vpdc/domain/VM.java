/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import org.jclouds.ovf.NetworkSection;
import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.ovf.ProductSection;
import org.jclouds.ovf.Section;
import org.jclouds.ovf.VirtualHardwareSection;
import org.jclouds.ovf.internal.BaseVirtualSystem;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * A virtual application (vApp) is a software solution, packaged in OVF containing one or more
 * virtual machines. A vApp can be authored by Developers at ISVs and VARs or by IT Administrators
 * in Enterprises and Service Providers.
 * 
 * @author Adrian Cole
 */
public class VM extends BaseVirtualSystem<VM> implements Resource {
   /**
    * Objects such as vAppTemplate, vApp, and Vm have a status attribute whose value indicates the
    * state of the object. Status for an object, such as a vAppTemplate or vApp, whose Children (Vm
    * objects) each have a status of their own, is computed from the status of the Children.
    * 
    * <h2>NOTE</h2>
    * <p/>
    * The deployment status of an object is indicated by the value of its deployed attribute.
    * 
    * @since vcloud api 0.8
    * 
    * @author Adrian Cole
    */
   public enum Status {

      /**
       * When the VM is in Designing,Saved,Inqueue, has issue in pre provisioning or any exception
       * cases. VM is not in Savvis VPDC (may the VM has been removed) or cannot get VM state due to
       * unknown exception
       */
      UNRESOLVED,
      /**
       * When the Savvis VPDC is in Provisioning, PartiallyDeployed, Failed and the VM failed in
       * provisioning or pending infrastructure notification
       */
      RESOLVED,
      /**
       * When the VM is deployed in vmware and powered off.
       */
      OFF,
      /**
       * We do not support suspended state.
       */
      SUSPENDED,
      /**
       * When the VM is deployed in vmware and powered on.
       */
      ON,
      /**
       * The VM is deployed in vmware but the state of VM may be Uninitialized, Start, Stop, Resume,
       * Reset, RebootGuest, Error, Failed, Unknown, PoweringOn, PoweringOff, Suspending, Stopping,
       * Starting, Resetting, RebootingGuest. Please call back the Get VApp Power State API after
       * few minute.
       */
      UNKNOWN, UNRECOGNIZED;

      public String value() {
         switch (this) {
            case UNRESOLVED:
               return "0";
            case RESOLVED:
               return "1";
            case OFF:
               return "2";
            case SUSPENDED:
               return "3";
            case ON:
               return "4";
            case UNKNOWN:
               return "5";
            default:
               return "UNRECOGNIZED";
         }
      }

      public static Status fromValue(String status) {
         try {
            return fromValue(Integer.parseInt(checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

      public static Status fromValue(int v) {
         switch (v) {
            case 0:
               return UNRESOLVED;
            case 1:
               return RESOLVED;
            case 2:
               return OFF;
            case 3:
               return SUSPENDED;
            case 4:
               return ON;
            case 5:
               return UNKNOWN;
            default:
               return UNRECOGNIZED;
         }
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends BaseVirtualSystem.Builder<VM> {
      protected String type;
      protected URI href;
      protected Status status;
      protected NetworkSection networkSection;
      protected Set<NetworkConfigSection> networkConfigSections = Sets.newLinkedHashSet();
      protected Set<NetworkConnectionSection> networkConnectionSections = Sets.newLinkedHashSet();

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder networkSection(NetworkSection networkSection) {
         this.networkSection = networkSection;
         return this;
      }

      /**
       * @see VM#getNetworkConfigSections
       */
      public Builder networkConfigSection(NetworkConfigSection networkConfigSection) {
         this.networkConfigSections.add(checkNotNull(networkConfigSection, "networkConfigSection"));
         return this;
      }

      /**
       * @see VM#getNetworkConfigSections
       */
      public Builder networkConfigSections(Iterable<NetworkConfigSection> networkConfigSections) {
         this.networkConfigSections = ImmutableSet.<NetworkConfigSection> copyOf(checkNotNull(networkConfigSections,
                  "networkConfigSections"));
         return this;
      }

      /**
       * @see VM#getNetworkConnectionSections
       */
      public Builder networkConnectionSection(NetworkConnectionSection networkConnectionSection) {
         this.networkConnectionSections.add(checkNotNull(networkConnectionSection, "networkConnectionSection"));
         return this;
      }

      /**
       * @see VM#getNetworkConnectionSections
       */
      public Builder networkConnectionSections(Iterable<NetworkConnectionSection> networkConnectionSections) {
         this.networkConnectionSections = ImmutableSet.<NetworkConnectionSection> copyOf(checkNotNull(
                  networkConnectionSections, "networkConnectionSections"));
         return this;
      }

      @Override
      public VM build() {
         return new VM(id, info, name, operatingSystem, virtualHardwareSections, productSections, additionalSections, type,
                  href, status, networkSection, networkConfigSections, networkConnectionSections);
      }

      public Builder fromVM(VM in) {
         return fromVirtualSystem(in).type(in.getType()).href(in.getHref()).status(in.getStatus()).networkSection(
                  in.getNetworkSection()).networkConfigSections(in.getNetworkConfigSections())
                  .networkConnectionSections(in.getNetworkConnectionSections());
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder additionalSection(String name, Section additionalSection) {
         return Builder.class.cast(super.additionalSection(name, additionalSection));
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder additionalSections(Multimap<String, Section> additionalSections) {
         return Builder.class.cast(super.additionalSections(additionalSections));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<VM> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromVirtualSystem(BaseVirtualSystem<VM> in) {
         return Builder.class.cast(super.fromVirtualSystem(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder virtualHardwareSection(VirtualHardwareSection virtualHardwareSection) {
         return Builder.class.cast(super.virtualHardwareSection(virtualHardwareSection));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder virtualHardwareSections(Iterable<? extends VirtualHardwareSection> virtualHardwareSections) {
         return Builder.class.cast(super.virtualHardwareSections(virtualHardwareSections));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder operatingSystemSection(OperatingSystemSection operatingSystem) {
         return Builder.class.cast(super.operatingSystemSection(operatingSystem));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder productSection(ProductSection productSection) {
         return Builder.class.cast(super.productSection(productSection));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder productSections(Iterable<? extends ProductSection> productSections) {
         return Builder.class.cast(super.productSections(productSections));
      }
   }

   protected final String type;
   protected final URI href;
   protected final Status status;
   protected final NetworkSection networkSection;
   protected final Set<NetworkConfigSection> networkConfigSections;
   protected final Set<NetworkConnectionSection> networkConnectionSections;

   @SuppressWarnings("unchecked")
   public VM(String id, String info, String name, OperatingSystemSection operatingSystem,
            Iterable<? extends VirtualHardwareSection> virtualHardwareSections,
            Iterable<? extends ProductSection> productSections, Multimap<String, Section> additionalSections,
            String type, URI href, Status status, NetworkSection networkSection,
            Iterable<NetworkConfigSection> networkConfigSections,
            Iterable<NetworkConnectionSection> networkConnectionSections) {
      super(id, info, name, operatingSystem, virtualHardwareSections, productSections, additionalSections);
      this.type = type;
      this.href = href;
      this.status = status;
      this.networkSection = networkSection;
      this.networkConfigSections = ImmutableSet.copyOf(checkNotNull(networkConfigSections, "networkConfigSections"));
      this.networkConnectionSections = ImmutableSet.copyOf(checkNotNull(networkConnectionSections,
               "networkConnectionSections"));
   }

   public Status getStatus() {
      return status;
   }

   public NetworkSection getNetworkSection() {
      return networkSection;
   }

   public Set<NetworkConfigSection> getNetworkConfigSections() {
      return networkConfigSections;
   }

   public Set<NetworkConnectionSection> getNetworkConnectionSections() {
      return networkConnectionSections;
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[id=%s, name=%s, info=%s, href=%s,status=%s, type=%s, virtualHardwareSections=%s, operatingSystem=%s, productSections=%s, networkSection=%s,  networkConfigSections=%s, networkConnectionSections=%s, additionalSections=%s]",
                        id, name, info, href, status, type, virtualHardwareSections, operatingSystem, productSections,
                        networkSection, networkConfigSections, networkConnectionSections, additionalSections);
   }

   @Override
   public int compareTo(Resource that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   public Builder toBuilder() {
      return builder().fromVM(this);
   }

}