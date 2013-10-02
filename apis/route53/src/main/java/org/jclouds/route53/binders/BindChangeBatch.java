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
package org.jclouds.route53.binders;

import static org.jclouds.io.Payloads.newStringPayload;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.rest.Binder;
import org.jclouds.route53.domain.ChangeBatch;
import org.jclouds.route53.domain.ChangeBatch.ActionOnResourceRecordSet;
import org.jclouds.route53.functions.SerializeRRS;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindChangeBatch implements Binder {

   private static final SerializeRRS xml = new SerializeRRS();

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      ChangeBatch from = ChangeBatch.class.cast(payload);
      StringBuilder b = new StringBuilder();
      b.append("<ChangeResourceRecordSetsRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><ChangeBatch>");
      if (from.getComment().isPresent())
         b.append("<Comment>").append(from.getComment().get()).append("</Comment>");
      b.append("<Changes>");
      for (ActionOnResourceRecordSet change : from)
         b.append("<Change>").append("<Action>").append(change.getAction()).append("</Action>")
               .append(xml.apply(change.getRRS())).append("</Change>");
      b.append("</Changes>");
      b.append("</ChangeBatch></ChangeResourceRecordSetsRequest>");
      Payload xmlPayload = newStringPayload(b.toString());
      xmlPayload.getContentMetadata().setContentType("application/xml");
      return (R) request.toBuilder().payload(xmlPayload).build();
   }
}
