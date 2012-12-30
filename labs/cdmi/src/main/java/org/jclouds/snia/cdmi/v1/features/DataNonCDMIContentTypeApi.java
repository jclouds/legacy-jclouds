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

import org.jclouds.io.Payload;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;

/**
 * Data Object Resource Operations
 * 
 * @see DataNonCDMIContentTypeAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
public interface DataNonCDMIContentTypeApi {
   /**
    * get CDMI Data object
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @return DataObjectNonCDMIContentType
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject");
    *  dataObject = get("parentContainer/childContainer/","myDataObject");
    * }
    * 
    * <pre>
    * @see DataNonCDMIContentTypeAsyncApi#getValue(String dataObjectName)
    */
   Payload getValue(String dataObjectName);

   /**
    * get CDMI Data object
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param range
    *           a valid ranges-specifier (see RFC2616 Section 14.35.1)
    * @return DataObjectNonCDMIContentType
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject","bytes=0-10");
    * }
    * 
    *         <pre>
    */
   Payload getValue(String dataObjectName, String range);

   /**
    * get CDMI Data object
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
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
    * create CDMI Data object Non CDMI Content Type
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param payload
    *           enables defining the body's payload i.e. file, inputStream, String, ByteArray
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  create("myDataObject",new StringPayload("value");
    *  create("myDataObject",new ByteArrayPayload(bytes);
    *  create("myDataObject",new FilePayload(myFileIn);
    *  create("myDataObject",new InputStreamPayload(is);
    *  
    *  File f = new File("yellow-flowers.jpg");
    *  payloadIn = new InputStreamPayload(new FileInputStream(f));
    *  payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(
    *            payloadIn.getContentMetadata().toBuilder()
    *            .contentType(MediaType.JPEG.toString())
    *            .contentLength(new Long(inFile.length()))
    *            .build()));
    *  dataNonCDMIContentTypeApi.create(containerName, f.getName(),
    * 					payloadIn);
    * }
    * 
    *           <pre>
    */
   void create(String dataObjectName, Payload payload);

   /**
    * create CDMI Data object partial Non CDMI Content Type Only part of the object is contained in
    * the payload and the X-CDMI-Partial header flag is set to true
    * 
    * 
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param payload
    *           enables defining the body's payload i.e. file, inputStream, String, ByteArray
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  createPartial("myDataObject",new StringPayload("value");
    *  createPartial("myDataObject",new ByteArrayPayload(bytes);
    *  createPartial("myDataObject",new FilePayload(myFileIn);
    *  createPartial("myDataObject",new InputStreamPayload(is);
    * }
    * 
    *           <pre>
    */
   void createPartial(String dataObjectName, Payload payload);

   /**
    * create CDMI Data object Non CDMI Content Type
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param inputString
    *           simple string input
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  create("myDataObject",new String("value");
    * }
    * 
    *           <pre>
    */
   void create(String dataObjectName, String inputString);

   /**
    * delete CDMI Data object
    * 
    * @param containerName
    *           containerName must end with a forward slash, /.
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
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
