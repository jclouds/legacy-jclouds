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

package org.jclouds.abiquo.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.xml.XMLParser;

import com.abiquo.model.transport.AcceptedRequestDto;
import com.google.inject.TypeLiteral;

/**
 * Return an {@link AcceptedRequestDto} representing the asynchronous task or
 * <code>null</code> if the operation completed synchronously.
 * <p>
 * Operations that generate asynchronous tasks will return one of the following:
 * <ul>
 * <li>204 if the operation completed synchronously</li>
 * <li>202 with the asynchronous task reference in the body if the operation has
 * been submitted and will be executed asynchronously</li>
 * </ul>
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ReturnTaskReferenceOrNull extends ParseXMLWithJAXB<AcceptedRequestDto<String>> {
   @Inject
   public ReturnTaskReferenceOrNull(final XMLParser xml, final TypeLiteral<AcceptedRequestDto<String>> type) {
      super(xml, type);
   }

   @Override
   public AcceptedRequestDto<String> apply(final HttpResponse from) {
      if (from.getStatusCode() == Status.NO_CONTENT.getStatusCode()) {
         releasePayload(from);
         return null;
      } else {
         return super.apply(from);
      }
   }

}
