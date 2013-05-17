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
package org.jclouds.s3.reference;

/**
 * Additional headers specified by Amazon S3 REST API.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/latest/index.html?RESTAuthentication.html"
 *      />
 * @author Adrian Cole
 * 
 */
public interface S3Headers {

   /**
    * Amazon S3 has clones, which often replace this with their particular tag.
    */
   public static final String DEFAULT_AMAZON_HEADERTAG = "amz";

   public static final String HEADER_PREFIX = "x-" + DEFAULT_AMAZON_HEADERTAG + "-";

   /**
    * The canned ACL to apply to the object. Options include private, public-read,
    * public-read-write, and authenticated-read. For more information, see REST Access Control
    * Policy.
    */
   public static final String CANNED_ACL = HEADER_PREFIX + "acl";

   public static final String AMZ_ETAG = HEADER_PREFIX + "meta-object-eTag";

   /**
    * Amazon's alternative date header
    */
   public static final String ALTERNATE_DATE = HEADER_PREFIX + "date";

   /**
    * Prefix for user metadata
    */
   public static final String USER_METADATA_PREFIX = HEADER_PREFIX + "meta-";

   /**
    * version ID header
    */
   public static final String VERSION_ID = HEADER_PREFIX + "version-id";

   /**
    * Multi-Factor Authentication header
    */
   public static final String MFA = HEADER_PREFIX + "mfa";

   /**
    * response header for a request's AWS request ID
    */
   public static final String REQUEST_ID = HEADER_PREFIX + "request-id";

   /**
    * response header for a request's extended debugging ID
    */
   public static final String EXTENDED_REQUEST_ID = HEADER_PREFIX + "id-2";

   /**
    * request header indicating how to handle metadata when copying an object
    */
   public static final String METADATA_DIRECTIVE = HEADER_PREFIX + "metadata-directive";

   /**
    * DevPay token header
    */
   public static final String SECURITY_TOKEN = HEADER_PREFIX + "security-token";

   /**
    * Header describing what class of storage a user wants
    */
   public static final String STORAGE_CLASS = HEADER_PREFIX + "storage-class";

   /**
    * ETag matching constraint header for the copy object request
    */
   public static final String COPY_SOURCE_IF_MATCH = HEADER_PREFIX + "copy-source-if-match";

   /**
    * ETag non-matching constraint header for the copy object request
    */
   public static final String COPY_SOURCE_IF_NO_MATCH = HEADER_PREFIX + "copy-source-if-none-match";

   /**
    * Unmodified since constraint header for the copy object request
    */
   public static final String COPY_SOURCE_IF_UNMODIFIED_SINCE = HEADER_PREFIX + "copy-source-if-unmodified-since";

   /**
    * Modified since constraint header for the copy object request
    */
   public static final String COPY_SOURCE_IF_MODIFIED_SINCE = HEADER_PREFIX + "copy-source-if-modified-since";

   /**
    * Encrypted symmetric key header that is used in the envelope encryption mechanism
    */
   public static final String CRYPTO_KEY = HEADER_PREFIX + "key";

   /**
    * Initialization vector (IV) header that is used in the symmetric and envelope encryption
    * mechanisms
    */
   public static final String CRYPTO_IV = HEADER_PREFIX + "iv";

   /**
    * JSON-encoded description of encryption materials used during encryption
    */
   public static final String MATERIALS_DESCRIPTION = HEADER_PREFIX + "matdesc";

   /**
    * Instruction file header to be placed in the metadata of instruction files
    */
   public static final String CRYPTO_INSTRUCTION_FILE = HEADER_PREFIX + "crypto-instr-file";
}
