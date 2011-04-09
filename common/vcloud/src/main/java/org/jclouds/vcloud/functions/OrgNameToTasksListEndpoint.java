/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.endpoints.TasksList;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameToTasksListEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, ? extends Org>> orgMap;
   private final URI defaultUri;

   @Inject
   public OrgNameToTasksListEndpoint(Supplier<Map<String, ? extends Org>> orgMap, @TasksList URI defaultUri) {
      this.orgMap = orgMap;
      this.defaultUri = defaultUri;
   }

   public URI apply(Object from) {
      Object org = from;
      if (org == null)
         return defaultUri;
      try {
         return checkNotNull(orgMap.get().get(org)).getTasksList().getHref();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + " not found in " + orgMap.get());
      }
   }

}