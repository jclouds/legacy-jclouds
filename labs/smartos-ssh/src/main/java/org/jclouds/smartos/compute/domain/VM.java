package org.jclouds.smartos.compute.domain;

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

      private Optional<String> publicAddress = Optional.absent();
      private UUID uuid;
      private String type;
      private String ram;
      private State state = State.STOPPED;
      private String alias;

      public Builder uuid(UUID uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = UUID.fromString(uuid);
         return this;
      }

      public Builder publicAddress(String publicAddress) {
         this.publicAddress = Optional.fromNullable(publicAddress);
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
         return new VM(publicAddress, uuid, type, ram, state, alias);
      }

      public Builder fromVM(VM in) {
         return publicAddress(in.getPublicAddress().orNull()).uuid(in.getUuid()).type(in.getType()).ram(in.getRam())
                  .state(in.getState()).alias(in.getAlias());
      }
   }

   private Optional<String> publicAddress;
   private final UUID uuid;
   private String type;
   private String ram;
   private State state;
   private String alias;

   protected VM(Optional<String> publicAddress, UUID uuid, String type, String ram, State state, String alias) {
      this.publicAddress = publicAddress;
      this.uuid = uuid;
      this.type = type;
      this.ram = ram;
      this.state = state;
      this.alias = alias;
   }

   public State getState() {
      return state;
   }

   public Optional<String> getPublicAddress() {
      return publicAddress;
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
      return uuid.equals(((VM) obj).getUuid());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("uuid", uuid).add("type", type).add("ram", ram)
               .add("alias", alias).add("publicAddress", publicAddress.orNull()).toString();
   }
}
