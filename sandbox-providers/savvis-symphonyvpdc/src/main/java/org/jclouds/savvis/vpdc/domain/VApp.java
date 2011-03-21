package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.ovf.NetworkSection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A virtual application (vApp) is a software solution, packaged in OVF containing one or more
 * virtual machines. A vApp can be authored by Developers at ISVs and VARs or by IT Administrators
 * in Enterprises and Service Providers.
 * 
 * @author Adrian Cole
 */
public class VApp extends Resource {
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

   public static class Builder extends Resource.Builder {
      private Status status;
      private String ipAddress;
      private Integer osType;
      private String osDescripton;
      private NetworkSection networkSection;
      private Set<ResourceAllocationSettingData> resourceAllocations = Sets.newLinkedHashSet();

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder osType(Integer osType) {
         this.osType = osType;
         return this;
      }

      public Builder networkSection(NetworkSection networkSection) {
         this.networkSection = networkSection;
         return this;
      }

      public Builder osDescripton(String osDescripton) {
         this.osDescripton = osDescripton;
         return this;
      }

      public Builder resourceAllocation(ResourceAllocationSettingData in) {
         this.resourceAllocations.add(checkNotNull(in, "resourceAllocation"));
         return this;
      }

      public Builder resourceAllocations(Set<ResourceAllocationSettingData> resourceAllocations) {
         this.resourceAllocations.addAll(checkNotNull(resourceAllocations, "resourceAllocations"));
         return this;
      }

      @Override
      public VApp build() {
         return new VApp(id, name, type, href, status, ipAddress, osType, osDescripton, networkSection,
               resourceAllocations);
      }

      public static Builder fromVApp(VApp in) {
         return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
               .status(in.getStatus()).ipAddress(in.getIpAddress()).osType(in.getOsType())
               .networkSection(in.getNetworkSection()).resourceAllocations(in.getResourceAllocations())
               .osDescripton(in.getOsDescripton());
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

   }

   private final Status status;
   private final String ipAddress;
   private final Integer osType;
   private final String osDescripton;
   private final NetworkSection networkSection;
   private final Set<ResourceAllocationSettingData> resourceAllocations;

   public VApp(String id, String name, String type, URI href, Status status, String ipAddress, Integer osType,
         String osDescripton, NetworkSection networkSection, Set<ResourceAllocationSettingData> resourceAllocations) {
      super(id, name, type, href);
      this.status = status;
      this.ipAddress = ipAddress;
      this.osType = osType;
      this.osDescripton = osDescripton;
      this.networkSection = networkSection;
      this.resourceAllocations = ImmutableSet.copyOf(checkNotNull(resourceAllocations, "resourceAllocations"));
   }

   public Status getStatus() {
      return status;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public Integer getOsType() {
      return osType;
   }

   public String getOsDescripton() {
      return osDescripton;
   }

   public NetworkSection getNetworkSection() {
      return networkSection;
   }

   public Set<ResourceAllocationSettingData> getResourceAllocations() {
      return resourceAllocations;
   }

   @Override
   public Builder toBuilder() {
      return Builder.fromVApp(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + ", status=" + status
            + ", ipAddress=" + ipAddress + ", osType=" + osType + ", osDescripton=" + osDescripton
            + ", networkSection=" + networkSection + ", resourceAllocations=" + resourceAllocations + "]";
   }

}