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
package org.jclouds.ec2.options;

import java.util.Set;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the DescribeSnapshots operation. <h2>
 * Usage</h2> The recommended way to instantiate a DescribeSnapshotsOptions object is to statically
 * import DescribeSnapshotsOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * Set<Snapshot> snapshots = connection.getElasticBlockStoreServices().describeSnapshots(restorableBy("123125").snapshotIds(1000, 1004));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-DescribeSnapshots.html"
 *      />
 */
public class DescribeSnapshotsOptions extends BaseEC2RequestOptions {

   /**
    * Account ID of a user that can create volumes from the snapshot.
    * 
    */
   public DescribeSnapshotsOptions restorableBy(String... accountIds) {
      indexFormValuesWithPrefix("RestorableBy", accountIds);
      return this;
   }

   public String getRestorableBy() {
      return getFirstFormOrNull("RestorableBy");
   }

   /**
    * The ID of the Amazon EBS snapshot.
    */
   public DescribeSnapshotsOptions snapshotIds(String... snapshotIds) {
      indexFormValuesWithPrefix("SnapshotId", snapshotIds);
      return this;
   }

   public Set<String> getSnapshotIds() {
      return getFormValuesWithKeysPrefixedBy("SnapshotId.");
   }

   /**
    * Returns snapshots owned by the specified owner. Multiple owners can be specified.
    * <p/>
    * Valid Values: self | amazon | AWS Account ID
    */
   public DescribeSnapshotsOptions ownedBy(String... owners) {
      indexFormValuesWithPrefix("Owner", owners);
      return this;
   }

   public Set<String> getOwners() {
      return getFormValuesWithKeysPrefixedBy("Owner.");
   }

   public static class Builder {

      /**
       * @see DescribeSnapshotsOptions#restorableBy(String[] )
       */
      public static DescribeSnapshotsOptions restorableBy(String... accountIds) {
         DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
         return options.restorableBy(accountIds);
      }

      /**
       * @see DescribeSnapshotsOptions#snapshotIds(String[] )
       */
      public static DescribeSnapshotsOptions snapshotIds(String... snapshotIds) {
         DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
         return options.snapshotIds(snapshotIds);
      }

      /**
       * @see DescribeSnapshotsOptions#ownedBy(String[] )
       */
      public static DescribeSnapshotsOptions ownedBy(String... owners) {
         DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
         return options.ownedBy(owners);
      }

   }
}
