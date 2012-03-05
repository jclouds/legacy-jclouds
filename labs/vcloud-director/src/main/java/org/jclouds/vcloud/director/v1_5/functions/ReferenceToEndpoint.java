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
package org.jclouds.vcloud.director.v1_5.functions;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;

/**
 * @author grkvlt@apache.org
 */
@Deprecated
public class ReferenceToEndpoint implements Function<Object, URI> {

   @Override
   public URI apply(Object input) {
      Preconditions.checkNotNull(input);
      Preconditions.checkArgument(input instanceof URISupplier);
      URISupplier reference = (URISupplier) input;
      return reference.getURI();
	};
}