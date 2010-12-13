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

package org.jclouds.elasticstack;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.options.ReadDriveOptions;
import org.jclouds.io.Payload;

/**
 * Provides synchronous access to elasticstack.
 * <p/>
 * 
 * @see ElasticStackAsyncClient
 * @see <a href="TODO: insert URL of elasticstack documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface ElasticStackClient extends CommonElasticStackClient {

   /**
    * Image a drive from another drive. The actual imaging process is asynchronous, with progress
    * reported via drive info.
    * 
    * @param source
    *           drive to copy from
    * @param destination
    *           drive to copy to
    */
   void imageDrive(String source, String destination);

   /**
    * @see #imageDrive(String, String)
    * @param conversionType
    *           Supports 'gzip' or 'gunzip' conversions.
    */
   void imageDrive(String source, String destination, ImageConversionType conversionType);

   /**
    * Read binary data from a drive
    * 
    * @param uuid
    *           drive to read
    * @return binary content of the drive.
    */
   Payload readDrive(String uuid);

   /**
    * @see #readDrive(String)
    * @param options
    *           controls offset and size of the request
    */
   Payload readDrive(String uuid, ReadDriveOptions options);

   /**
    * Write binary data to a drive
    * 
    * @param uuid
    *           drive to write
    * @param content
    *           what to write.
    *           <ul>
    *           <li>Binary data (Content-Type: application/octet-stream)</li>
    *           <li>Supports raw data or Content-Encoding: gzip</li>
    *           <li>Does not support Transfer-Encoding: chunked</li>
    *           </ul>
    */
   void writeDrive(String uuid, Payload content);

   /**
    * @see ElasticStackClient#writeDrive(String, Payload)
    * @param offset
    *           the byte offset in the target drive at which to start writing, not an offset in the
    *           input stream.
    */
   void writeDrive(String uuid, Payload content, long offset);

}
