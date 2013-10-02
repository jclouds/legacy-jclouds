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
package org.jclouds.azureblob.binders;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import java.util.List;

/**
 * Binds a list of blocks to a putBlockList request
 *
 * <?xml version="1.0" encoding="utf-8"?>
 * <BlockList>
 *   <Committed>first-base64-encoded-block-id</Committed>
 *   <Uncommitted>second-base64-encoded-block-id</Uncommitted>
 *   <Latest>third-base64-encoded-block-id</Latest>
 *   ...
 * </BlockList>
 */
public class BindAzureBlocksToRequest implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      List<String> blockIds = (List<String>)input;
      StringBuilder content = new StringBuilder();
      content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      content.append("<BlockList>");
      for (String id : blockIds) {
         content.append("<Latest>").append(id).append("</Latest>");
      }
      content.append("</BlockList>");
      request.setPayload(content.toString());
      return request;
   }
}
