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
package org.jclouds.rimuhosting.miro.binder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import org.jclouds.rimuhosting.miro.data.NewServerData;
import org.jclouds.rimuhosting.miro.domain.MetaData;

/**
 * @author Ivan Meredith
 */
public class CreateServerOptions extends RimuHostingJsonBinder {
   @Inject
   public CreateServerOptions(Json jsonBinder) {
      super(jsonBinder);
   }
   
   private String password;
   private List<MetaData> metaData = Lists.newArrayList();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String name = checkNotNull(postParams.get("name")).toString();
      String imageId = checkNotNull(postParams.get("imageId")).toString();
      String planId = checkNotNull(postParams.get("planId")).toString();
      // There will be cases when the password is null.
      String password = this.password;
      NewServerData newServerData = new NewServerData(new CreateOptions(name, password, imageId), planId);
      newServerData.setMetaData(metaData);
      return bindToRequest(request, newServerData);
   }

   public CreateServerOptions withPassword(String password) {
      this.password = password;
      return this;
   }

   public CreateServerOptions withMetaData(List<MetaData> metaData) {
      this.metaData = metaData;
      return this;
   }
}
