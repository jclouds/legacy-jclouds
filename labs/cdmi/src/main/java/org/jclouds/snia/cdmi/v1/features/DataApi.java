/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.snia.cdmi.v1.features;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.functions.MultipartMimePayload;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;

/**
 * Data Object Resource Operations
 * 
 * @see DataAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
public interface DataApi {
   /**
    * get CDMI Data object
    * 
    * @param dataObjectName
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject");
    * }
    * 
    *         <pre>
    */
   DataObject get(String dataObjectName);

   /**
    * get CDMI Data object
    * 
    * @param dataObjectName
    * @param queryParams
    *           enables getting only certain fields, metadata, value range
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject",ContainerQueryParams.Builder.field("parentURI").field("objectName"));
    *  dataObject = get("myDataObject",ContainerQueryParams.Builder.value(0,10));
    * }
    * 
    *         <pre>
    */
   DataObject get(String dataObjectName, DataObjectQueryParams queryParams);

   /**
    * Get DataObject using Multi-part MIME.
    * 
    * @param dataObjectName
    * @return
    */
   DataObject getMultipartMime(String dataObjectName);

   /**
    * create CDMI Data object
    * 
    * @param dataObjectName
    * @param options
    *           enables defining the body i.e. metadata, mimetype, value
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = create(
    *                                "myDataObject",
    *                                CreateDataObjectOptions.Builder
    *                                                    .value(value)
    *                                                    .mimetype("text/plain")
    *                                                    .metadata(pDataObjectMetaDataIn);
    * }
    * 
    *         <pre>
    */
   DataObject create(String dataObjectName, CreateDataObjectOptions... options);

   /**
    * Create CDMI data object using Multi-part MIME Multi-part MIME is the CDMI
    * mechanism to create an data object that contains binary data metadata in
    * one automic operation. The other way is to set the valuetransferencoding
    * to base64, but is results in a lot of extra overhead for both the client
    * and server.
    * 
    * @param dataObjectName
    * @param payload
    *           MultipartMimePayloadIn json metadata in first part and content
    *           in second part
    * @return DataObject without content
    */
   DataObject create(String dataObjectName, MultipartMimePayload payload);

   /**
    * Create CDDMI data object but return etag. Added to support Blobstore
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param options
    * @return etag
    */
   String createRtnEtag(String dataObjectName, CreateDataObjectOptions... options);

   /**
    * Update a Data Object
    * 
    * @param dataObjectName
    * @param queryParams
    *           enables updating only certain fields, metadata, value range
    * @param options
    *           enables defining the body i.e. metadata, mimetype, value
    */
   String update(String dataObjectName, DataObjectQueryParams queryParams, CreateDataObjectOptions... options);

   /**
    * delete CDMI Data object
    * 
    * @param dataObjectName
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  delete("myDataObject");
    * }
    * 
    *           <pre>
    */
   void delete(String dataObjectName);

}
