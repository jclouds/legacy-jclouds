/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.aws.s3.blobstore.functions.BucketToContainerListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ResourceToBucketList;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore.FutureBase;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link S3AsyncBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubS3AsyncClient implements S3AsyncClient {
   private final DateService dateService;
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubAsyncBlobStore blobStore;
   private final LoggerFactory logFactory;
   private final S3Object.Factory objectProvider;
   private final Blob.Factory blobProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final BlobToObjectMetadata blob2ObjectMetadata;
   private final BucketToContainerListOptions bucket2ContainerListOptions;
   private final ResourceToBucketList resource2BucketList;

   @Inject
   private StubS3AsyncClient(StubAsyncBlobStore blobStore, LoggerFactory logFactory,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            DateService dateService, S3Object.Factory objectProvider, Blob.Factory blobProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob,
            BlobToObject blob2Object, BlobToObjectMetadata blob2ObjectMetadata,
            BucketToContainerListOptions bucket2ContainerListOptions,
            ResourceToBucketList resource2BucketList) {
      this.blobStore = blobStore;
      this.logFactory = logFactory;
      this.objectProvider = objectProvider;
      this.blobProvider = blobProvider;
      this.dateService = dateService;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectMetadata = checkNotNull(blob2ObjectMetadata, "blob2ObjectMetadata");
      this.bucket2ContainerListOptions = checkNotNull(bucket2ContainerListOptions,
               "bucket2ContainerListOptions");
      this.resource2BucketList = checkNotNull(resource2BucketList, "resource2BucketList");
   }

   public static final String TEST_ACL_ID = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
   public static final String TEST_ACL_EMAIL = "james@misterm.org";
   private static Map<String, BucketMetadata.LocationConstraint> bucketToLocation = new ConcurrentHashMap<String, BucketMetadata.LocationConstraint>();

   /**
    * An S3 item's "ACL" may be a {@link CannedAccessPolicy} or an {@link AccessControlList}.
    */
   private static Map<String, Object> keyToAcl = new ConcurrentHashMap<String, Object>();

   public static final String DEFAULT_OWNER_ID = "abc123";

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   public Future<Boolean> putBucketIfNotExists(String name, PutBucketOptions... optionsList) {
      final PutBucketOptions options = (optionsList.length == 0) ? new PutBucketOptions()
               : optionsList[0];
      if (options.getLocationConstraint() != null)
         bucketToLocation.put(name, options.getLocationConstraint());
      keyToAcl.put(name, options.getAcl());
      return blobStore.createContainer(name);
   }

   public Future<ListBucketResponse> listBucket(final String name, ListBucketOptions... optionsList) {
      ListContainerOptions options = bucket2ContainerListOptions.apply(optionsList);
      return wrapFuture(blobStore.list(name, options), resource2BucketList);
   }

   public Future<ObjectMetadata> copyObject(final String sourceBucket, final String sourceObject,
            final String destinationBucket, final String destinationObject,
            CopyObjectOptions... nullableOptions) {
      final CopyObjectOptions options = (nullableOptions.length == 0) ? new CopyObjectOptions()
               : nullableOptions[0];
      return new FutureBase<ObjectMetadata>() {
         public ObjectMetadata get() throws InterruptedException, ExecutionException {
            ConcurrentMap<String, Blob> source = blobStore.getContainerToBlobs().get(sourceBucket);
            ConcurrentMap<String, Blob> dest = blobStore.getContainerToBlobs().get(
                     destinationBucket);
            if (source.containsKey(sourceObject)) {
               Blob object = source.get(sourceObject);
               if (options.getIfMatch() != null) {
                  if (!object.getMetadata().getETag().equals(options.getIfMatch()))
                     blobStore.throwResponseException(412);

               }
               if (options.getIfNoneMatch() != null) {
                  if (object.getMetadata().getETag().equals(options.getIfNoneMatch()))
                     blobStore.throwResponseException(412);
               }
               if (options.getIfModifiedSince() != null) {
                  DateTime modifiedSince = dateService
                           .rfc822DateParse(options.getIfModifiedSince());
                  if (modifiedSince.isAfter(object.getMetadata().getLastModified()))
                     blobStore.throwResponseException(412);

               }
               if (options.getIfUnmodifiedSince() != null) {
                  DateTime unmodifiedSince = dateService.rfc822DateParse(options
                           .getIfUnmodifiedSince());
                  if (unmodifiedSince.isBefore(object.getMetadata().getLastModified()))
                     blobStore.throwResponseException(412);
               }
               Blob sourceS3 = source.get(sourceObject);
               MutableBlobMetadata newMd = blobStore
                        .copy(sourceS3.getMetadata(), destinationObject);
               if (options.getAcl() != null)
                  keyToAcl.put(destinationBucket + "/" + destinationObject, options.getAcl());

               newMd.setLastModified(new DateTime());
               Blob newBlob = blobProvider.create(newMd);
               newBlob.setData(sourceS3.getData());
               dest.put(destinationObject, newBlob);
               return blob2ObjectMetadata.apply(blobStore.copy(newMd));
            }
            throw new KeyNotFoundException(sourceBucket, sourceObject);
         }
      };
   }

   public Future<String> putObject(final String bucketName, final S3Object object,
            PutObjectOptions... nullableOptions) {
      final PutObjectOptions options = (nullableOptions.length == 0) ? new PutObjectOptions()
               : nullableOptions[0];
      if (options.getAcl() != null)
         keyToAcl.put(bucketName + "/" + object.getMetadata().getKey(), options.getAcl());
      return blobStore.putBlob(bucketName, object2Blob.apply(object));
   }

   protected AccessControlList getACLforS3Item(String bucketAndObjectKey) {
      AccessControlList acl = null;
      Object aclObj = keyToAcl.get(bucketAndObjectKey);
      if (aclObj instanceof AccessControlList) {
         acl = (AccessControlList) aclObj;
      } else if (aclObj instanceof CannedAccessPolicy) {
         acl = AccessControlList.fromCannedAccessPolicy((CannedAccessPolicy) aclObj,
                  DEFAULT_OWNER_ID);
      } else if (aclObj == null) {
         // Default to private access policy
         acl = AccessControlList.fromCannedAccessPolicy(CannedAccessPolicy.PRIVATE,
                  DEFAULT_OWNER_ID);
      }
      return acl;
   }

   public Future<AccessControlList> getBucketACL(final String bucket) {
      return new FutureBase<AccessControlList>() {
         public AccessControlList get() throws InterruptedException, ExecutionException {
            return getACLforS3Item(bucket);
         }
      };
   }

   public Future<AccessControlList> getObjectACL(final String bucket, final String objectKey) {
      return new FutureBase<AccessControlList>() {
         public AccessControlList get() throws InterruptedException, ExecutionException {
            return getACLforS3Item(bucket + "/" + objectKey);
         }
      };
   }

   /**
    * Replace any AmazonCustomerByEmail grantees with a somewhat-arbitrary canonical user grantee,
    * to match S3 which substitutes each email address grantee with that user's corresponding ID. In
    * short, although you can PUT email address grantees, these are actually subsequently returned
    * by S3 as canonical user grantees.
    * 
    * @param acl
    * @return
    */
   protected AccessControlList sanitizeUploadedACL(AccessControlList acl) {
      // Replace any email address grantees with canonical user grantees, using
      // the acl's owner ID as the surrogate replacement.
      for (Grant grant : acl.getGrants()) {
         if (grant.getGrantee() instanceof EmailAddressGrantee) {
            EmailAddressGrantee emailGrantee = (EmailAddressGrantee) grant.getGrantee();
            String id = emailGrantee.getEmailAddress().equals(TEST_ACL_EMAIL) ? TEST_ACL_ID : acl
                     .getOwner().getId();
            grant.setGrantee(new CanonicalUserGrantee(id, acl.getOwner().getDisplayName()));
         }
      }
      return acl;
   }

   public Future<Boolean> putBucketACL(final String bucket, final AccessControlList acl) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            keyToAcl.put(bucket, sanitizeUploadedACL(acl));
            return true;
         }
      };
   }

   public Future<Boolean> putObjectACL(final String bucket, final String objectKey,
            final AccessControlList acl) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            keyToAcl.put(bucket + "/" + objectKey, sanitizeUploadedACL(acl));
            return true;
         }
      };
   }

   public Future<Boolean> bucketExists(final String bucketName) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            return blobStore.getContainerToBlobs().containsKey(bucketName);
         }
      };
   }

   public Future<Boolean> deleteBucketIfEmpty(String bucketName) {
      return blobStore.deleteContainerImpl(bucketName);
   }

   public Future<Void> deleteObject(String bucketName, String key) {
      return blobStore.removeBlob(bucketName, key);
   }

   public Future<S3Object> getObject(final String bucketName, final String key,
            final GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return wrapFuture(blobStore.getBlob(bucketName, key, getOptions), blob2Object);
   }

   public Future<ObjectMetadata> headObject(String bucketName, String key) {
      return wrapFuture(blobStore.blobMetadata(bucketName, key),
               new Function<BlobMetadata, ObjectMetadata>() {

                  @Override
                  public ObjectMetadata apply(BlobMetadata from) {

                     return blob2ObjectMetadata.apply(from);
                  }

               });
   }

   public Future<? extends SortedSet<BucketMetadata>> listOwnedBuckets() {
      return new FutureBase<SortedSet<BucketMetadata>>() {

         public SortedSet<BucketMetadata> get() throws InterruptedException, ExecutionException {
            return Sets.newTreeSet(Iterables.transform(blobStore.getContainerToBlobs().keySet(),
                     new Function<String, BucketMetadata>() {
                        public BucketMetadata apply(String name) {
                           return new BucketMetadata(name, null, null);
                        }

                     }));
         }
      };

   }

   public S3Object newS3Object() {
      return objectProvider.create(null);
   }

}
