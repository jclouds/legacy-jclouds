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

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author Andrea Turli
 */
public enum MacAddressToBSD implements Function<String, String> {

   INSTANCE;

   @Override
   public String apply(String macAddress) {
      checkArgument(macAddress.length() == 17);
      return Joiner.on(":").join(
              Iterables.transform(Splitter.on(":").split(macAddress),
                      new Function<String, String>() {
                         @Override
                         public String apply(String addressPart) {
                            if (addressPart.equals("00"))
                               return "0";
                            if (addressPart.startsWith("0"))
                               return addressPart.substring(1);

                            return addressPart;
                         }
                      }));
   }
}
