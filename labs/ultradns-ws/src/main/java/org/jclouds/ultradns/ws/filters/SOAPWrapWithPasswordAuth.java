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
package org.jclouds.ultradns.ws.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
public class SOAPWrapWithPasswordAuth implements HttpRequestFilter {
   static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
   static final String SOAP_PREFIX = new StringBuilder()
         .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
         .append("xmlns=\"http://webservice.api.ultra.neustar.com/v01/\"><soapenv:Header>")
         .append("<wsse:Security xmlns:wsse=\"").append(WSSE_NS).append("\"><wsse:UsernameToken>")
         .append("<wsse:Username>%s</wsse:Username><wsse:Password>%s</wsse:Password>")
         .append("</wsse:UsernameToken></wsse:Security></soapenv:Header><soapenv:Body>").toString();
   static final String SOAP_SUFFIX = "</soapenv:Body></soapenv:Envelope>";
   
   private final Supplier<Credentials> creds;

   @Inject
   public SOAPWrapWithPasswordAuth(@Provider Supplier<Credentials> creds) {
      this.creds = creds;
   }

   public HttpRequest filter(HttpRequest request) {
      checkNotNull(request.getPayload(), "request is not ready to wrap; payload not present");
      Credentials current = creds.get();
      String body = request.getPayload().getRawContent().toString();
      Payload wrappedPayload = Payloads.newStringPayload(new StringBuilder()
            .append(format(SOAP_PREFIX, current.identity, current.credential)).append(body).append(SOAP_SUFFIX)
            .toString());
      wrappedPayload.getContentMetadata().setContentType("application/xml");
      return request.toBuilder().payload(wrappedPayload).build();
   }
}
