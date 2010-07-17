/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudfiles.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.domain.internal.ObjectInfoImpl;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.TypeLiteral;

/**
 * This parses {@link ObjectInfo} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseObjectInfoListFromJsonResponse extends
      ParseJson<PageSet<ObjectInfo>> implements InvocationContext {

   private GeneratedHttpRequest<?> request;

   @Inject
   public ParseObjectInfoListFromJsonResponse(Gson gson) {
      super(gson, new TypeLiteral<PageSet<ObjectInfo>>() {
      });
   }

   public PageSet<ObjectInfo> apply(InputStream stream) {
      checkState(request != null, "request should be initialized at this point");
      checkState(request.getArgs() != null,
            "request.getArgs() should be initialized at this point");
      checkArgument(request.getArgs()[0] instanceof String,
            "arg[0] must be a container name");
      checkArgument(request.getArgs()[1] instanceof ListContainerOptions[],
            "arg[1] must be an array of ListContainerOptions");
      ListContainerOptions[] optionsList = (ListContainerOptions[]) request
            .getArgs()[1];
      ListContainerOptions options = optionsList.length > 0 ? optionsList[0]
            : ListContainerOptions.NONE;
      Type listType = new TypeToken<SortedSet<ObjectInfoImpl>>() {
      }.getType();

      try {
         SortedSet<ObjectInfoImpl> list = gson.fromJson(new InputStreamReader(
               stream, "UTF-8"), listType);
         SortedSet<ObjectInfo> returnVal = Sets.newTreeSet(Iterables.transform(
               list, new Function<ObjectInfoImpl, ObjectInfo>() {
                  public ObjectInfo apply(ObjectInfoImpl from) {
                     return from;
                  }
               }));
         boolean truncated = options.getMaxResults() == returnVal.size();
         String marker = truncated ? returnVal.last().getName() : null;
         return new PageSetImpl<ObjectInfo>(returnVal, marker);

      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }

   @Override
   public ParseObjectInfoListFromJsonResponse setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest<?>,
            "note this handler requires a GeneratedHttpRequest");
      this.request = (GeneratedHttpRequest<?>) request;
      return this;
   }
}