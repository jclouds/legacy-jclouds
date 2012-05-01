package org.jclouds.jenkins.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 * @see <a
 *      href="http://ci.jruby.org/computer/api/">api
 *      doc</a>
 */
public class ComputerView implements Comparable<ComputerView> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromComputerMetadata(this);
   }

   public static class Builder {
      protected String displayName;
      protected int busyExecutors;
      protected int totalExecutors;
      protected Set<Computer> computers = ImmutableSet.of();

      /**
       * @see ComputerView#getDisplayName()
       */
      public Builder displayName(String displayName) {
         this.displayName = checkNotNull(displayName, "displayName");
         return this;
      }

      /**
       * @see ComputerView#getBusyExecutors()
       */
      public Builder busyExecutors(int busyExecutors) {
         this.busyExecutors = busyExecutors;
         return this;
      }

      /**
       * @see ComputerView#getTotalExecutors()
       */
      public Builder totalExecutors(int totalExecutors) {
         this.totalExecutors = totalExecutors;
         return this;
      }
      
      /**
       * @see ComputerView#getLinks()
       */
      public Builder computers(Computer... computers) {
         return computers(ImmutableSet.copyOf(checkNotNull(computers, "computers")));
      }

      /**
       * @see ComputerView#getLinks()
       */
      public Builder computers(Set<Computer> computers) {
         this.computers = ImmutableSet.copyOf(checkNotNull(computers, "computers"));
         return this;
      }
      
      public ComputerView build() {
         return new ComputerView(displayName, busyExecutors, totalExecutors, computers);
      }

      public Builder fromComputerMetadata(ComputerView from) {
         return displayName(from.getDisplayName()).busyExecutors(from.getBusyExecutors()).totalExecutors(from.getTotalExecutors()).computers(from.getComputers());
      }
   }

   protected final String displayName;
   protected final int busyExecutors;
   protected final int totalExecutors;
   @SerializedName("computer")
   protected final Set<Computer> computers;

   public ComputerView(String displayName, int busyExecutors, int totalExecutors, Set<Computer> computers) {
      this.displayName = checkNotNull(displayName, "displayName");
      this.busyExecutors = busyExecutors;
      this.totalExecutors = totalExecutors;
      this.computers = ImmutableSet.copyOf(checkNotNull(computers, "computers"));
   }

   /**
    * 
    * @return the displayName of the computer
    */
   public String getDisplayName() {
      return displayName;
   }

   /**
    * 
    * @return the number of objects in the computer
    */
   public int getBusyExecutors() {
      return busyExecutors;
   }

   /**
    * @return the total totalExecutors stored in this computer
    */
   public int getTotalExecutors() {
      return totalExecutors;
   }
   
   /**
    * @return the computers in this set
    */
   //TODO: create type adapter for gson that understands ForwardingSet so that we can implement the Set interface
   public Set<Computer> getComputers() {
      return computers;
   }
   
   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ComputerView) {
         final ComputerView other = ComputerView.class.cast(object);
         return equal(getDisplayName(), other.getDisplayName()) && equal(getBusyExecutors(), other.getBusyExecutors())
                  && equal(getTotalExecutors(), other.getTotalExecutors()) && equal(getComputers(), other.getComputers());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getDisplayName(), getBusyExecutors(), getTotalExecutors(), getComputers());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("displayName", getDisplayName()).add("busyExecutors", getBusyExecutors()).add(
               "totalExecutors", getTotalExecutors()).add("computers", getComputers());
   }

   @Override
   public int compareTo(ComputerView that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getDisplayName().compareTo(that.getDisplayName());
   }



}
