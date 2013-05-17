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
package org.jclouds.sqs.reference;

/**
 * Configuration properties and constants used in SQS connections.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryParams.html"
 *      />
 * @author Adrian Cole
 */
public interface SQSParameters {

   /**
    * The action to perform. For example: CreateQueue.
    */
   public static final String ACTION = "Action";

   /**
    * The API version to use, as specified in the WSDL. For example: 2011-10-01.
    */
   public static final String VERSION = "Version";

   /**
    * Your Access Key ID. For example: 0AS7253JW73RRM652K02. For more information, see Your AWS
    * Identifiers in the Amazon SQS Developer Guide.
    */
   public static final String AWS_ACCESS_KEY_ID = "AWSAccessKeyId";

   /**
    * The date and time the request is signed, in the format YYYY-MM-DDThh:mm:ssZ, as specified in
    * the ISO 8601 standard. Query requests must include either Timestamp or Expires, but not both.
    * 
    */
   public static final String TIMESTAMP = "Timestamp";

   /**
    * The date and time at which the signature included in the request expires, in the format
    * YYYY-MM-DDThh:mm:ssZ, as specified in the ISO 8601 standard. Query requests must include
    * either Timestamp or Expires, but not both.
    */
   public static final String EXPIRES = "Expires";
   /**
    * A request signature (for information, see Request Authentication in the Amazon SQS Developer
    * Guide). For example: Qnpl4Qk/7tINHzfXCiT7VbBatDA=.
    */
   public static final String SIGNATURE = "Signature";
   /**
    *Required when you use signature version 2 with Query requests. For more information, see Query
    * Request Authentication in the Amazon SQS Developer Guide.
    */
   public static final String SIGNATURE_METHOD = "SignatureMethod";
   /**
    * For more information, see Query Request Authentication in the Amazon SQS Developer Guide.
    */
   public static final String SIGNATURE_VERSION = "SignatureVersion";
}
