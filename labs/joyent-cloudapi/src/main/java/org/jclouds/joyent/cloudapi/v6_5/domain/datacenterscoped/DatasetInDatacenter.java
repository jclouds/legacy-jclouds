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
package org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;

/**
 * @author Adrian Cole
 */
public class DatasetInDatacenter extends DatacenterAndId {
   protected final Dataset dataset;

   public DatasetInDatacenter(Dataset dataset, String datacenterId) {
      super(datacenterId, checkNotNull(dataset, "dataset").getId());
      this.dataset = dataset;
   }

   public Dataset get() {
      return dataset;
   }

   // superclass hashCode/equals are good enough, and help us use DatacenterAndId and DatasetInDatacenter
   // interchangeably as Map keys

   @Override
   public String toString() {
      return "[dataset=" + dataset + ", datacenterId=" + datacenterId + "]";
   }

}
