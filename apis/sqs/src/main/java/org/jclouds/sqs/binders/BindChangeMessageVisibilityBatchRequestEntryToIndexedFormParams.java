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
package org.jclouds.sqs.binders;

import java.util.Map;

import org.jclouds.aws.binders.BindTableToIndexedFormParams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams extends BindTableToIndexedFormParams
      implements MapBinder {

   protected BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams() {
      super("ChangeMessageVisibilityBatchRequestEntry.%d.Id",
            "ChangeMessageVisibilityBatchRequestEntry.%d.ReceiptHandle",
            "ChangeMessageVisibilityBatchRequestEntry.%d.VisibilityTimeout");
   }

   public Map<String, String> idReceiptHandle(Iterable<String> input) {
      return Maps.uniqueIndex(input, new Function<String, String>() {
         int index = 1;

         @Override
         public String apply(String input) {
            return index++ + "";
         }
      });
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, String> idReceiptHandle = (Map<String, String>) postParams.get("idReceiptHandle");
      if (idReceiptHandle == null) {
         idReceiptHandle = idReceiptHandle((Iterable<String>) postParams.get("receiptHandles"));
      }
      int visibilityTimeout = (Integer) postParams.get("visibilityTimeout");

      Builder<Object, Object, Object> builder = ImmutableTable.builder();
      for (Map.Entry<?, ?> entry : idReceiptHandle.entrySet())
         builder.put(entry.getKey(), entry.getValue(), visibilityTimeout);
      return bindToRequest(request, (Object) builder.build());
   }
}
