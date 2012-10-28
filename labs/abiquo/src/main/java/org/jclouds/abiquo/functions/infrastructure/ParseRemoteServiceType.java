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

package org.jclouds.abiquo.functions.infrastructure;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.google.common.base.Function;

/**
 * Parses a {@link ParseRemoteServiceType} object to extract its type in the
 * format that the API expects it.
 * 
 * @author Francesc Montserrat
 */
@Singleton
public class ParseRemoteServiceType implements Function<Object, String> {
   @Override
   public String apply(final Object input) {
      checkArgument(checkNotNull(input, "input") instanceof RemoteServiceType,
            "This parser is only valid for RemoteServiceType objects");

      return ((RemoteServiceType) input).name().replaceAll("_", "").toLowerCase();
   }

}
