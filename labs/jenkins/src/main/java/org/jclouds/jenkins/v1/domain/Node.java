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
 *      href="http://ci.jruby.org/api/">api
 *      doc</a>
 */
public class Node implements Comparable<Node> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromNodeMetadata(this);
   }

   public static class Builder {
      protected String name;
      protected String description;
      protected Set<Job> jobs = ImmutableSet.of();

      /**
       * @see Node#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Node#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see Node#getJobs()
       */
      public Builder jobs(Job... jobs) {
         return jobs(ImmutableSet.copyOf(checkNotNull(jobs, "jobs")));
      }

      /**
       * @see Node#getJobs()
       */
      public Builder jobs(Set<Job> jobs) {
         this.jobs = ImmutableSet.copyOf(checkNotNull(jobs, "jobs"));
         return this;
      }

      public Node build() {
         return new Node(name, description, jobs);
      }

      public Builder fromNodeMetadata(Node from) {
         return name(from.getName()).description(from.getDescription()).jobs(from.getJobs());
      }
   }

   @SerializedName("nodeName")
   protected final String name;
   @SerializedName("nodeDescription")
   protected final String description;
   protected final Set<Job> jobs;

   public Node(String name, String description, Set<Job> jobs) {
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.jobs = ImmutableSet.copyOf(checkNotNull(jobs, "jobs"));
   }

   /**
    * 
    * @return the name of the node
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return the description of the node
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return the jobs on this node
    */
   public Set<Job> getJobs() {
      return jobs;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Node) {
         final Node other = Node.class.cast(object);
         return equal(getName(), other.getName()) && equal(getDescription(), other.getDescription())
                  && equal(getJobs(), other.getJobs());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getName(), getDescription(), getJobs());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("name", getName()).add("description", getDescription()).add("jobs", getJobs());
   }

   @Override
   public int compareTo(Node that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getName().compareTo(that.getName());
   }

}
