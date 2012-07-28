package org.jclouds.smartos.compute.domain;

import java.util.Map;
import java.util.UUID;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Representing a Virtual Machine (Zone / KVM )
 **/
public class VM {

   public enum State {
      RUNNING, STOPPED, INCOMPLETE
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVM(this);
   }

   public static class Builder {

      protected SmartOSHost host;
      protected UUID uuid;
      protected String type;
      protected String ram;
      protected State state = State.STOPPED;
      protected String alias;

      public Builder uuid(UUID uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = UUID.fromString(uuid);
         return this;
      }

      public Builder host(SmartOSHost host) {
         this.host = host;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder ram(String ram) {
         this.ram = ram;
         return this;
      }

      public Builder state(String state) {
         this.state = State.valueOf(state.toUpperCase());
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder alias(String alias) {
         this.alias = alias;
         return this;
      }

      public Builder fromVmadmString(String string) {
         String[] sections = string.split(":");

         uuid(sections[0]);

         type(sections[1]);
         ram(sections[2]);
         state(sections[3]);

         if (sections.length > 4)
            alias(sections[4]);

         return this;
      }

      public VM build() {
         return new VM(host, uuid, type, ram, state, alias);
      }

      public Builder fromVM(VM in) {
         return host(in.getHost()).uuid(in.getUuid()).type(in.getType()).ram(in.getRam()).state(in.getState())
                  .alias(in.getAlias());
      }
   }

   protected SmartOSHost host;
   protected final UUID uuid;
   protected String type;
   protected String ram;
   protected State state;
   protected String alias;

   public VM(SmartOSHost host, UUID uuid, String type, String ram, State state, String alias) {
      this.host = host;
      this.uuid = uuid;
      this.type = type;
      this.ram = ram;
      this.state = state;
      this.alias = alias;
   }

   public State getState() {
      return state;
   }

   public void destroy() {
      host.destroyHost(uuid);
   }

   public void reboot() {
      host.rebootHost(uuid);
   }

   public void stop() {
      host.stopHost(uuid);
   }

   public void start() {
      host.startHost(uuid);
   }

   public Optional<String> getPublicAddress() throws InterruptedException {
      Map<String, String> ipAddresses;

      for (int i = 0; i < 30; i++) {
         ipAddresses = host.getVMIpAddresses(uuid);
         if (!ipAddresses.isEmpty()) {
            // Got some
            String ip = ipAddresses.get("net0");
            if (ip != null && !ip.equals("0.0.0.0"))
               return Optional.of(ip);
         }

         Thread.sleep(1000);
      }

      return Optional.absent();
   }

   public SmartOSHost getHost() {
      return host;
   }

   public UUID getUuid() {
      return uuid;
   }

   public String getType() {
      return type;
   }

   public String getRam() {
      return ram;
   }

   public String getAlias() {
      return alias;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      // UUID is primary key
      return uuid.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      return uuid.equals(((DataSet) obj).getUuid());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("uuid", uuid).add("type", type).add("ram", ram)
               .add("alias", alias).toString();
   }
}
