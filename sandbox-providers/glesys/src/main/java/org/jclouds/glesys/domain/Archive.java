package org.jclouds.glesys.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Information about an archive
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#archive_list" />
 */
public class Archive implements Comparable<Archive> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String username;
      protected String totalSize;
      protected String freeSize;
      protected boolean locked;

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Builder totalSize(String totalSize) {
         this.totalSize = totalSize;
         return this;
      }

      public Builder freeSize(String freeSize) {
         this.freeSize = freeSize;
         return this;
      }

      public Builder locked(boolean locked) {
         this.locked = locked;
         return this;
      }

      public Archive build() {
         return new Archive(username, totalSize, freeSize, locked);
      }

      public Builder fromArchive(Archive in) {
         return username(in.getUsername()).totalSize(in.getTotalSize()).freeSize(in.getFreeSize()).locked(in.isLocked());
      }
   }

   private final String username;
   @SerializedName("size_total")
   private final String totalSize;
   @SerializedName("size_free")
   private final String freeSize;
   private final boolean locked;

   public String getUsername() {
      return username;
   }

   public String getTotalSize() {
      return totalSize;
   }

   public String getFreeSize() {
      return freeSize;
   }

   public boolean isLocked() {
      return locked;
   }

   public Archive(String username, String totalSize, String freeSize, boolean locked) {
      this.username = username;
      this.totalSize = totalSize;
      this.freeSize = freeSize;
      this.locked = locked;
   }


   @Override
   public int hashCode() {
      return Objects.hashCode(username);
   }

   @Override
   public int compareTo(Archive other) {
      return username.compareTo(other.getUsername());
   }
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      return obj instanceof Archive
            && Objects.equal(username, ((Archive) obj).username);
   }

   @Override
   public String toString() {
      return String.format("[username=%s, totalSize=%s, freeSize=%s, locked=%b]", username, totalSize, freeSize, locked);
   }

}
