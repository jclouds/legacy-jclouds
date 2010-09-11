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

package org.jclouds.slicehost.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   private final SlicehostClient client;

   @Inject
   protected SlicehostAddNodeWithTagStrategy(SlicehostClient client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public NodeMetadata execute(String tag, String name, Template template) {
      Slice slice = client.createSlice(name, Integer.parseInt(template.getImage().getProviderId()), Integer
               .parseInt(template.getHardware().getProviderId()));
      return new NodeMetadataImpl(slice.getId() + "", name, slice.getId() + "", template.getLocation(), null,
               ImmutableMap.<String, String> of(), tag, template.getImage().getId(), template.getImage()
                        .getOperatingSystem(), NodeState.PENDING, Iterables.filter(slice.getAddresses(),
                        new Predicate<String>() {

                           @Override
                           public boolean apply(String input) {
                              return !input.startsWith("10.");
                           }

                        }), Iterables.filter(slice.getAddresses(), new Predicate<String>() {

                  @Override
                  public boolean apply(String input) {
                     return input.startsWith("10.");
                  }

               }), ImmutableMap.<String, String> of(), new Credentials("root", slice.getRootPassword()));
   }

}