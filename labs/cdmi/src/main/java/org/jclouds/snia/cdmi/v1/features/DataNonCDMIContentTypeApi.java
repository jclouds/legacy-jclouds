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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
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
@Timeout(duration = 600, timeUnit = TimeUnit.SECONDS)
public interface DataNonCDMIContentTypeApi {
   /**
    * get CDMI Data object's payload
    * 
    * @param dataObject
    * @return payload
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  payload = getPayload("mycontainer/myDataObject");
    *  payload = getPayload("parentContainer/childContainer/myDataObject");
    * }
    * 
    * <pre>
    * @see DataNonCDMIContentTypeAsyncApi#getPayload(String dataObjectName)
    */
   Payload getPayload(String dataObject);

   /**
    * get CDMI Data object's payload
    * 
    * @param dataObject
    * @param range
    *           a valid ranges-specifier (see RFC2616 Section 14.35.1)
    * @return payload
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  payload = get("mycontainer/myDataObject","bytes=0-10");
    * }
    * 
    *         <pre>
    */
   Payload getPayload(String dataObject, String range);

   /**
    * get CDMI Data object
    * 
    * @param dataObject
    * @param queryParams
    *           enables specifying additional information when retrieving payload
    * @return payload
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("mycontainer/myDataObject","bytes=0-10");
    * }
    * 
    *         <pre>
    */
   Payload getPayload(String dataObject, DataObjectQueryParams queryParams);

   /**
    * get CDMI Data object's payload constrained by byte range
    * 
    * @param dataObject
    * @param range
    *           a valid ranges-specifier (see RFC2616 Section 14.35.1)
    * @param queryParams
    *           enables getting only certain fields, metadata, value range, any
    * @return Payload
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("mycontainer/myDataObject","bytes=0-10");
    * }
    * 
    *         <pre>
    */
   Payload getPayload(String dataObject, String range, DataObjectQueryParams queryParams);

   /**
    * get CDMI Data object
    * 
    * @param dataObject
    * @param queryParams
    *           enables getting only certain fields, metadata, value range, any
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("mycontainer/myDataObject",ContainerQueryParams.Builder.field("parentURI").field("objectName"));
    *  dataObject = get("mycontainer/myDataObject",ContainerQueryParams.Builder.any("myquery=anyQueryParam"));
    * }
    * 
    *         <pre>
    */
   DataObject get(String dataObject, DataObjectQueryParams queryParams);

   /**
    * create CDMI Data object Non CDMI Content Type
    * 
    * @param dataObject
    * @param payload
    *           enables defining the body's payload i.e. file, inputStream, String, ByteArray
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  create("mycontainer/myDataObject",Payloads.newPayload(X);
    *  
    *  File f = new File("yellow-flowers.jpg");
    *  payloadIn = new InputStreamPayload(new FileInputStream(f));
    *  payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(
    *            payloadIn.getContentMetadata().toBuilder()
    *            .contentType(MediaType.JPEG.toString())
    *            .contentLength(new Long(inFile.length()))
    *            .build()));
    *  dataNonCDMIContentTypeApi.create(containerName+ f.getName(),
    * 					payloadIn);
    * }
    * 
    *           <pre>
    */
   void create(String dataObject, Payload payload);

   /**
    * create CDMI Data object partial Non CDMI Content Type Only part of the object is contained in the payload and the
    * X-CDMI-Partial header flag is set to true
    * 
    * 
    * 
    * @param dataObject
    *           dataObject must not end with a forward slash, /.
    * @param payload
    *           enables defining the body's payload i.e. file, inputStream, String, ByteArray
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  createPartial("mycontainer/myDataObject",Payloads.newPayload(X);
    * }
    * 
    *           <pre>
    */
   void createPartial(String dataObject, Payload payload);

   /**
    * create CDMI Data object Non CDMI Content Type
    * 
    * @param dataObject
    * @param inputString
    *           simple string input
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  create("mycontainer/myDataObject",new String("value");
    * }
    * 
    *           <pre>
    */
   void create(String dataObject, String inputString);

   /**
    * delete CDMI Data object
    * 
    * @param dataObject
    *           <pre>
    *  Examples: 
    *  {@code
    *  delete("mycontainer/myDataObject");
    * }
    * 
    *           <pre>
    */
   void delete(String dataObject);

}
