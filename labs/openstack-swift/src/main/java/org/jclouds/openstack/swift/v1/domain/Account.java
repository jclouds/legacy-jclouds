package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-account-metadata.html">api doc</a>
 */
public class Account {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromAccountMetadata(this);
   }

   public static class Builder {
      protected int containerCount;
      protected long bytesUsed;

      /**
       * @see Account#getContainerCount()
       */
      public Builder containerCount(int containerCount) {
         this.containerCount = containerCount;
         return this;
      }

      /**
       * @see Account#getBytesUsed()
       */
      public Builder bytesUsed(long bytesUsed) {
         this.bytesUsed = bytesUsed;
         return this;
      }

      public Account build() {
         return new Account(containerCount, bytesUsed);
      }

      public Builder fromAccountMetadata(Account from) {
         return containerCount(from.getContainerCount()).bytesUsed(from.getBytesUsed());
      }
   }
  
   protected int containerCount;
   protected long bytesUsed;

   @ConstructorProperties({"containerCount", "bytesUsed"})
   protected Account(int containerCount, long bytesUsed) {
      this.containerCount = containerCount;
      this.bytesUsed = bytesUsed;
   }

   /**
    * 
    * @return the number of containers in OpenStack Object Storage for the account
    */
   public int getContainerCount() {
      return containerCount;
   }

   /**
    * @return the total bytes stored in OpenStack Object Storage for the account
    */
   public long getBytesUsed() {
      return bytesUsed;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Account) {
         final Account other = Account.class.cast(object);
         return equal(getContainerCount(), other.getContainerCount()) && equal(getBytesUsed(), other.getBytesUsed());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getContainerCount(), getBytesUsed());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("containerCount", getContainerCount()).add("bytesUsed", getBytesUsed());
   }
}
