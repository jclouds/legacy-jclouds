/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.jets3t;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3ContextFactory;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.util.Utils;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3BucketLoggingStatus;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import com.google.inject.Module;

/**
 * A JetS3t S3Service implemented by JClouds
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class JCloudsS3Service extends S3Service {

   private static final long serialVersionUID = 1L;

   private final S3Context context;
   private final S3Connection connection;

   private final long requestTimeoutMilliseconds = 10000;

   /**
    * Initializes a JClouds context to S3.
    * 
    * @param awsCredentials
    *           - credentials to access S3
    * @param modules
    *           - Module that configures a FutureHttpClient, if not specified, default is
    *           URLFetchServiceClientModule
    * @throws S3ServiceException
    */
   protected JCloudsS3Service(AWSCredentials awsCredentials, Module... modules)
            throws S3ServiceException {
      super(awsCredentials);
      context = S3ContextFactory.createS3Context(awsCredentials.getAccessKey(), awsCredentials
               .getSecretKey(), modules);
      connection = context.getConnection();
   }

   @Override
   public int checkBucketStatus(final String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   protected Map copyObjectImpl(String sourceBucketName, String sourceObjectKey,
            String destinationBucketName, String destinationObjectKey, AccessControlList acl,
            Map destinationMetadata, Calendar ifModifiedSince, Calendar ifUnmodifiedSince,
            String[] ifMatchTags, String[] ifNoneMatchTags) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected S3Bucket createBucketImpl(String bucketName, String location, AccessControlList acl)
            throws S3ServiceException {
      if (location != null)
         throw new UnsupportedOperationException("Bucket location is not yet supported");
      if (acl != null)
         throw new UnsupportedOperationException("Bucket ACL is not yet supported");

      try {
         if (connection.putBucketIfNotExists(bucketName).get()) {
            // Bucket created.
         }
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException("error creating bucket: " + bucketName, e);
      }
      return new S3Bucket(bucketName);
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#deleteBucketIfEmpty(String)
    */
   @Override
   protected void deleteBucketImpl(String bucketName) throws S3ServiceException {
      try {
         connection.deleteBucketIfEmpty(bucketName).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException("error deleting bucket: " + bucketName, e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#deleteObject(String, String)
    */
   @Override
   protected void deleteObjectImpl(String bucketName, String objectKey) throws S3ServiceException {
      try {
         connection.deleteObject(bucketName, objectKey).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException(String.format("error deleting object: %1$s:%2$s", bucketName,
                  objectKey), e);
      }
   }

   @Override
   protected AccessControlList getBucketAclImpl(String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected String getBucketLocationImpl(String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected S3BucketLoggingStatus getBucketLoggingStatusImpl(String bucketName)
            throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected AccessControlList getObjectAclImpl(String bucketName, String objectKey)
            throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected S3Object getObjectDetailsImpl(String bucketName, String objectKey,
            Calendar ifModifiedSince, Calendar ifUnmodifiedSince, String[] ifMatchTags,
            String[] ifNoneMatchTags) throws S3ServiceException {
      try {
         if (ifModifiedSince != null)
            throw new IllegalArgumentException("ifModifiedSince");
         if (ifUnmodifiedSince != null)
            throw new IllegalArgumentException("ifUnmodifiedSince");
         if (ifMatchTags != null)
            throw new IllegalArgumentException("ifMatchTags");
         if (ifNoneMatchTags != null)
            throw new IllegalArgumentException("ifNoneMatchTags");

         return Util.convertObjectHead(connection.headObject(bucketName, objectKey).get());
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException(String.format("error retrieving object head: %1$s:%2$s",
                  bucketName, objectKey), e);
      }
   }

   @Override
   protected S3Object getObjectImpl(String bucketName, String objectKey, Calendar ifModifiedSince,
            Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags,
            Long byteRangeStart, Long byteRangeEnd) throws S3ServiceException {
      try {
         GetObjectOptions options = Util.convertGetObjectOptions(ifModifiedSince, 
               ifUnmodifiedSince, ifMatchTags, ifNoneMatchTags);
         return Util.convertObject(connection.getObject(bucketName, objectKey, options).get());
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException(String.format("error retrieving object: %1$s:%2$s",
                  bucketName, objectKey), e);
      }
   }

   @Override
   public boolean isBucketAccessible(String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected boolean isRequesterPaysBucketImpl(String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected S3Bucket[] listAllBucketsImpl() throws S3ServiceException {
      try {
         List<org.jclouds.aws.s3.domain.S3Bucket.Metadata> jcBucketList = connection
                  .listOwnedBuckets().get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         return Util.convertBuckets(jcBucketList);
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException("error listing buckets", e);
      }
   }

   @Override
   protected S3ObjectsChunk listObjectsChunkedImpl(String bucketName, String prefix,
            String delimiter, long maxListingLength, String priorLastKey, boolean completeListing)
            throws S3ServiceException {
      try {         
         List<S3Object> jsObjects = new ArrayList<S3Object>();
         List<String> commonPrefixes = new ArrayList<String>();
         org.jclouds.aws.s3.domain.S3Bucket jcBucket = null;
         do {            
            ListBucketOptions options = Util.convertListObjectOptions(prefix, priorLastKey, 
                  delimiter, maxListingLength);
            
            jcBucket = connection.listBucket(bucketName, options)
                  .get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);

            jsObjects.addAll(Arrays.asList(Util.convertObjectHeads(jcBucket.getContents())));
            commonPrefixes.addAll(jcBucket.getCommonPrefixes());
            if (jcBucket.isTruncated()) {
               priorLastKey = jsObjects.get(jsObjects.size() - 1).getKey();
            } else {
               priorLastKey = null;
            }
         } while (completeListing && jcBucket.isTruncated());  // Build entire listing if requested

         return new S3ObjectsChunk(
               prefix,  // Return the supplied prefix, not the one in the S3 response.
               jcBucket.getDelimiter(),
               (S3Object[]) jsObjects.toArray(new S3Object[jsObjects.size()]),
               (String[]) commonPrefixes.toArray(new String[commonPrefixes.size()]),
               priorLastKey);
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException("error listing objects in bucket " + bucketName, e);         
      }
   }

   @Override
   protected S3Object[] listObjectsImpl(String bucketName, String prefix, String delimiter,
            long maxListingLength) throws S3ServiceException 
   {
      try {
         return listObjectsChunked(bucketName, prefix, delimiter, maxListingLength, null, true)
            .getObjects();
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException("error listing objects in bucket " + bucketName, e);         
      }
   }

   @Override
   protected void putBucketAclImpl(String bucketName, AccessControlList acl)
            throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

   @Override
   protected void putObjectAclImpl(String bucketName, String objectKey, AccessControlList acl)
            throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

   @Override
   protected S3Object putObjectImpl(String bucketName, S3Object jsObject) throws S3ServiceException {
      try {         
         PutObjectOptions options = Util.convertPutObjectOptions(jsObject.getAcl());
         org.jclouds.aws.s3.domain.S3Object jcObject = Util.convertObject(jsObject);
         byte md5[] = connection.putObject(bucketName, jcObject, options)
            .get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         jsObject.setMd5Hash(md5);
         return jsObject;
      } catch (Exception e) {
         Utils.<S3ServiceException> rethrowIfRuntimeOrSameType(e);
         throw new S3ServiceException("error putting object", e);         
      }
   }

   @Override
   protected void setBucketLoggingStatusImpl(String bucketName, S3BucketLoggingStatus status)
            throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

   @Override
   protected void setRequesterPaysBucketImpl(String bucketName, boolean requesterPays)
            throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

   protected org.jets3t.service.model.S3Owner getAccountOwnerImpl()
            throws org.jets3t.service.S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

}
