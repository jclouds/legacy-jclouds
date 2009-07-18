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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.classextension.EasyMock.createNiceMock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.XStream;

/**
 * Implementation of {@link S3Connection} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class StubS3Connection implements S3Connection {
   public static final String TEST_ACL_ID = "1a405254c932b52e5b5caaa88186bc431a1bacb9ece631f835daddaf0c47677c";
   public static final String TEST_ACL_EMAIL = "james@misterm.org";

   private static Map<String, Map<String, S3Object>> bucketToContents = new ConcurrentHashMap<String, Map<String, S3Object>>();
   private static Map<String, Metadata.LocationConstraint> bucketToLocation = new ConcurrentHashMap<String, Metadata.LocationConstraint>();

   /**
    * An S3 item's "ACL" may be a {@link CannedAccessPolicy} or an {@link AccessControlList}.
    */
   private static Map<String, Object> keyToAcl = new ConcurrentHashMap<String, Object>();

   public static final String DEFAULT_OWNER_ID = "abc123";

   /**
    * @throws java.io.IOException
    */
   public static byte[] toByteArray(Object data) throws IOException {
      checkNotNull(data, "data must be set before calling generateETag()");
      byte[] bytes = null;
      if (data == null || data instanceof byte[]) {
         bytes = (byte[]) data;
      } else if (data instanceof String) {
         bytes = ((String) data).getBytes();
      } else if (data instanceof File || data instanceof InputStream) {
         InputStream io = (data instanceof InputStream) ? (InputStream) data : new FileInputStream(
                  (File) data);
         bytes = IOUtils.toByteArray(io);
         IOUtils.closeQuietly(io);
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return bytes;

   }

   public Future<S3Object> getObject(final String s3Bucket, final String key) {
      return getObject(s3Bucket, key, new GetOptions());

   }

   public S3Object.Metadata copy(S3Object.Metadata in) {
      return (S3Object.Metadata) xstream.fromXML(xstream.toXML(in));
   }

   public S3Object.Metadata copy(S3Object.Metadata in, String newKey) {
      return (S3Object.Metadata) xstream.fromXML(xstream.toXML(in).replaceAll(in.getKey(), newKey));
   }

   public S3Object.Metadata headObject(final String s3Bucket, final String key) {
      if (!bucketToContents.containsKey(s3Bucket))
         return S3Object.Metadata.NOT_FOUND;
      Map<String, S3Object> realContents = bucketToContents.get(s3Bucket);
      if (!realContents.containsKey(key))
         return S3Object.Metadata.NOT_FOUND;
      return realContents.get(key).getMetadata();
   }

   public Future<Boolean> deleteObject(final String s3Bucket, final String key) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (bucketToContents.containsKey(s3Bucket)) {
               bucketToContents.get(s3Bucket).remove(key);
            }
            return true;
         }
      };
   }

   public Future<byte[]> putObject(final String s3Bucket, final S3Object object) {
      return putObject(s3Bucket, object, new PutObjectOptions());
   }

   public Future<Boolean> putBucketIfNotExists(final String s3Bucket) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (!bucketToContents.containsKey(s3Bucket)) {
               bucketToContents.put(s3Bucket, new ConcurrentHashMap<String, S3Object>());
            }
            return bucketToContents.containsKey(s3Bucket);
         }
      };
   }

   public boolean deleteBucketIfEmpty(final String s3Bucket) {
      if (bucketToContents.containsKey(s3Bucket)) {
         if (bucketToContents.get(s3Bucket).size() == 0)
            bucketToContents.remove(s3Bucket);
         else
            return false;
      }
      return true;
   }

   XStream xstream = new XStream();

   public Future<S3Object.Metadata> copyObject(final String sourceBucket,
            final String sourceObject, final String destinationBucket,
            final String destinationObject) {
      return copyObject(sourceBucket, sourceObject, destinationBucket, destinationObject,
               new CopyObjectOptions());
   }

   public boolean bucketExists(final String s3Bucket) {
      return bucketToContents.containsKey(s3Bucket);
   }

   public Future<S3Bucket> listBucket(final String s3Bucket) {
      return listBucket(s3Bucket, new ListBucketOptions());
   }

   private abstract class FutureBase<V> implements Future<V> {
      public boolean cancel(boolean b) {
         return false;
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean isDone() {
         return true;
      }

      public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
               TimeoutException {
         return get();
      }
   }

   public List<Metadata> listOwnedBuckets() {
      return Lists.newArrayList(Iterables.transform(bucketToContents.keySet(),
               new Function<String, Metadata>() {
                  public Metadata apply(String name) {
                     return new S3Bucket.Metadata(name);
                  }
               }));
   }

   public Future<Boolean> putBucketIfNotExists(String name, PutBucketOptions options) {
      if (options.getLocationConstraint() != null)
         bucketToLocation.put(name, options.getLocationConstraint());
      keyToAcl.put(name, options.getAcl());
      return putBucketIfNotExists(name);
   }

   class DelimiterFilter implements Predicate<S3Object.Metadata> {
      private final String prefix;
      private final String delimiter;

      DelimiterFilter(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public boolean apply(S3Object.Metadata metadata) {
         if (prefix == null)
            return metadata.getKey().indexOf(delimiter) == -1;
         if (metadata.getKey().startsWith(prefix))
            return metadata.getKey().replaceFirst(prefix, "").indexOf(delimiter) == -1;
         return false;
      }
   }

   class CommonPrefixes implements Function<S3Object.Metadata, String> {
      private final String prefix;
      private final String delimiter;
      static final String NO_PREFIX = "NO_PREFIX";

      CommonPrefixes(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public String apply(S3Object.Metadata metadata) {
         String working = metadata.getKey();

         if (prefix != null) {
            if (working.startsWith(prefix)) {
               working = working.replaceFirst(prefix, "");
            }
         }
         if (working.contains(delimiter)) {
            return working.substring(0, working.indexOf(delimiter));
         }
         return NO_PREFIX;
      }
   }

   public String getFirstQueryOrNull(String string, HttpRequestOptions options) {
      Collection<String> values = options.buildQueryParameters().get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   public Future<S3Bucket> listBucket(final String name, final ListBucketOptions options) {
      return new FutureBase<S3Bucket>() {
         public S3Bucket get() throws InterruptedException, ExecutionException {
            final Map<String, S3Object> realContents = bucketToContents.get(name);

            if (realContents == null)
               return S3Bucket.NOT_FOUND;
            SortedSet<S3Object.Metadata> contents = Sets.newTreeSet(Iterables.transform(
                     realContents.keySet(), new Function<String, S3Object.Metadata>() {
                        public S3Object.Metadata apply(String key) {
                           return realContents.get(key).getMetadata();
                        }
                     }));
            S3Bucket returnVal = new S3Bucket(name);

            final String marker = getFirstQueryOrNull(S3Constants.MARKER, options);
            if (marker != null) {
               S3Object.Metadata lastMarkerMetadata = Iterables.find(contents,
                        new Predicate<S3Object.Metadata>() {
                           public boolean apply(S3Object.Metadata metadata) {
                              return metadata.getKey().equals(marker);
                           }
                        });
               contents = contents.tailSet(lastMarkerMetadata);
               // amazon spec means after the marker, not including it.
               contents.remove(lastMarkerMetadata);
               returnVal.setMarker(marker);
            }
            final String prefix = getFirstQueryOrNull(S3Constants.PREFIX, options);
            if (prefix != null) {
               contents = Sets.newTreeSet(Iterables.filter(contents,
                        new Predicate<S3Object.Metadata>() {
                           public boolean apply(S3Object.Metadata o) {
                              return (o != null && o.getKey().startsWith(prefix));
                           }
                        }));
               returnVal.setPrefix(prefix);
            }

            final String delimiter = getFirstQueryOrNull(S3Constants.DELIMITER, options);
            if (delimiter != null) {
               Iterable<String> iterable = Iterables.transform(contents, new CommonPrefixes(
                        prefix != null ? prefix : null, delimiter));
               SortedSet<String> commonPrefixes = iterable != null ? Sets.newTreeSet(iterable)
                        : new TreeSet<String>();
               commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

               contents = Sets.newTreeSet(Iterables.filter(contents, new DelimiterFilter(
                        prefix != null ? prefix : null, delimiter)));

               returnVal.setCommonPrefixes(commonPrefixes);
               returnVal.setDelimiter(delimiter);
            }

            final String maxKeysString = getFirstQueryOrNull(S3Constants.MAX_KEYS, options);
            if (maxKeysString != null) {
               int maxKeys = Integer.parseInt(maxKeysString);
               SortedSet<S3Object.Metadata> contentsSlice = firstSliceOfSize(contents, maxKeys);
               returnVal.setMaxKeys(maxKeys);
               if (!contentsSlice.contains(contents.last())) {
                  // Partial listing
                  returnVal.setTruncated(true);
                  returnVal.setMarker(contentsSlice.last().getKey());
               } else {
                  returnVal.setTruncated(false);
                  returnVal.setMarker(null);
               }
               contents = contentsSlice;
            }

            returnVal.setContents(contents);
            return returnVal;
         }
      };
   }

   public static <T extends Comparable<?>> SortedSet<T> firstSliceOfSize(Iterable<T> elements,
            int size) {
      List<List<T>> slices = Lists.partition(Lists.newArrayList(elements), size);
      return Sets.newTreeSet(slices.get(0));
   }

   public Future<org.jclouds.aws.s3.domain.S3Object.Metadata> copyObject(final String sourceBucket,
            final String sourceObject, final String destinationBucket,
            final String destinationObject, final CopyObjectOptions options) {

      return new FutureBase<S3Object.Metadata>() {
         public S3Object.Metadata get() throws InterruptedException, ExecutionException {
            Map<String, S3Object> source = bucketToContents.get(sourceBucket);
            Map<String, S3Object> dest = bucketToContents.get(destinationBucket);
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
                     throw new ExecutionException(new RuntimeException("after"));

               }
               if (options.getIfUnmodifiedSince() != null) {
                  DateTime unmodifiedSince = dateService.rfc822DateParse(options
                           .getIfUnmodifiedSince());
                  if (unmodifiedSince.isAfter(object.getMetadata().getLastModified()))
                     throw new ExecutionException(new RuntimeException("after"));
               }
               S3Object sourceS3 = source.get(sourceObject);
               S3Object.Metadata newMd = copy(sourceS3.getMetadata(), destinationObject);
               if (options.getAcl() != null)
                  keyToAcl.put(destinationBucket + "/" + destinationObject, options.getAcl());
               if (options.getMetadata() != null) {
                  newMd.setUserMetadata(options.getMetadata());
               }
               newMd.setLastModified(new DateTime());
               dest.put(destinationObject, new S3Object(newMd, sourceS3.getData()));
               return copy(newMd);
            }
            return S3Object.Metadata.NOT_FOUND;
         }
      };
   }

   private void throwResponseException(int code) throws ExecutionException {
      HttpResponse response = null;
      try {
         response = new HttpResponse(new URL("file:///unused")); // TODO: Get real object URL?
      } catch (MalformedURLException e) {
         // This shouldn't ever happen.
         e.printStackTrace();
         assert false;
      }
      response.setStatusCode(code);
      throw new ExecutionException(new HttpResponseException(createNiceMock(HttpCommand.class),
               response));
   }

   public Future<byte[]> putObject(final String bucketName, final S3Object object,
            final PutObjectOptions options) {
      if (!bucketToContents.containsKey(bucketName)) {
         new RuntimeException("bucketName not found: " + bucketName);
      }
      try {
         S3Object.Metadata newMd = copy(object.getMetadata());
         newMd.setLastModified(new DateTime());
         byte[] data = toByteArray(object.getData());
         final byte[] eTag = HttpUtils.eTag(data);
         newMd.setETag(eTag);
         newMd.setContentType(object.getMetadata().getContentType());
         if (options.getAcl() != null)
            keyToAcl.put(bucketName + "/" + object.getKey(), options.getAcl());
         bucketToContents.get(bucketName).put(object.getKey(), new S3Object(newMd, data));

         // Set HTTP headers to match metadata
         newMd.getAllHeaders().put(HttpHeaders.LAST_MODIFIED,
                  dateService.rfc822DateFormat(newMd.getLastModified()));
         newMd.getAllHeaders().put(HttpHeaders.ETAG, HttpUtils.toHexString(eTag));
         newMd.getAllHeaders().put(HttpHeaders.CONTENT_TYPE, newMd.getContentType());
         newMd.getAllHeaders().put(HttpHeaders.CONTENT_LENGTH, newMd.getSize() + "");
         for (Entry<String, String> userMD : newMd.getUserMetadata().entries()) {
            newMd.getAllHeaders().put(userMD.getKey(), userMD.getValue());
         }

         return new FutureBase<byte[]>() {
            public byte[] get() throws InterruptedException, ExecutionException {
               return eTag;
            }
         };
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   DateService dateService = new DateService();

   public Future<S3Object> getObject(final String bucketName, final String key,
            final GetOptions options) {
      return new FutureBase<S3Object>() {
         public S3Object get() throws InterruptedException, ExecutionException {
            if (!bucketToContents.containsKey(bucketName))
               return S3Object.NOT_FOUND;
            Map<String, S3Object> realContents = bucketToContents.get(bucketName);
            if (!realContents.containsKey(key))
               return S3Object.NOT_FOUND;

            S3Object object = realContents.get(key);

            if (options.getIfMatch() != null) {
               if (!Arrays.equals(object.getMetadata().getETag(), HttpUtils.fromHexString(options
                        .getIfMatch().replaceAll("\"", ""))))
                  throwResponseException(412);
            }
            if (options.getIfNoneMatch() != null) {
               if (Arrays.equals(object.getMetadata().getETag(), HttpUtils.fromHexString(options
                        .getIfNoneMatch().replaceAll("\"", ""))))
                  throwResponseException(304);
            }
            if (options.getIfModifiedSince() != null) {
               DateTime modifiedSince = dateService.rfc822DateParse(options.getIfModifiedSince());
               if (object.getMetadata().getLastModified().isBefore(modifiedSince))
                  throw new ExecutionException(new RuntimeException(String.format(
                           "%1$s is before %2$s", object.getMetadata().getLastModified(),
                           modifiedSince)));

            }
            if (options.getIfUnmodifiedSince() != null) {
               DateTime unmodifiedSince = dateService.rfc822DateParse(options
                        .getIfUnmodifiedSince());
               if (object.getMetadata().getLastModified().isAfter(unmodifiedSince))
                  throw new ExecutionException(new RuntimeException(String.format(
                           "%1$s is after %2$s", object.getMetadata().getLastModified(),
                           unmodifiedSince)));
            }
            S3Object returnVal = new S3Object(copy(object.getMetadata()), object.getData());
            if (options.getRange() != null) {
               byte[] data = (byte[]) returnVal.getData();
               ByteArrayOutputStream out = new ByteArrayOutputStream();
               for (String s : options.getRange().replaceAll("bytes=", "").split(",")) {
                  if (s.startsWith("-")) {
                     int length = Integer.parseInt(s.replaceAll("\\-", ""));
                     out.write(data, data.length - length, length);
                  } else if (s.endsWith("-")) {
                     int offset = Integer.parseInt(s.replaceAll("\\-", ""));
                     out.write(data, offset, data.length - offset);
                  } else if (s.contains("-")) {
                     String[] firstLast = s.split("\\-");
                     int offset = Integer.parseInt(firstLast[0]);
                     int last = Integer.parseInt(firstLast[1]);
                     int length = (last < data.length) ? last + 1 : data.length - offset;

                     out.write(data, offset, length);
                  } else {
                     throw new IllegalArgumentException("first and last were null!");
                  }

               }
               returnVal.setData(out.toByteArray());
               returnVal.setContentLength(out.size());
               returnVal.getMetadata().setSize(data.length);
            }
            returnVal.setData(new ByteArrayInputStream((byte[]) returnVal.getData()));
            return returnVal;
         }
      };
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

}
