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

package org.jclouds.elastichosts.options;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Contains options supported for read drive operations. <h2>
 * Usage</h2> The recommended way to instantiate a ReadDriveOptions object is to statically import
 * ReadDriveOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.elastichosts.options.ReadDriveOptions.Builder.*;
 * 
 * 
 * // this will get the first 1024 bytes starting at offset 2048
 * Payload payload = client.readDrive("drive-uuid",offset(2048l).size(1024l));
 * <code>
 * 
 * @author Adrian Cole
 * 
 */
public class ReadDriveOptions {

   private Long offset;
   private Long size;

   /**
    * start at the specified offset in bytes
    */
   public ReadDriveOptions offset(long offset) {
      checkArgument(offset >= 0, "start must be >= 0");
      this.offset = offset;
      return this;
   }

   /**
    * download the specified size in bytes
    */
   public ReadDriveOptions size(long size) {
      checkArgument(size >= 0, "start must be >= 0");
      this.size = size;
      return this;
   }

   public static class Builder {

      /**
       * @see ReadDriveOptions#offset
       */
      public static ReadDriveOptions offset(long offset) {
         ReadDriveOptions options = new ReadDriveOptions();
         return options.offset(offset);
      }

      /**
       * @see ReadDriveOptions#size
       */
      public static ReadDriveOptions size(long size) {
         ReadDriveOptions options = new ReadDriveOptions();
         return options.size(size);
      }

   }

   public Long getOffset() {
      return offset;
   }

   public Long getSize() {
      return size;
   }
}
