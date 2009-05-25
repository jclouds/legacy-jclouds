/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.classextension.EasyMock.createNiceMock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.aws.s3.commands.CopyObject;
import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.aws.util.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.XStream;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class StubS3Connection implements S3Connection {
   private static Map<String, Map<String, S3Object>> bucketToContents = new ConcurrentHashMap<String, Map<String, S3Object>>();
   private static Map<String, Metadata.LocationConstraint> bucketToLocation = new ConcurrentHashMap<String, Metadata.LocationConstraint>();
   private static Map<String, CannedAccessPolicy> keyToAcl = new ConcurrentHashMap<String, CannedAccessPolicy>();

   /**
    * @throws java.io.IOException
    */
   public static byte[] toByteArray(Object data) throws IOException {
      checkNotNull(data, "data must be set before calling generateMd5()");
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
      return getObject(s3Bucket, key, new GetObjectOptions());

   }

   public S3Object.Metadata copy(S3Object.Metadata in) {
      return (S3Object.Metadata) xstream.fromXML(xstream.toXML(in));
   }

   public S3Object.Metadata copy(S3Object.Metadata in, String newKey) {
      return (S3Object.Metadata) xstream.fromXML(xstream.toXML(in).replaceAll(in.getKey(), newKey));
   }

   public Future<S3Object.Metadata> headObject(final String s3Bucket, final String key) {
      return new FutureBase<S3Object.Metadata>() {
         public S3Object.Metadata get() throws InterruptedException, ExecutionException {
            if (!bucketToContents.containsKey(s3Bucket))
               return S3Object.Metadata.NOT_FOUND;
            Map<String, S3Object> realContents = bucketToContents.get(s3Bucket);
            if (!realContents.containsKey(key))
               return S3Object.Metadata.NOT_FOUND;
            return realContents.get(key).getMetadata();
         }
      };
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

   public Future<Boolean> deleteBucketIfEmpty(final String s3Bucket) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (bucketToContents.containsKey(s3Bucket)) {
               if (bucketToContents.get(s3Bucket).size() == 0)
                  bucketToContents.remove(s3Bucket);
               else
                  return false;
            }
            return true;
         }
      };
   }

   XStream xstream = new XStream();

   public Future<S3Object.Metadata> copyObject(final String sourceBucket,
            final String sourceObject, final String destinationBucket,
            final String destinationObject) {
      return copyObject(sourceBucket, sourceObject, destinationBucket, destinationObject,
               new CopyObjectOptions());
   }

   public Future<Boolean> bucketExists(final String s3Bucket) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            return bucketToContents.containsKey(s3Bucket);
         }
      };
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

   public Future<List<Metadata>> listOwnedBuckets() {
      return new FutureBase<List<S3Bucket.Metadata>>() {
         public List<S3Bucket.Metadata> get() throws InterruptedException, ExecutionException {
            return Lists.newArrayList(Iterables.transform(bucketToContents.keySet(),
                     new Function<String, Metadata>() {
                        public Metadata apply(String name) {
                           return new S3Bucket.Metadata(name);
                        }
                     }));
         }
      };
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

            if (options.getMarker() != null) {
               final String marker;
               try {
                  marker = URLDecoder.decode(options.getMarker(), "UTF-8");
               } catch (UnsupportedEncodingException e) {
                  throw new IllegalArgumentException(e);
               }
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
            try {

               if (options.getPrefix() != null) {
                  contents = Sets.newTreeSet(Iterables.filter(contents,
                           new Predicate<S3Object.Metadata>() {
                              public boolean apply(S3Object.Metadata o) {
                                 try {
                                    return (o != null && o.getKey().startsWith(
                                             URLDecoder.decode(options.getPrefix(), "UTF-8")));
                                 } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                 }
                              }
                           }));
                  returnVal.setPrefix(URLDecoder.decode(options.getPrefix(), "UTF-8"));
               }

               if (options.getDelimiter() != null) {
                  Iterable<String> iterable = Iterables.transform(contents, new CommonPrefixes(
                           options.getPrefix() != null ? URLDecoder.decode(options.getPrefix(),
                                    "UTF-8") : null, URLDecoder.decode(options.getDelimiter(),
                                    "UTF-8")));
                  Set<String> commonPrefixes = iterable != null ? Sets.newTreeSet(iterable)
                           : new HashSet<String>();
                  commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

                  contents = Sets.newTreeSet(Iterables.filter(contents, new DelimiterFilter(options
                           .getPrefix() != null ? URLDecoder.decode(options.getPrefix(), "UTF-8")
                           : null, URLDecoder.decode(options.getDelimiter(), "UTF-8"))));

                  returnVal.setCommonPrefixes(commonPrefixes);
                  returnVal.setDelimiter(URLDecoder.decode(options.getDelimiter(), "UTF-8"));
               }
            } catch (UnsupportedEncodingException e) {
               throw new RuntimeException(e);
            }

            if (options.getMaxKeys() != null) {
               contents = firstSliceOfSize(contents, Integer.parseInt(options.getMaxKeys()));
               returnVal.setMaxKeys(Integer.parseInt(options.getMaxKeys()));
               returnVal.setTruncated(true);
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
                  if (!Arrays.equals(object.getMetadata().getMd5(), S3Utils.fromHexString(options
                           .getIfMatch().replaceAll("\"", ""))))
                     throwResponseException(412);

               }
               if (options.getIfNoneMatch() != null) {
                  if (Arrays.equals(object.getMetadata().getMd5(), S3Utils.fromHexString(options
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
                  keyToAcl.put(destinationBucket + destinationObject, options.getAcl());
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
      HttpResponse response = new HttpResponse();
      response.setStatusCode(code);
      throw new ExecutionException(new HttpResponseException(createNiceMock(CopyObject.class),
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
         final byte[] md5 = S3Utils.md5(data);
         newMd.setMd5(md5);
         newMd.setContentType(object.getMetadata().getContentType());
         if (options.getAcl() != null)
            keyToAcl.put(bucketName + object, options.getAcl());
         bucketToContents.get(bucketName).put(object.getKey(), new S3Object(newMd, data));
         return new FutureBase<byte[]>() {
            public byte[] get() throws InterruptedException, ExecutionException {
               return md5;
            }
         };
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   DateService dateService = new DateService();

   public Future<S3Object> getObject(final String bucketName, final String key,
            final GetObjectOptions options) {
      return new FutureBase<S3Object>() {
         public S3Object get() throws InterruptedException, ExecutionException {
            if (!bucketToContents.containsKey(bucketName))
               return S3Object.NOT_FOUND;
            Map<String, S3Object> realContents = bucketToContents.get(bucketName);
            if (!realContents.containsKey(key))
               return S3Object.NOT_FOUND;

            S3Object object = realContents.get(key);

            if (options.getIfMatch() != null) {
               if (!Arrays.equals(object.getMetadata().getMd5(), S3Utils.fromHexString(options
                        .getIfMatch().replaceAll("\"", ""))))
                  throwResponseException(412);
            }
            if (options.getIfNoneMatch() != null) {
               if (Arrays.equals(object.getMetadata().getMd5(), S3Utils.fromHexString(options
                        .getIfNoneMatch().replaceAll("\"", ""))))
                  throwResponseException(304);
            }
            if (options.getIfModifiedSince() != null) {
               DateTime modifiedSince = dateService.rfc822DateParse(options.getIfModifiedSince());
               if (modifiedSince.isAfter(object.getMetadata().getLastModified()))
                  throw new ExecutionException(new RuntimeException("after"));

            }
            if (options.getIfUnmodifiedSince() != null) {
               DateTime unmodifiedSince = dateService.rfc822DateParse(options
                        .getIfUnmodifiedSince());
               if (unmodifiedSince.isAfter(object.getMetadata().getLastModified()))
                  throw new ExecutionException(new RuntimeException("after"));
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

}
