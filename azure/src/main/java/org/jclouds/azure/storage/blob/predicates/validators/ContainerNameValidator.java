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

package org.jclouds.azure.storage.blob.predicates.validators;

import com.google.common.base.CharMatcher;
import org.jclouds.predicates.Validator;
import javax.annotation.Nullable;
import static com.google.common.base.CharMatcher.*;

/**
* Validates name for Azure container.
* The complete requirements are listed at:
* http://weblogs.asp.net/vblasberg/archive/2009/02/17/azure-details-and-limitations-blobs-tables-and-queues.aspx
*
* @see org.jclouds.rest.InputParamValidator
* @see org.jclouds.predicates.Validator
*
* @author Oleksiy Yarmula
*/
public class ContainerNameValidator extends Validator<String> {

   public void validate(@Nullable String containerName) {

       if(containerName == null || containerName.length() < 3 || containerName.length() > 63) throw exception(containerName, "Can't be null or empty. Length must be 3 to 63 symbols.");
       if(CharMatcher.JAVA_LETTER_OR_DIGIT.indexIn(containerName) != 0) throw exception(containerName, "Should start with letter/number");
       if(!containerName.toLowerCase().equals(containerName)) throw exception(containerName, "Should be only lowercase");

       /* The name must be a valid DNS name. From wikipedia:
       "The characters allowed in a label are a subset of the ASCII character set, a
       and includes the characters a through z, A through Z, digits 0 through 9".
       From Azure:
       Every Dash (-) Must Be Immediately Preceded and Followed by a Letter or Number.
       */
       CharMatcher lettersNumbersOrDashes = inRange('a', 'z').or(inRange('0', '9').or(is('-')));
       if(! lettersNumbersOrDashes.matchesAllOf(containerName)) throw exception(containerName, "Should have lowercase ASCII letters, " +
               "numbers, or dashes");
       if(containerName.contains("--")) throw exception(containerName, "Every dash must be followed by letter or number");
       if(containerName.endsWith("-")) throw exception(containerName, "Shouldn't end with a dash");
   }

   private IllegalArgumentException exception(String containerName, String reason) {
       return new IllegalArgumentException(String.format("Object '%s' doesn't match Azure container naming convention. " +
               "Reason: %s. For more info, please refer to http://weblogs.asp.net/vblasberg/archive/2009/02/17/" +
               "azure-details-and-limitations-blobs-tables-and-queues.aspx.", containerName, reason));
   } 

}