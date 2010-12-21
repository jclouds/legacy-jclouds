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

package org.jclouds.cloudsigma.functions;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.Model;
import org.jclouds.cloudsigma.domain.NIC;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapToNICs implements Function<Map<String, String>, List<NIC>> {

   @Override
   public List<NIC> apply(Map<String, String> from) {
      ImmutableList.Builder<NIC> nics = ImmutableList.builder();
      NIC: for (int id : new int[] { 0, 1 }) {
         String key = String.format("nic:%d", id);
         if (!from.containsKey(key + ":model"))
            break NIC;
         NIC.Builder nicBuilder = new NIC.Builder();
         nicBuilder.dhcp(from.get(key + ":dhcp"));
         nicBuilder.model(Model.fromValue(from.get(key + ":model")));
         nicBuilder.vlan(from.get(key + ":vlan"));
         nicBuilder.mac(from.get(key + ":mac"));
         if (from.containsKey(key + ":block"))
            nicBuilder.block(Splitter.on(' ').split(from.get(key + ":block")));
         nics.add(nicBuilder.build());
      }
      return nics.build();
   }
}