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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A placement group is a logical grouping of instances.
 * 
 * <p/>
 * You first create a cluster placement group, then launch multiple cluster compute instances into
 * the group. Currently cluster compute instances are available only in the US-East (Northern
 * Virginia) Region. You must give each placement group a name that is unique within your account.
 * For more information about cluster placement groups, see Cluster Compute Instance Concepts.
 * <p/>
 * Note
 * <p/>
 * You can't merge cluster placement groups. Instead you must terminate the instances in one of the
 * groups, and then relaunch the instances into the other group.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribePlacementGroups.html"
 *      />
 * @author Adrian Cole
 */
public class PlacementGroup implements Comparable<PlacementGroup> {
   public static enum State {
      PENDING, AVAILABLE, DELETING, DELETED, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static State fromValue(String state) {
         try {
            return valueOf(checkNotNull(state, "state").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final String region;
   private final String name;
   private final String strategy;
   private final State state;

   public PlacementGroup(String region, String name, String strategy, State state) {
      this.region = checkNotNull(region, "region");
      this.name = checkNotNull(name, "name");
      this.strategy = checkNotNull(strategy, "strategy");
      this.state = checkNotNull(state, "state");
   }

   @Override
   public int compareTo(PlacementGroup o) {
      return name.compareTo(o.name);
   }

   /**
    * @return placement groups are in a region, however the namescope is global.
    */
   public String getRegion() {
      return region;
   }

   /**
    * @return Name of the placement group.
    */
   public String getName() {
      return name;
   }

   /**
    * @return The placement strategy.
    */
   public String getStrategy() {
      return strategy;
   }

   /**
    * @return Status of the placement group.
    */
   public State getState() {
      return state;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((strategy == null) ? 0 : strategy.hashCode());
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
      PlacementGroup other = (PlacementGroup) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (strategy == null) {
         if (other.strategy != null)
            return false;
      } else if (!strategy.equals(other.strategy))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", region=" + region + ", state=" + state + ", strategy=" + strategy + "]";
   }

}
