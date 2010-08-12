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

package org.jclouds.aws.s3.jets3t;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.http.options.GetOptions;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3BucketLoggingStatus;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;

/**
 * A JetS3t S3Service implemented by JClouds
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class JCloudsS3Service extends S3Service {

   private static final long serialVersionUID = 1L;

   private final BlobStoreContext context;
   private final S3Client connection;

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
   protected JCloudsS3Service(AWSCredentials awsCredentials, Module... modules) throws S3ServiceException {
      super(awsCredentials);
      context = new BlobStoreContextFactory().createContext("s3", awsCredentials.getAccessKey(), awsCredentials
               .getSecretKey(), ImmutableList.<Module> copyOf(modules));
      connection = (S3Client) context.getProviderSpecificContext().getApi();
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
   protected Map copyObjectImpl(String sourceBucketName, String sourceObjectKey, String destinationBucketName,
            String destinationObjectKey, AccessControlList acl, Map destinationMetadata, Calendar ifModifiedSince,
            Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags) throws S3ServiceException {
      try {
         CopyObjectOptions options = Util.convertCopyObjectOptions(acl, destinationMetadata, ifModifiedSince,
                  ifUnmodifiedSince, ifMatchTags, ifNoneMatchTags);
         ObjectMetadata jcObjectMetadata = connection.copyObject(sourceBucketName, sourceObjectKey,
                  destinationBucketName, destinationObjectKey, options);

         Map map = new HashMap();
         // Result fields returned when copy is successful.
         map.put("Last-Modified", jcObjectMetadata.getLastModified());
         map.put("ETag", jcObjectMetadata.getETag());
         return map;
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error copying object", e);
      }
   }

   @Override
   protected S3Bucket createBucketImpl(String bucketName, String location, AccessControlList acl)
            throws S3ServiceException {
      if (location != null)
         throw new UnsupportedOperationException("Bucket location is not yet supported");
      if (acl != null)
         throw new UnsupportedOperationException("Bucket ACL is not yet supported");

      try {
         if (connection.putBucketInRegion(null, bucketName)) {
            // Bucket created.
         }
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error creating bucket: " + bucketName, e);
      }
      return new S3Bucket(bucketName);
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3AsyncBlobStore#deleteContainer(String)
    */
   @Override
   protected void deleteBucketImpl(String bucketName) throws S3ServiceException {
      try {
         connection.deleteBucketIfEmpty(bucketName);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error deleting bucket: " + bucketName, e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3AsyncBlobStore#removeBlob(String, String)
    */
   @Override
   protected void deleteObjectImpl(String bucketName, String objectKey) throws S3ServiceException {
      try {
         connection.deleteObject(bucketName, objectKey);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException(String.format("error deleting object: %1$s:%2$s", bucketName, objectKey), e);
      }
   }

   @Override
   protected AccessControlList getBucketAclImpl(String bucketName) throws S3ServiceException {
      try {
         org.jclouds.aws.s3.domain.AccessControlList jcACL = connection.getBucketACL(bucketName);
         return Util.convertAccessControlList(jcACL);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error getting bucket's ACL: " + bucketName, e);
      }
   }

   @Override
   protected String getBucketLocationImpl(String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected S3BucketLoggingStatus getBucketLoggingStatusImpl(String bucketName) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();
   }

   @Override
   protected AccessControlList getObjectAclImpl(String bucketName, String objectKey) throws S3ServiceException {
      try {
         org.jclouds.aws.s3.domain.AccessControlList jcACL = connection.getObjectACL(bucketName, objectKey);
         return Util.convertAccessControlList(jcACL);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error getting object's ACL", e);
      }
   }

   @Override
   protected S3Object getObjectDetailsImpl(String bucketName, String objectKey, Calendar ifModifiedSince,
            Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags) throws S3ServiceException {
      try {
         if (ifModifiedSince != null)
            throw new IllegalArgumentException("ifModifiedSince");
         if (ifUnmodifiedSince != null)
            throw new IllegalArgumentException("ifUnmodifiedSince");
         if (ifMatchTags != null)
            throw new IllegalArgumentException("ifMatchTags");
         if (ifNoneMatchTags != null)
            throw new IllegalArgumentException("ifNoneMatchTags");

         return Util.convertObjectHead(connection.headObject(bucketName, objectKey));
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException(String.format("error retrieving object head: %1$s:%2$s", bucketName, objectKey),
                  e);
      }
   }

   @Override
   protected S3Object getObjectImpl(String bucketName, String objectKey, Calendar ifModifiedSince,
            Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags, Long byteRangeStart,
            Long byteRangeEnd) throws S3ServiceException {
      try {
         GetOptions options = Util.convertGetObjectOptions(ifModifiedSince, ifUnmodifiedSince, ifMatchTags,
                  ifNoneMatchTags);
         return Util.convertObject(connection.getObject(bucketName, objectKey, options));
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException(String.format("error retrieving object: %1$s:%2$s", bucketName, objectKey), e);
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
         Set<org.jclouds.aws.s3.domain.BucketMetadata> jcBucketList = connection.listOwnedBuckets();
         return Util.convertBuckets(jcBucketList);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error listing buckets", e);
      }
   }

   @Override
   protected S3ObjectsChunk listObjectsChunkedImpl(String bucketName, String prefix, String delimiter,
            long maxListingLength, String priorLastKey, boolean completeListing) throws S3ServiceException {
      try {
         List<S3Object> jsObjects = new ArrayList<S3Object>();
         List<String> commonPrefixes = new ArrayList<String>();
         ListBucketResponse jcBucket = null;
         do {
            ListBucketOptions options = Util.convertListObjectOptions(prefix, priorLastKey, delimiter,
                     (int) maxListingLength);

            jcBucket = connection.listBucket(bucketName, options);

            jsObjects.addAll(Arrays.asList(Util.convertObjectHeads(jcBucket)));
            commonPrefixes.addAll(jcBucket.getCommonPrefixes());
            if (jcBucket.isTruncated()) {
               priorLastKey = jsObjects.get(jsObjects.size() - 1).getKey();
            } else {
               priorLastKey = null;
            }
         } while (completeListing && jcBucket.isTruncated()); // Build entire listing if requested

         return new S3ObjectsChunk(
                  prefix, // Return the supplied prefix, not the one in the S3
                  // response.
                  jcBucket.getDelimiter(), (S3Object[]) jsObjects.toArray(new S3Object[jsObjects.size()]),
                  (String[]) commonPrefixes.toArray(new String[commonPrefixes.size()]), priorLastKey);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error listing objects in bucket " + bucketName, e);
      }
   }

   @Override
   protected S3Object[] listObjectsImpl(String bucketName, String prefix, String delimiter, long maxListingLength)
            throws S3ServiceException {
      try {
         return listObjectsChunked(bucketName, prefix, delimiter, maxListingLength, null, true).getObjects();
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error listing objects in bucket " + bucketName, e);
      }
   }

   @Override
   protected void putBucketAclImpl(String bucketName, AccessControlList jsACL) throws S3ServiceException {
      try {
         org.jclouds.aws.s3.domain.AccessControlList jcACL = Util.convertAccessControlList(jsACL);
         connection.putBucketACL(bucketName, jcACL);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error putting bucket's ACL: " + bucketName, e);
      }
   }

   @Override
   protected void putObjectAclImpl(String bucketName, String objectKey, AccessControlList jsACL)
            throws S3ServiceException {
      try {
         org.jclouds.aws.s3.domain.AccessControlList jcACL = Util.convertAccessControlList(jsACL);
         connection.putObjectACL(bucketName, objectKey, jcACL);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error putting object's ACL", e);
      }
   }

   @Override
   protected S3Object putObjectImpl(String bucketName, S3Object jsObject) throws S3ServiceException {
      try {
         PutObjectOptions options = Util.convertPutObjectOptions(jsObject.getAcl());
         org.jclouds.aws.s3.domain.S3Object jcObject = Util.convertObject(jsObject, connection.newS3Object());
         String eTag = connection.putObject(bucketName, jcObject, options);
         jsObject.setETag(eTag);
         return jsObject;
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, S3ServiceException.class);
         throw new S3ServiceException("error putting object", e);
      }
   }

   @Override
   protected void setBucketLoggingStatusImpl(String bucketName, S3BucketLoggingStatus status) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

   @Override
   protected void setRequesterPaysBucketImpl(String bucketName, boolean requesterPays) throws S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

   protected org.jets3t.service.model.S3Owner getAccountOwnerImpl() throws org.jets3t.service.S3ServiceException {
      // TODO Unimplemented
      throw new UnsupportedOperationException();

   }

}
