/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.http.Uris.uriBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.internal.ObjectInfoImpl;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import com.google.inject.TypeLiteral;

/**
 * This parses {@link ObjectInfo} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseObjectInfoListFromJsonResponse extends ParseJson<PageSet<ObjectInfo>> implements
      InvocationContext<ParseObjectInfoListFromJsonResponse> {

   private List<Object> args;
   private String container;
   private GeneratedHttpRequest request;
   private ListContainerOptions options;

   @Inject
   public ParseObjectInfoListFromJsonResponse(Json json) {
      super(json, new TypeLiteral<PageSet<ObjectInfo>>() {
      });
   }

   public PageSet<ObjectInfo> apply(InputStream stream) {
      checkState(args != null, "request should be initialized at this point");
      Type listType = new TypeToken<SortedSet<ObjectInfoImpl>>() {
      }.getType();

      try {
         SortedSet<ObjectInfoImpl> list = apply(stream, listType);
         SortedSet<ObjectInfo> returnVal = Sets.newTreeSet(Iterables.transform(list,
               new Function<ObjectInfoImpl, ObjectInfo>() {
                  public ObjectInfo apply(ObjectInfoImpl from) {
                     return from.toBuilder().container(container)
                           .uri(uriBuilder(request.getEndpoint()).clearQuery().appendPath(from.getName()).build())
                           .build();
                  }
               }));
         boolean truncated = options.getMaxResults() == returnVal.size();
         String marker = truncated ? returnVal.last().getName() : null;
         return new PageSetImpl<ObjectInfo>(returnVal, marker);
      } catch (IOException e) {
         throw new RuntimeException("problem reading response from request: " + request, e);
      }
   }

   @Override
   public ParseObjectInfoListFromJsonResponse setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      this.request = GeneratedHttpRequest.class.cast(request);
      this.args = this.request.getInvocation().getArgs();
      checkArgument(args.get(0) instanceof String, "arg[0] must be a container name");
      this.container = args.get(0).toString();
      checkArgument(args.get(1) instanceof ListContainerOptions[], "arg[1] must be an array of ListContainerOptions");
      ListContainerOptions[] optionsList = (ListContainerOptions[]) args.get(1);
      this.options = optionsList.length > 0 ? optionsList[0] : ListContainerOptions.NONE;
      return this;
   }
}
