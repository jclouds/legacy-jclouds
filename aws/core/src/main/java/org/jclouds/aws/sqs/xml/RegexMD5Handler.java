/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.sqs.xml;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.Singleton;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QuerySendMessage.html"
 *      />
 * @author Adrian Cole
 */
@Singleton
public class RegexMD5Handler implements Function<HttpResponse, byte[]> {
   Pattern pattern = Pattern.compile("<MD5OfMessageBody>([\\S&&[^<]]+)</MD5OfMessageBody>");
   private final EncryptionService encryptionService;

   @Inject
   RegexMD5Handler(EncryptionService encryptionService) {
      this.encryptionService = encryptionService;
   }

   @Override
   public byte[] apply(HttpResponse response) {
      byte[] value = null;
      try {
         Matcher matcher = pattern.matcher(Utils.toStringAndClose(response.getContent()));
         if (matcher.find()) {
            value = encryptionService.fromHexString(matcher.group(1));
         }
      } catch (IOException e) {
         Throwables.propagate(e);
      } finally {
         try {
            response.getContent().close();
         } catch (IOException e) {
            Throwables.propagate(e);
         }
      }
      return value;
   }

}
