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
package org.jclouds.sqs.xml;

import javax.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryDeleteMessageBatch.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class DeleteMessageBatchResponseHandler extends BatchResponseHandler<String> {

   @Inject
   protected DeleteMessageBatchResponseHandler(IdHandler resultHandler, BatchErrorHandler errorHandler) {
      super("DeleteMessageBatchResultEntry", resultHandler, errorHandler);
   }

}
