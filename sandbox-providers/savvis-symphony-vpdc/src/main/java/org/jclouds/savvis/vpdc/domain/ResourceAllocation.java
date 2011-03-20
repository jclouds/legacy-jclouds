package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 * 
 */
public class ResourceAllocation {
   public enum Type {
      /**
       * 3 GHz equals 1 vCPU
       * <p/>
       * 1.5 Ghz equals 0.5 vCPU
       */
      PROCESSOR,
      /**
       * Gigabytes
       */
      MEMORY, ETHERNET_ADAPTER,
      /**
       * Gigabytes
       */
      BOOT_DISK,
      /**
       * Gigabytes
       */
      DATA_DISK, UNRECOGNIZED;

      public String value() {
         switch (this) {
         case PROCESSOR:
            return "3";
         case MEMORY:
            return "4";
         case ETHERNET_ADAPTER:
            return "10";
         case BOOT_DISK:
            return "27";
         case DATA_DISK:
            return "26";
         default:
            return "UNRECOGNIZED";
         }
      }

      public static Type fromValue(String type) {
         try {
            return fromValue(Integer.parseInt(checkNotNull(type, "type")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

      public static Type fromValue(int v) {
         switch (v) {
         case 3:
            return PROCESSOR;
         case 4:
            return MEMORY;
         case 10:
            return ETHERNET_ADAPTER;
         case 27:
            return BOOT_DISK;
         case 26:
            return DATA_DISK;
         default:
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected int id;
      protected Type type;
      protected String name;
      protected String description;
      protected String allocationUnits;
      protected long virtualQuantity;
      protected String connection;
      protected String caption;
      protected String hostResource;

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder connection(String connection) {
         this.connection = connection;
         return this;
      }

      public Builder caption(String caption) {
         this.caption = caption;
         return this;
      }

      public Builder hostResource(String hostResource) {
         this.hostResource = hostResource;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder allocationUnits(String allocationUnits) {
         this.allocationUnits = allocationUnits;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder virtualQuantity(long virtualQuantity) {
         this.virtualQuantity = virtualQuantity;
         return this;
      }

      public ResourceAllocation build() {
         return new ResourceAllocation(id, type, name, description, allocationUnits, virtualQuantity, connection,
               caption, hostResource);
      }

      public static Builder fromResourceAllocation(ResourceAllocation in) {
         return new Builder().id(in.getId()).type(in.getType()).name(in.getName()).description(in.getDescription())
               .allocationUnits(in.getAllocationUnits()).virtualQuantity(in.getVirtualQuantity())
               .connection(in.getConnection()).caption(in.getCaption()).hostResource(in.getHostResource());
      }
   }

   protected final int id;
   protected final Type type;
   protected final String name;
   protected final String description;
   protected final String allocationUnits;
   protected final long virtualQuantity;
   protected final String connection;
   protected final String caption;
   protected final String hostResource;

   public ResourceAllocation(int id, Type type, String name, String description, String allocationUnits,
         long virtualQuantity, String connection, String caption, String hostResource) {
      this.id = id;
      this.type = checkNotNull(type, "type");
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.allocationUnits = allocationUnits;
      this.virtualQuantity = virtualQuantity;
      this.connection = connection;
      this.caption = caption;
      this.hostResource = hostResource;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public Type getType() {
      return type;
   }

   public String getAllocationUnits() {
      return allocationUnits;
   }

   public String getConnection() {
      return connection;
   }

   public String getCaption() {
      return caption;
   }

   public long getVirtualQuantity() {
      return virtualQuantity;
   }

   public String getHostResource() {
      return hostResource;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((allocationUnits == null) ? 0 : allocationUnits.hashCode());
      result = prime * result + ((caption == null) ? 0 : caption.hashCode());
      result = prime * result + ((connection == null) ? 0 : connection.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((hostResource == null) ? 0 : hostResource.hashCode());
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + (int) (virtualQuantity ^ (virtualQuantity >>> 32));
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
      ResourceAllocation other = (ResourceAllocation) obj;
      if (allocationUnits == null) {
         if (other.allocationUnits != null)
            return false;
      } else if (!allocationUnits.equals(other.allocationUnits))
         return false;
      if (caption == null) {
         if (other.caption != null)
            return false;
      } else if (!caption.equals(other.caption))
         return false;
      if (connection == null) {
         if (other.connection != null)
            return false;
      } else if (!connection.equals(other.connection))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (hostResource == null) {
         if (other.hostResource != null)
            return false;
      } else if (!hostResource.equals(other.hostResource))
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type != other.type)
         return false;
      if (virtualQuantity != other.virtualQuantity)
         return false;
      return true;
   }

   public Builder toBuilder() {
      return Builder.fromResourceAllocation(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", type=" + type + ", name=" + name + ", description=" + description + ", allocationUnits="
            + allocationUnits + ", virtualQuantity=" + virtualQuantity + ", connection=" + connection + ", caption="
            + caption + ", hostResource=" + hostResource + "]";
   }

}