/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.strategy.cloud;

import java.util.List;

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.strategy.ListRootEntities;
import org.jclouds.abiquo.strategy.cloud.internal.ListVirtualDatacentersImpl;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * List virtual datacenters.
 * 
 * @author Ignasi Barrera
 */
@ImplementedBy(ListVirtualDatacentersImpl.class)
public interface ListVirtualDatacenters extends ListRootEntities<VirtualDatacenter> {
   Iterable<VirtualDatacenter> execute(VirtualDatacenterOptions virtualDatacenterOptions);

   Iterable<VirtualDatacenter> execute(List<Integer> virtualDatacenterIds);

   Iterable<VirtualDatacenter> execute(Predicate<VirtualDatacenter> selector,
         VirtualDatacenterOptions virtualDatacenterOptions);

}
