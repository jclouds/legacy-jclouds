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
package org.jclouds.aws.s3.internal;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.TreeSetListBucketResponse;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.internal.Nullable;

/**
 * Implementation of {@link S3BlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class StubS3BlobStore extends StubBlobStore<BucketMetadata, ObjectMetadata, S3Object>
         implements S3BlobStore {

   @Inject
   protected StubS3BlobStore(Map<String, Map<String, S3Object>> containerToBlobs,
            DateService dateService, Provider<BucketMetadata> containerMetaProvider,
            Provider<S3Object> blobProvider) {
      super(containerToBlobs, dateService, containerMetaProvider, blobProvider);
   }

   public static final String TEST_ACL_ID = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
   public static final String TEST_ACL_EMAIL = "james@misterm.org";
   private static Map<String, BucketMetadata.LocationConstraint> bucketToLocation = new ConcurrentHashMap<String, BucketMetadata.LocationConstraint>();

   /**
    * An S3 item's "ACL" may be a {@link CannedAccessPolicy} or an {@link AccessControlList}.
    */
   private static Map<String, Object> keyToAcl = new ConcurrentHashMap<String, Object>();

   public static final String DEFAULT_OWNER_ID = "abc123";

   public Future<Boolean> createContainer(String name, @Nullable PutBucketOptions nullableOptions) {
      final PutBucketOptions options = (nullableOptions == null) ? new PutBucketOptions()
               : nullableOptions;
      if (options.getLocationConstraint() != null)
         bucketToLocation.put(name, options.getLocationConstraint());
      keyToAcl.put(name, options.getAcl());
      return super.createContainer(name);
   }

   public Future<ListBucketResponse> listBlobs(final String name, final ListBucketOptions options) {
      return new FutureBase<ListBucketResponse>() {
         public ListBucketResponse get() throws InterruptedException, ExecutionException {
            final Map<String, S3Object> realContents = getContainerToBlobs().get(name);

            if (realContents == null)
               throw new ContainerNotFoundException("name");
            SortedSet<ObjectMetadata> contents = Sets.newTreeSet(Iterables.transform(realContents
                     .keySet(), new Function<String, ObjectMetadata>() {
               public ObjectMetadata apply(String key) {
                  return realContents.get(key).getMetadata();
               }
            }));

            String marker = getFirstQueryOrNull(S3Constants.MARKER, options);
            if (marker != null) {
               final String finalMarker = marker;
               ObjectMetadata lastMarkerMetadata = Iterables.find(contents,
                        new Predicate<ObjectMetadata>() {
                           public boolean apply(ObjectMetadata metadata) {
                              return metadata.getKey().equals(finalMarker);
                           }
                        });
               contents = contents.tailSet(lastMarkerMetadata);
               // amazon spec means after the marker, not including it.
               contents.remove(lastMarkerMetadata);
            }
            final String prefix = getFirstQueryOrNull(S3Constants.PREFIX, options);
            if (prefix != null) {
               contents = Sets.newTreeSet(Iterables.filter(contents,
                        new Predicate<ObjectMetadata>() {
                           public boolean apply(ObjectMetadata o) {
                              return (o != null && o.getKey().startsWith(prefix));
                           }
                        }));
            }

            final String delimiter = getFirstQueryOrNull(S3Constants.DELIMITER, options);
            SortedSet<String> commonPrefixes = null;
            if (delimiter != null) {
               Iterable<String> iterable = Iterables.transform(contents, new CommonPrefixes(
                        prefix != null ? prefix : null, delimiter));
               commonPrefixes = iterable != null ? Sets.newTreeSet(iterable)
                        : new TreeSet<String>();
               commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

               contents = Sets.newTreeSet(Iterables.filter(contents, new DelimiterFilter(
                        prefix != null ? prefix : null, delimiter)));
            }

            final String maxKeysString = getFirstQueryOrNull(S3Constants.MAX_KEYS, options);
            int maxResults = contents.size();
            boolean truncated = false;
            if (maxKeysString != null) {
               int maxKeys = Integer.parseInt(maxKeysString);
               SortedSet<ObjectMetadata> contentsSlice = firstSliceOfSize(contents, maxKeys);
               maxResults = maxKeys;
               if (!contentsSlice.contains(contents.last())) {
                  // Partial listing
                  truncated = true;
                  marker = contentsSlice.last().getKey();
               } else {
                  marker = null;
               }
               contents = contentsSlice;
            }
            return new TreeSetListBucketResponse(name, contents, prefix, marker, maxResults,
                     delimiter, truncated, commonPrefixes);
         }
      };
   }

   public Future<ObjectMetadata> copyBlob(final String sourceBucket, final String sourceObject,
            final String destinationBucket, final String destinationObject,
            @Nullable CopyObjectOptions nullableOptions) {
      final CopyObjectOptions options = (nullableOptions == null) ? new CopyObjectOptions()
               : nullableOptions;
      return new FutureBase<ObjectMetadata>() {
         public ObjectMetadata get() throws InterruptedException, ExecutionException {
            Map<String, S3Object> source = getContainerToBlobs().get(sourceBucket);
            Map<String, S3Object> dest = getContainerToBlobs().get(destinationBucket);
            if (source.containsKey(sourceObject)) {
               S3Object object = source.get(sourceObject);
               if (options.getIfMatch() != null) {
                  if (!Arrays.equals(object.getMetadata().getETag(), HttpUtils
                           .fromHexString(options.getIfMatch().replaceAll("\"", ""))))
                     throwResponseException(412);

               }
               if (options.getIfNoneMatch() != null) {
                  if (Arrays.equals(object.getMetadata().getETag(), HttpUtils.fromHexString(options
                           .getIfNoneMatch().replaceAll("\"", ""))))
                     throwResponseException(412);
               }
               if (options.getIfModifiedSince() != null) {
                  DateTime modifiedSince = dateService
                           .rfc822DateParse(options.getIfModifiedSince());
                  if (modifiedSince.isAfter(object.getMetadata().getLastModified()))
                     throwResponseException(412);

               }
               if (options.getIfUnmodifiedSince() != null) {
                  DateTime unmodifiedSince = dateService.rfc822DateParse(options
                           .getIfUnmodifiedSince());
                  if (unmodifiedSince.isBefore(object.getMetadata().getLastModified()))
                     throwResponseException(412);
               }
               S3Object sourceS3 = source.get(sourceObject);
               ObjectMetadata newMd = copy(sourceS3.getMetadata(), destinationObject);
               if (options.getAcl() != null)
                  keyToAcl.put(destinationBucket + "/" + destinationObject, options.getAcl());
               if (options.getMetadata() != null) {
                  newMd.setUserMetadata(options.getMetadata());
               }
               newMd.setLastModified(new DateTime());
               dest.put(destinationObject, new S3Object(newMd, sourceS3.getData()));
               return copy(newMd);
            }
            throw new KeyNotFoundException(sourceBucket, sourceObject);
         }
      };
   }

   public Future<byte[]> putBlob(final String bucketName, final S3Object object,
            @Nullable PutObjectOptions nullableOptions) {
      final PutObjectOptions options = (nullableOptions == null) ? new PutObjectOptions()
               : nullableOptions;
      if (options.getAcl() != null)
         keyToAcl.put(bucketName + "/" + object.getKey(), options.getAcl());
      return super.putBlob(bucketName, object);
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

   public Future<AccessControlList> getContainerACL(final String bucket) {
      return new FutureBase<AccessControlList>() {
         public AccessControlList get() throws InterruptedException, ExecutionException {
            return getACLforS3Item(bucket);
         }
      };
   }

   public Future<AccessControlList> getBlobACL(final String bucket, final String objectKey) {
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

   public Future<Boolean> putContainerACL(final String bucket, final AccessControlList acl) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            keyToAcl.put(bucket, sanitizeUploadedACL(acl));
            return true;
         }
      };
   }

   public Future<Boolean> putBlobACL(final String bucket, final String objectKey,
            final AccessControlList acl) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            keyToAcl.put(bucket + "/" + objectKey, sanitizeUploadedACL(acl));
            return true;
         }
      };
   }

   public Future<ObjectMetadata> copyBlob(String sourceBucket, String sourceObject,
            String destinationBucket, String destinationObject) {
      return copyBlob(sourceBucket, sourceObject, destinationBucket, destinationObject,
               CopyObjectOptions.NONE);
   }

   public Future<Boolean> createContainer(String bucketName) {
      return createContainer(bucketName, PutBucketOptions.NONE);
   }

   @Override
   public Future<S3Object> getBlob(String bucketName, String key) {
      return getBlob(bucketName, key, GetOptions.NONE);
   }

   public Future<ListBucketResponse> listBlobs(String bucketName) {
      return listBlobs(bucketName, ListBucketOptions.NONE);
   }

   public Future<byte[]> putBlob(String bucketName, S3Object object) {
      return putBlob(bucketName, object, PutObjectOptions.NONE);
   }

}
