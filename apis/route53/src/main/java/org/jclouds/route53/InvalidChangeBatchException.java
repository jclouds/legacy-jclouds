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
package org.jclouds.route53;

import org.jclouds.http.HttpResponseException;

import com.google.common.collect.ImmutableList;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_ChangeResourceRecordSets.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class InvalidChangeBatchException extends IllegalArgumentException {
   private static final long serialVersionUID = 1L;

   private final ImmutableList<String> messages;

   public InvalidChangeBatchException(ImmutableList<String> messages, HttpResponseException cause) {
      super(messages.toString(), cause);
      this.messages = messages;
   }

   public ImmutableList<String> getMessages() {
      return messages;
   }
}
