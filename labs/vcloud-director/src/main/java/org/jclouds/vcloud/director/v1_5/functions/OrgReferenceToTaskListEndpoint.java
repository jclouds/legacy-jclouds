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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;
import org.jclouds.vcloud.director.v1_5.features.OrgClient;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * @author grkvlt@apache.org
 */
@Singleton
public class OrgReferenceToTaskListEndpoint implements Function<Object, URI> {
   private final OrgClient client;

   @Inject
   public OrgReferenceToTaskListEndpoint(OrgClient client) {
      this.client = client;
   }

   @Override
   public URI apply(Object input) {
      Preconditions.checkNotNull(input);
      Preconditions.checkArgument(input instanceof URISupplier);
      URISupplier reference = (URISupplier) input;
      Org org = client.getOrg(reference);
      for (Link link : org.getLinks()) {
         if (link.getType().equals(VCloudDirectorMediaType.TASKS_LIST)) {
            return link.getHref();
         }
      }
      throw new RuntimeException(String.format("Could not find a link of type %s", VCloudDirectorMediaType.TASKS_LIST));
	};
}