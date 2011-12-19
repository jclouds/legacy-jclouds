package org.jclouds.glesys.domain;

/**
 * Detailed information about an archive volume.
 * 
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#archive_details" />
 */
public class ArchiveDetails extends Archive {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends Archive.Builder {
      public ArchiveDetails build() {
         return new ArchiveDetails(username, totalSize, freeSize, locked);
      }

      public Builder fromArchiveDetails(ArchiveDetails in) {
         return username(in.getUsername()).totalSize(in.getTotalSize()).freeSize(in.getFreeSize()).locked(in.isLocked());
      }

      @Override
      public Builder username(String username) {
         return Builder.class.cast(super.username(username));
      }

      @Override
      public Builder totalSize(String size) {
         return Builder.class.cast(super.totalSize(size));
      }

      @Override
      public Builder freeSize(String size) {
         return Builder.class.cast(super.freeSize(size));
      }
      
      @Override
      public Builder locked(boolean locked) {
         return Builder.class.cast(super.locked(locked));
      }
   }
   
   public ArchiveDetails(String username, String totalSize, String freeSize, boolean locked) {
      super(username, totalSize, freeSize, locked);
   }
}
