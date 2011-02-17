/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.util.Preconditions2;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Container for node filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.compute.ComputeService}.
 * 
 * @author Oleksiy Yarmula
 */
public class NodePredicates {

   private static class ParentLocationId implements Predicate<ComputeMetadata> {
      private final String id;

      private ParentLocationId(String id) {
         this.id = id;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((id == null) ? 0 : id.hashCode());
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
         ParentLocationId other = (ParentLocationId) obj;
         if (id == null) {
            if (other.id != null)
               return false;
         } else if (!id.equals(other.id))
            return false;
         return true;
      }

      @Override
      public boolean apply(ComputeMetadata nodeMetadata) {
         if (nodeMetadata.getLocation().getParent() == null)
            return false;
         return id.equals(nodeMetadata.getLocation().getParent().getId());
      }

      @Override
      public String toString() {
         return "ParentLocationId [id=" + id + "]";
      }
   }

   private static class LocationId implements Predicate<ComputeMetadata> {
      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((id == null) ? 0 : id.hashCode());
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
         LocationId other = (LocationId) obj;
         if (id == null) {
            if (other.id != null)
               return false;
         } else if (!id.equals(other.id))
            return false;
         return true;
      }

      private final String id;

      private LocationId(String id) {
         this.id = id;
      }

      @Override
      public boolean apply(ComputeMetadata nodeMetadata) {
         return id.equals(nodeMetadata.getLocation().getId());
      }

      @Override
      public String toString() {
         return "locationId(" + id + ")";
      }
   }

   /**
    * Return nodes in the specified location.
    * 
    * @param id
    *           id of the location
    * @return predicate
    */
   public static Predicate<ComputeMetadata> locationId(final String id) {
      checkNotNull(id, "id must be defined");
      return new LocationId(id);
   }

   /**
    * Return nodes in the specified parent location.
    * 
    * @param id
    *           id of the location
    * @return predicate
    */
   public static Predicate<ComputeMetadata> parentLocationId(final String id) {
      checkNotNull(id, "id must be defined");
      return new ParentLocationId(id);
   }

   /**
    * Return nodes with the specific ids Note: returns all nodes, regardless of the state.
    * 
    * @param ids
    *           ids of the resources
    * @return predicate
    */
   public static Predicate<ComputeMetadata> withIds(String... ids) {
      checkNotNull(ids, "ids must be defined");
      final Set<String> search = Sets.newHashSet(ids);
      return new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata nodeMetadata) {
            return search.contains(nodeMetadata.getProviderId());
         }

         @Override
         public String toString() {
            return "withIds(" + search + ")";
         }
      };
   }

   /**
    * return everything.
    */
   public static Predicate<ComputeMetadata> all() {
      return Predicates.<ComputeMetadata> alwaysTrue();
   }

   /**
    * Return nodes in the specified group. Note: returns all nodes, regardless of the state.
    * 
    * @param group
    *           group to match the items
    * @return predicate
    */
   public static Predicate<NodeMetadata> inGroup(final String group) {
      Preconditions2.checkNotEmpty(group, "group must be defined");
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata nodeMetadata) {
            return group.equals(nodeMetadata.getGroup());
         }

         @Override
         public String toString() {
            return "inGroup(" + group + ")";
         }
      };
   }

   /**
    * 
    * @see #inGroup(String)
    */
   @Deprecated
   public static Predicate<NodeMetadata> withTag(final String tag) {
      return inGroup(tag);
   }

   /**
    * Return nodes with specified group that are in the NODE_RUNNING state.
    * 
    * @param group
    *           group to match the items
    * @return predicate
    */
   public static Predicate<NodeMetadata> runningInGroup(final String group) {
      Preconditions2.checkNotEmpty(group, "Tag must be defined");
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata nodeMetadata) {
            return group.equals(nodeMetadata.getGroup()) && nodeMetadata.getState() == NodeState.RUNNING;
         }

         @Override
         public String toString() {
            return "runningInGroup(" + group + ")";
         }
      };
   }

   /**
    * 
    * @see #inGroup(String)
    */
   @Deprecated
   public static Predicate<NodeMetadata> runningWithTag(final String tag) {
      return runningInGroup(tag);
   }

   /**
    * Match nodes with State == RUNNING
    */
   public static final Predicate<NodeMetadata> RUNNING = new Predicate<NodeMetadata>() {
      @Override
      public boolean apply(NodeMetadata nodeMetadata) {
         return nodeMetadata.getState() == NodeState.RUNNING;
      }

      @Override
      public String toString() {
         return "RUNNING";
      }
   };

   /**
    * Match nodes with State == NODE_TERMINATED
    */
   public static final Predicate<NodeMetadata> TERMINATED = new Predicate<NodeMetadata>() {
      @Override
      public boolean apply(NodeMetadata nodeMetadata) {
         return nodeMetadata.getState() == NodeState.TERMINATED;
      }

      @Override
      public String toString() {
         return "TERMINATED";
      }
   };

}
