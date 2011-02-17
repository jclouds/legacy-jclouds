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

package org.jclouds.s3.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.concurrent.Futures;
import org.jclouds.date.DateService;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.options.GetOptions;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.s3.blobstore.functions.BucketToContainerListOptions;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.blobstore.functions.ResourceToBucketList;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.Payer;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grant;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link S3AsyncBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@Singleton
public class StubS3AsyncClient implements S3AsyncClient {
   private final DateService dateService;
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final AsyncBlobStore blobStore;
   private final S3Object.Factory objectProvider;
   private final Blob.Factory blobProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final BlobToObjectMetadata blob2ObjectMetadata;
   private final BucketToContainerListOptions bucket2ContainerListOptions;
   private final ResourceToBucketList resource2BucketList;
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs;
   private final ConcurrentMap<String, Location> containerToLocation;
   private final ExecutorService service;

   @Inject
   private StubS3AsyncClient(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            TransientAsyncBlobStore blobStore, ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            ConcurrentMap<String, Location> containerToLocation, DateService dateService,
            S3Object.Factory objectProvider, Blob.Factory blobProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobToObjectMetadata blob2ObjectMetadata, BucketToContainerListOptions bucket2ContainerListOptions,
            ResourceToBucketList resource2BucketList) {
      this.service = service;
      this.containerToBlobs = containerToBlobs;
      this.containerToLocation = containerToLocation;
      this.blobStore = blobStore;
      this.objectProvider = objectProvider;
      this.blobProvider = blobProvider;
      this.dateService = dateService;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectMetadata = checkNotNull(blob2ObjectMetadata, "blob2ObjectMetadata");
      this.bucket2ContainerListOptions = checkNotNull(bucket2ContainerListOptions, "bucket2ContainerListOptions");
      this.resource2BucketList = checkNotNull(resource2BucketList, "resource2BucketList");
   }

   public static final String TEST_ACL_ID = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
   public static final String TEST_ACL_EMAIL = "james@misterm.org";

   /**
    * An S3 item's "ACL" may be a {@link CannedAccessPolicy} or an {@link AccessControlList}.
    */
   private static Map<String, Object> keyToAcl = new ConcurrentHashMap<String, Object>();

   public static final String DEFAULT_OWNER_ID = "abc123";

   @Override
   public ListenableFuture<Boolean> putBucketInRegion(@Nullable String region, String name,
            PutBucketOptions... optionsList) {
      region = region == null ? Region.US_STANDARD : region;
      final PutBucketOptions options = (optionsList.length == 0) ? new PutBucketOptions() : optionsList[0];
      keyToAcl.put(name, options.getAcl());
      return blobStore.createContainerInLocation(new LocationBuilder().scope(LocationScope.REGION).id(region)
               .description(region).build(), name);
   }

   public ListenableFuture<ListBucketResponse> listBucket(final String name, ListBucketOptions... optionsList) {
      ListContainerOptions options = bucket2ContainerListOptions.apply(optionsList);
      return Futures.compose(blobStore.list(name, options), resource2BucketList, service);
   }

   public ListenableFuture<ObjectMetadata> copyObject(final String sourceBucket, final String sourceObject,
            final String destinationBucket, final String destinationObject, CopyObjectOptions... nullableOptions) {
      final CopyObjectOptions options = (nullableOptions.length == 0) ? new CopyObjectOptions() : nullableOptions[0];
      ConcurrentMap<String, Blob> source = containerToBlobs.get(sourceBucket);
      ConcurrentMap<String, Blob> dest = containerToBlobs.get(destinationBucket);
      if (source.containsKey(sourceObject)) {
         Blob object = source.get(sourceObject);
         if (options.getIfMatch() != null) {
            if (!object.getMetadata().getETag().equals(options.getIfMatch()))
               return immediateFailedFuture(TransientAsyncBlobStore.returnResponseException(412));
         }
         if (options.getIfNoneMatch() != null) {
            if (object.getMetadata().getETag().equals(options.getIfNoneMatch()))
               return immediateFailedFuture(TransientAsyncBlobStore.returnResponseException(412));
         }
         if (options.getIfModifiedSince() != null) {
            Date modifiedSince = dateService.rfc822DateParse(options.getIfModifiedSince());
            if (modifiedSince.after(object.getMetadata().getLastModified()))
               return immediateFailedFuture(TransientAsyncBlobStore.returnResponseException(412));

         }
         if (options.getIfUnmodifiedSince() != null) {
            Date unmodifiedSince = dateService.rfc822DateParse(options.getIfUnmodifiedSince());
            if (unmodifiedSince.before(object.getMetadata().getLastModified()))
               return immediateFailedFuture(TransientAsyncBlobStore.returnResponseException(412));
         }
         Blob sourceS3 = source.get(sourceObject);
         MutableBlobMetadata newMd = TransientAsyncBlobStore.copy(sourceS3.getMetadata(), destinationObject);
         if (options.getAcl() != null)
            keyToAcl.put(destinationBucket + "/" + destinationObject, options.getAcl());

         newMd.setLastModified(new Date());
         Blob newBlob = blobProvider.create(newMd);
         newBlob.setPayload(sourceS3.getPayload());
         dest.put(destinationObject, newBlob);
         return immediateFuture((ObjectMetadata) blob2ObjectMetadata.apply(TransientAsyncBlobStore.copy(newMd)));
      }
      return immediateFailedFuture(new KeyNotFoundException(sourceBucket, sourceObject, sourceBucket + "/"
               + sourceObject));
   }

