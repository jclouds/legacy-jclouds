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
package org.jclouds.azure.management.binders;

import javax.inject.Singleton;

import org.jclouds.azure.management.domain.OSImageParams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Throwables;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindOSImageParamsToXmlPayload implements Binder {


   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      OSImageParams params = OSImageParams.class.cast(input);
      try {
         return (R) request.toBuilder().payload(XMLBuilder.create("OSImage").a("xmlns", "http://schemas.microsoft.com/windowsazure")
                                                          .e("Label").t(params.getLabel()).up()
                                                          .e("MediaLink").t(params.getMediaLink().toASCIIString()).up()
                                                          .e("Name").t(params.getName()).up()
                                                          .e("OS").t(params.getOS().toString()).up()
                                                          .up().asString()).build();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }

   }

}
