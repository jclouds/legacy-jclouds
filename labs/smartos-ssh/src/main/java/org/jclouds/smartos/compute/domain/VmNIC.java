package org.jclouds.smartos.compute.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Specification of a network card.
 */
public class VmNIC {

   @SerializedName("nic_tag")
   protected final String tag;
   protected final String ip;
   protected final String netmask;
   protected final String gateway;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVmNIC(this);
   }

   public static class Builder {

      public String tag = "admin";
      public String ip;
      public String netmask;
      public String gateway;

      public Builder simpleDHCPNic() {
         tag = "admin";
         ip = "dhcp";
         return this;
      }

      public Builder tag(String tag) {
         this.tag = tag;
         return this;
      }

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public VmNIC build() {
         return new VmNIC(tag, ip, netmask, gateway);
      }

      public Builder fromVmNIC(VmNIC in) {
         return tag(in.getTag()).ip(in.getIp()).netmask(in.getNetmask()).gateway(in.getGateway());
      }
   }

   public VmNIC(String tag, String ip, String netmask, String gateway) {
      this.tag = tag;
      this.ip = ip;
      this.netmask = netmask;
      this.gateway = gateway;
   }

   public String getTag() {
      return tag;
   }

   public String getIp() {
      return ip;
   }

   public String getNetmask() {
      return netmask;
   }

   public String getGateway() {
      return gateway;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("tag", tag).add("ip", ip).add("netmask", netmask)
               .add("gateway", gateway).toString();
   }
}
