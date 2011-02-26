/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
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

   public static final String CONTENT_MD5 = "Content-MD5";

   /** Prefix for general Amazon headers: x-amz- */
   public static final String AMAZON_PREFIX = "x-amz-";

   /**
    * The canned ACL to apply to the object. Options include private, public-read,
    * public-read-write, and authenticated-read. For more information, see REST Access Control
    * Policy.
    */
   public static final String CANNED_ACL = "x-amz-acl";
   
   public static final String AMZ_MD5 = "x-amz-meta-object-eTag";
   
   /** Amazon's alternative date header: x-amz-date */
   public static final String ALTERNATE_DATE = "x-amz-date";

   /** Prefix for user metadata: x-amz-meta- */
   public static final String USER_METADATA_PREFIX = "x-amz-meta-";

   /** version ID header */
   public static final String VERSION_ID = "x-amz-version-id";

   /** Multi-Factor Authentication header */
   public static final String MFA = "x-amz-mfa";

   /** response header for a request's AWS request ID */
   public static final String REQUEST_ID = "x-amz-request-id";

   /** response header for a request's extended debugging ID */
   public static final String EXTENDED_REQUEST_ID = "x-amz-id-2";

   /** request header indicating how to handle metadata when copying an object */
   public static final String METADATA_DIRECTIVE = "x-amz-metadata-directive";

   /** DevPay token header */
   public static final String SECURITY_TOKEN = "x-amz-security-token";

   /** Header describing what class of storage a user wants */
   public static final String STORAGE_CLASS = "x-amz-storage-class";

   /** ETag matching constraint header for the copy object request */
   public static final String COPY_SOURCE_IF_MATCH = "x-amz-copy-source-if-match";

   /** ETag non-matching constraint header for the copy object request */
   public static final String COPY_SOURCE_IF_NO_MATCH = "x-amz-copy-source-if-none-match";

   /** Unmodified since constraint header for the copy object request */
   public static final String COPY_SOURCE_IF_UNMODIFIED_SINCE = "x-amz-copy-source-if-unmodified-since";

   /** Modified since constraint header for the copy object request */
   public static final String COPY_SOURCE_IF_MODIFIED_SINCE = "x-amz-copy-source-if-modified-since";

   /** Range header for the get object request */
   public static final String RANGE = "Range";

   /** Modified since constraint header for the get object request */
   public static final String GET_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";

   /** Unmodified since constraint header for the get object request */
   public static final String GET_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

   /** ETag matching constraint header for the get object request */
   public static final String GET_OBJECT_IF_MATCH = "If-Match";

   /** ETag non-matching constraint header for the get object request */
   public static final String GET_OBJECT_IF_NONE_MATCH = "If-None-Match";

   /** Encrypted symmetric key header that is used in the envelope encryption mechanism */
   public static final String CRYPTO_KEY = "x-amz-key";
   
   /** Initialization vector (IV) header that is used in the symmetric and envelope encryption mechanisms */
   public static final String CRYPTO_IV = "x-amz-iv";

   /** JSON-encoded description of encryption materials used during encryption */ 
   public static final String MATERIALS_DESCRIPTION = "x-amz-matdesc";
   
   /** Instruction file header to be placed in the metadata of instruction files */
   public static final String CRYPTO_INSTRUCTION_FILE = "x-amz-crypto-instr-file";
}
