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
package org.jclouds.aws.s3;

import java.util.Map;
import org.jclouds.aws.s3.domain.DeleteResult;
import org.jclouds.io.Payload;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.options.PutObjectOptions;

/**
 * Provides access to amazon-specific S3 features
 *
 * @author Adrian Cole
 * @see AWSS3AsyncClient
 */
public interface AWSS3Client extends S3Client {

   /**
    * This operation initiates a multipart upload and returns an upload ID. This upload ID is used
    * to associate all the parts in the specific multipart upload. You specify this upload ID in
    * each of your subsequent upload part requests (see Upload Part). You also include this upload
    * ID in the final request to either complete or abort the multipart upload request.
    *
    * <h4>Note</h4> If you create an object using the multipart upload APIs, currently you cannot
    * copy the object between regions.
    *
    *
    * @param bucketName
    *           namespace of the object you are to upload
    * @param objectMetadata
    *           metadata around the object you wish to upload
    * @param options
    *           controls optional parameters such as canned ACL
    * @return ID for the initiated multipart upload.
    */
   String initiateMultipartUpload(String bucketName, ObjectMetadata objectMetadata, PutObjectOptions... options);


   /**
    * This operation aborts a multipart upload. After a multipart upload is aborted, no additional
    * parts can be uploaded using that upload ID. The storage consumed by any previously uploaded
    * parts will be freed. However, if any part uploads are currently in progress, those part
    * uploads might or might not succeed. As a result, it might be necessary to abort a given
    * multipart upload multiple times in order to completely free all storage consumed by all parts.
    *
    *
    * @param bucketName
    *           namespace of the object you are deleting
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @param uploadId
    *           id of the multipart upload in progress.
    */
   void abortMultipartUpload(String bucketName, String key, String uploadId);

   /**
    * This operation uploads a part in a multipart upload. You must initiate a multipart upload (see
    * Initiate Multipart Upload) before you can upload any part. In response to your initiate
    * request. Amazon S3 returns an upload ID, a unique identifier, that you must include in your
    * upload part request.
    *
    * <p/>
    * Part numbers can be any number from 1 to 10,000, inclusive. A part number uniquely identifies
    * a part and also defines its position within the object being created. If you upload a new part
    * using the same part number that was used with a previous part, the previously uploaded part is
    * overwritten. Each part must be at least 5 MB in size, except the last part. There is no size
    * limit on the last part of your multipart upload.
    *
    * <p/>
    * To ensure that data is not corrupted when traversing the network, specify the Content-MD5
    * header in the upload part request. Amazon S3 checks the part data against the provided MD5
    * value. If they do not match, Amazon S3 returns an error.
    *
    *
    * @param bucketName
    *           namespace of the object you are storing
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @param partNumber
    *           which part is this.
    * @param uploadId
    *           id of the multipart upload in progress.
    * @param part
    *           contains the data to create or overwrite
    * @return ETag of the content uploaded
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/API/mpUploadUploadPart.html"
    *      />
    */
   String uploadPart(String bucketName, String key, int partNumber, String uploadId, Payload part);

   /**
    *
    This operation completes a multipart upload by assembling previously uploaded parts.
    * <p/>
    * You first initiate the multipart upload and then upload all parts using the Upload Parts
    * operation (see Upload Part). After successfully uploading all relevant parts of an upload, you
    * call this operation to complete the upload. Upon receiving this request, Amazon S3
    * concatenates all the parts in ascending order by part number to create a new object. In the
    * Complete Multipart Upload request, you must provide the parts list. For each part in the list,
    * you must provide the part number and the ETag header value, returned after that part was
    * uploaded.
    * <p/>
    * Processing of a Complete Multipart Upload request could take several minutes to complete.
    * After Amazon S3 begins processing the request, it sends an HTTP response header that specifies
    * a 200 OK response. While processing is in progress, Amazon S3 periodically sends whitespace
    * characters to keep the connection from timing out. Because a request could fail after the
    * initial 200 OK response has been sent, it is important that you check the response body to
    * determine whether the request succeeded.
    * <p/>
    * Note that if Complete Multipart Upload fails, applications should be prepared to retry the
    * failed requests.
    *
    * @param bucketName
    *           namespace of the object you are deleting
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @param uploadId
    *           id of the multipart upload in progress.
    * @param parts
    *           a map of part id to eTag from the {@link #uploadPart} command.
    * @return ETag of the content uploaded
    */
   String completeMultipartUpload(String bucketName, String key, String uploadId, Map<Integer, String> parts);

   /**
    * The Multi-Object Delete operation enables you to delete multiple objects from a bucket using a 
    * single HTTP request. If you know the object keys that you want to delete, then this operation 
    * provides a suitable alternative to sending individual delete requests (see DELETE Object), 
    * reducing per-request overhead.
    * 
    * The Multi-Object Delete request contains a set of up to 1000 keys that you want to delete.
    * 
    * If a key does not exist is considered to be deleted. 
    * 
    * The Multi-Object Delete operation supports two modes for the response; verbose and quiet.
    * By default, the operation uses verbose mode in which the response includes the result of
    * deletion of each key in your request.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/API/multiobjectdeleteapi.html" />
    * @param bucketName
    *           namespace of the objects you are deleting
    * @param keys
    *           set of unique keys identifying objects
    */
   DeleteResult deleteObjects(String bucketName, Iterable<String> keys);
}
