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
package org.jclouds.atmos.functions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import org.jclouds.atmos.domain.AtmosError;
import org.jclouds.atmos.util.AtmosUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrei Savu
 * @see Error codes section at <a href="https://www.synaptic.att.com/assets/us/en/home/Atmos_Programmers_Guide_1.3.4A.pdf" />
 */
public class ThrowIllegalStateExceptionOn400 implements Function<Exception, Object> {

   private final AtmosUtils utils;

   @Inject
   public ThrowIllegalStateExceptionOn400(AtmosUtils utils) {
      this.utils = checkNotNull(utils, "utils is null");
   }

   @Override
   public Object apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException exception = (HttpResponseException) from;
         if (exception.getResponse().getStatusCode() == 400) {
            AtmosError error = parseErrorFromResponse(exception);

            if (error.getCode() == 1016) {
               throw new IllegalStateException("The resource you are trying to create\n" +
                   "already exists.", from);
            }
         }
      }
      throw Throwables.propagate(from);
   }

   @VisibleForTesting
   protected AtmosError parseErrorFromResponse(HttpResponseException responseException) {
      HttpResponse response = responseException.getResponse();
      HttpCommand command = responseException.getCommand();

      byte[] content = HttpUtils.closeClientButKeepContentStream(response);
      return utils.parseAtmosErrorFromContent(command, response, new String(content));
   }
}
