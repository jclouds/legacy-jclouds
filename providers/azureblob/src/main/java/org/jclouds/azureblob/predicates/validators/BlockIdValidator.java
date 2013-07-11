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
package org.jclouds.azureblob.predicates.validators;

import com.google.inject.Singleton;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.predicates.Validator;

/**
 * Validates Block IDs used in Put Block:
 *
 * "A valid Base64 string value that identifies the block. Prior to encoding, the string must
 * be less than or equal to 64 bytes in size. For a given blob, the length of the value
 * specified for the blockid parameter must be the same size for each block. Note that the
 * Base64 string must be URL-encoded."
 *
 * @see {http://msdn.microsoft.com/en-us/library/windowsazure/dd135726.aspx}
 */
@Singleton
public class BlockIdValidator extends Validator<String> {
   @Override
   public void validate(String s) throws IllegalArgumentException {
      if (s == null || s.length() > 64)
         throw new IllegalArgumentException("block id:" + s + "; Block Ids must be less than or equal to 64 bytes in size");

   }
}