   public ListenableFuture<String> putObject(final String bucketName, final S3Object object,
            PutObjectOptions... nullableOptions) {
      final PutObjectOptions options = (nullableOptions.length == 0) ? new PutObjectOptions() : nullableOptions[0];
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
         acl = AccessControlList.fromCannedAccessPolicy((CannedAccessPolicy) aclObj, DEFAULT_OWNER_ID);
      } else if (aclObj == null) {
         // Default to private access policy
         acl = AccessControlList.fromCannedAccessPolicy(CannedAccessPolicy.PRIVATE, DEFAULT_OWNER_ID);
      }
      return acl;
   }

   public ListenableFuture<AccessControlList> getBucketACL(final String bucket) {
      return immediateFuture(getACLforS3Item(bucket));
   }

   public ListenableFuture<AccessControlList> getObjectACL(final String bucket, final String objectKey) {
      return immediateFuture(getACLforS3Item(bucket + "/" + objectKey));
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
            String id = emailGrantee.getEmailAddress().equals(TEST_ACL_EMAIL) ? TEST_ACL_ID : acl.getOwner().getId();
            grant.setGrantee(new CanonicalUserGrantee(id, acl.getOwner().getDisplayName()));
         }
      }
      return acl;
   }

   public ListenableFuture<Boolean> putBucketACL(final String bucket, final AccessControlList acl) {
      keyToAcl.put(bucket, sanitizeUploadedACL(acl));
      return immediateFuture(true);
   }

   public ListenableFuture<Boolean> putObjectACL(final String bucket, final String objectKey,
            final AccessControlList acl) {
      keyToAcl.put(bucket + "/" + objectKey, sanitizeUploadedACL(acl));
      return immediateFuture(true);
   }

   public ListenableFuture<Boolean> bucketExists(final String bucketName) {
      return immediateFuture(containerToBlobs.containsKey(bucketName));
   }

   public ListenableFuture<Boolean> deleteBucketIfEmpty(String bucketName) {
      Boolean returnVal = true;
      if (containerToBlobs.containsKey(bucketName)) {
         if (containerToBlobs.get(bucketName).size() == 0)
            containerToBlobs.remove(bucketName);
         else
            returnVal = false;
      }
      return immediateFuture(returnVal);
   }

   public ListenableFuture<Void> deleteObject(String bucketName, String key) {
      return blobStore.removeBlob(bucketName, key);
   }

   public ListenableFuture<S3Object> getObject(final String bucketName, final String key, final GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return Futures.compose(blobStore.getBlob(bucketName, key, getOptions), blob2Object, service);
   }

   public ListenableFuture<ObjectMetadata> headObject(String bucketName, String key) {
      return Futures.compose(blobStore.blobMetadata(bucketName, key), new Function<BlobMetadata, ObjectMetadata>() {
         @Override
         public ObjectMetadata apply(BlobMetadata from) {
            return blob2ObjectMetadata.apply(from);
         }
      }, service);
   }

   public ListenableFuture<? extends Set<BucketMetadata>> listOwnedBuckets() {
      return immediateFuture(Sets.newLinkedHashSet(Iterables.transform(containerToBlobs.keySet(),
               new Function<String, BucketMetadata>() {
                  public BucketMetadata apply(String name) {
                     return new BucketMetadata(name, null, null);
                  }

               })));
   }

   public S3Object newS3Object() {
      return objectProvider.create(null);
   }

   @Override
   public ListenableFuture<String> getBucketLocation(String bucketName) {
      Location location = containerToLocation.get(bucketName);
      return immediateFuture(location.getId());
   }

   @Override
   public ListenableFuture<Payer> getBucketPayer(String bucketName) {
      return immediateFuture(Payer.BUCKET_OWNER);
   }

   @Override
   public ListenableFuture<Void> setBucketPayer(String bucketName, Payer payer) {
      return immediateFuture(null);

   }

   @Override
   public ListenableFuture<Void> disableBucketLogging(String bucketName) {
      return immediateFuture(null);
   }

   @Override
   public ListenableFuture<Void> enableBucketLogging(String bucketName, BucketLogging logging) {
      return immediateFuture(null);
   }

   @Override
   public ListenableFuture<BucketLogging> getBucketLogging(String bucketName) {
      return immediateFuture(null);
   }

   @Override
   public ListenableFuture<Boolean> objectExists(String bucketName, String key) {
      return immediateFuture(containerToBlobs.get(bucketName).containsKey(key));
   }

}
