/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.util.Preconditions2;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Container for {@link ComputeMetadata node} filtering {@link Predicate predicates}.
 *
 * This class has static methods that create customized predicates to use with
 * {@link ComputeMetadata} and {@link NodeMetadata} objects.
 *
 * @author Oleksiy Yarmula
 * @author Andrew Kennedy
 */
public class NodePredicates {

   private static class ParentLocationId implements Predicate<ComputeMetadata> {

      private final String id;

      private ParentLocationId(String id) {
         this.id = id;
      }

      @Override
      public boolean apply(ComputeMetadata nodeMetadata) {
         if (nodeMetadata.getLocation().getParent() == null)
            return false;
         return id.equals(nodeMetadata.getLocation().getParent().getId());
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.id);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         ParentLocationId that = (ParentLocationId) o;
         return Objects.equal(this.id, that.id);
      }

      @Override
      public String toString() {
         return "ParentLocationId [id=" + id + "]";
      }
   }

   private static class LocationId implements Predicate<ComputeMetadata> {

      private final String id;

      private LocationId(String id) {
         this.id = id;
      }

      @Override
      public boolean apply(ComputeMetadata nodeMetadata) {
         return id.equals(nodeMetadata.getLocation().getId());
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(id);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         LocationId that = LocationId.class.cast(o);
         return Objects.equal(this.id, that.id);
      }

      @Override
      public String toString() {
         return "LocationId [id=" + id + "]";
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
    * Return nodes with the specific ids.
    *
    * NOTE Returns all nodes, regardless of the state.
    *
    * @param ids
    *           ids of the resources
    * @return predicate
    */
   public static <T extends ComputeMetadata> Predicate<T> withIds(final String... ids) {
      checkNotNull(ids, "ids must be defined");
      final Set<String> search = ImmutableSet.copyOf(ids);
      return new Predicate<T>() {
         @Override
         public boolean apply(T nodeMetadata) {
            return search.contains(nodeMetadata.getId());
         }

         @Override
         public String toString() {
            return "withIds(" + search + ")";
         }
      };
   }

   /**
    * Return everything.
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
    * Return nodes who have a value for {@link NodeMetadata#getGroup}
    */
   public static Predicate<NodeMetadata> hasGroup() {
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata nodeMetadata) {
            return nodeMetadata != null && nodeMetadata.getGroup() != null;
         }

         @Override
         public String toString() {
            return "hasGroup()";
         }
      };
   }

   /**
    * Return nodes with specified group that are in the NODE_RUNNING state.
    *
    * @param group
    *           group to match the items
    * @return predicate
    */
   public static Predicate<NodeMetadata> runningInGroup(final String group) {
      Preconditions2.checkNotEmpty(group, "group must be defined");
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

   /**
    * Return nodes with the specified tags in their metadata.
    *
    * Searches for matching values in the list of {@link ComputeMetadata#getTags() tags} and
    * any node {@link ComputeMetadata#getUserMetadata() metadata} regardless of value.
    *
    * @param tags
    *           the list of tags that must be present
    * @return predicate
    * @see #hasMetadata(Map)
    * @see #hasMetadataEntry(String, String)
    */
   public static Predicate<ComputeMetadata> hasTags(final String...tags) {
      checkNotNull(tags, "tags must be defined");
      final Set<String> search = ImmutableSet.copyOf(tags);
      return new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata node) {
            final Iterable<String> nodeTags = Iterables.concat(node.getTags(), node.getUserMetadata().keySet());
            return Iterables.all(search, new Predicate<String>() {
               @Override
               public boolean apply(String input) {
                  return Iterables.contains(nodeTags, input);
               }
            });
         }

         @Override
         public String toString() {
            return "hasTags(" + Iterables.toString(search) + ")";
         }
      };
   }

   /**
    * Return nodes that contain the specified data in their metadata.
    *
    * Searches for matching key-value pairs in the node {@link ComputeMetadata#getUserMetadata() metadata}
    * and any matching {@link ComputeMetadata#getTags() tags} with empty values.
    *
    * @param metadata
    *           the map of metadata that must be present
    * @return predicate
    * @see #hasTags(String...)
    * @see #hasMetadataEntry(String, String)
    */
   public static Predicate<ComputeMetadata> hasMetadata(final Map<String, String> metadata) {
      checkNotNull(metadata, "metadata must be defined");
      final Map<String, String> search = ImmutableMap.copyOf(metadata);
      return new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata node) {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder().putAll(node.getUserMetadata());
            for (String key : node.getTags()) {
               builder.put(key, "");
            }
            Map<String, String> nodeMetadata = builder.build();
            return Maps.difference(search, nodeMetadata).entriesOnlyOnLeft().isEmpty();
         }

         @Override
         public String toString() {
            return "hasMetadata(" + Joiner.on(",").withKeyValueSeparator("=").join(search) + ")";
         }
      };
   }

   /**
    * Return nodes that contain the specified entry in their metadata.
    *
    * Searches for a matching key-value pair in the node {@link ComputeMetadata#getUserMetadata() metadata}
    * and {@link ComputeMetadata#getTags() tags}. Null {@literal value} parameters will be treated the
    * same as an empty string, althougth the {@link #hasTags(String...) hasTags} predicate may be preferable.
    *
    * @param key
    *           the key that must be present in the metadata
    * @param value
    *           the metadata value for the key, which may be null or empty
    * @return predicate
    * @see #hasTags(String...)
    * @see #hasMetadata(Map)
    */
   public static Predicate<ComputeMetadata> hasMetadataEntry(final String key, @Nullable final String value) {
      checkNotNull(key, "key must be defined");
      return new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata node) {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder().putAll(node.getUserMetadata());
            for (String key : node.getTags()) {
                builder.put(key, "");
            }
            Map<String, String> nodeMetadata = builder.build();
            if (nodeMetadata.containsKey(key)) {
                String found = nodeMetadata.get(key);
                return Strings.nullToEmpty(value).equals(found);
            } else return false;
         }

         @Override
         public String toString() {
            return "hasMetadataEntry(" + key + "," + (Strings.isNullOrEmpty(value) ? "<empty>" : value) + ")";
         }
      };
   }
}
