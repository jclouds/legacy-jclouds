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
package org.jclouds.s3.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.s3.domain.Payer;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindPayerToXmlPayload implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      checkArgument(checkNotNull(toBind, "toBind") instanceof Payer, "this binder is only valid for Payer!");
      String text = String
            .format(
                  "<RequestPaymentConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Payer>%s</Payer></RequestPaymentConfiguration>",
                  ((Payer) toBind).value());
      request.setPayload(text);
      request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_XML);
      return request;
   }
}
