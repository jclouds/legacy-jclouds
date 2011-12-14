package org.jclouds.glesys.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_status" />
 */
public class Template {


   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private int minDiskSize;
      private int minMemSize;
      private String os;
      private String platform;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder minDiskSize(int minDiskSize) {
         this.minDiskSize = minDiskSize;
         return this;
      }

      public Builder minMemSize(int minMemSize) {
         this.minMemSize = minMemSize;
         return this;
      }

      public Builder os(String os) {
         this.os = os;
         return this;
      }

      public Builder platform(String platform) {
         this.platform = platform;
         return this;
      }
      
      public Template build() {
         return new Template(name, minDiskSize, minMemSize, os, platform);
      }
      
      public Builder fromTemplate(Template in) {
         return name(in.getName()).minDiskSize(in.getMinDiskSize()).minMemSize(in.getMinMemSize()).os(in.getOs()).platform(in.getPlatform());
      }

   }

   private final String name;
   @SerializedName("min_disk_size")
   private final int minDiskSize;
   @SerializedName("min_mem_size")
   private final int minMemSize;
   private final String os;
   private final String platform;

   public Template(String name, int minDiskSize, int minMemSize, String os, String platform) {
      this.name = name;
      this.minDiskSize = minDiskSize;
      this.minMemSize = minMemSize;
      this.os = os;
      this.platform = platform;
   }

   public String getName() {
      return name;
   }

   public int getMinDiskSize() {
      return minDiskSize;
   }

   public int getMinMemSize() {
      return minMemSize;
   }

   public String getOs() {
      return os;
   }

   public String getPlatform() {
      return platform;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Template) {
         final Template other = (Template) object;
         return Objects.equal(name, other.name)
               && Objects.equal(minDiskSize, other.minDiskSize)
               && Objects.equal(minMemSize, other.minMemSize)
               && Objects.equal(os, other.os)
               && Objects.equal(platform, other.platform);
      } else {
         return false;
      }
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(name, minDiskSize, minMemSize, os, platform);
   }
   
   @Override
   public String toString() {
      return String.format("[name=%s, min_disk_size=%d, min_mem_size=%d, os=%s, platform=%s]",
            name, minDiskSize, minMemSize, os, platform);
   }
}
