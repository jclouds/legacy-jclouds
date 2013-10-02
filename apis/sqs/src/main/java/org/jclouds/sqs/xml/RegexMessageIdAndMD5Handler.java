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
package org.jclouds.sqs.xml;

import static com.google.common.io.BaseEncoding.base16;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.sqs.domain.MessageIdAndMD5;

import com.google.common.base.Function;
import com.google.common.hash.HashCodes;
import com.google.inject.Singleton;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QuerySendMessage.html"
 *      />
 * @author Adrian Cole
 */
@Singleton
public class RegexMessageIdAndMD5Handler implements Function<HttpResponse, MessageIdAndMD5> {
   private static final Pattern pattern = Pattern.compile("<MessageId>([\\S&&[^<]]+)</MessageId>\\s*<MD5OfMessageBody>([\\S&&[^<]]+)</MD5OfMessageBody>", Pattern.DOTALL);
   private final ReturnStringIf2xx returnStringIf200;

   @Inject
   public RegexMessageIdAndMD5Handler(ReturnStringIf2xx returnStringIf200) {
      this.returnStringIf200 = returnStringIf200;
   }

   @Override
   public MessageIdAndMD5 apply(HttpResponse response) {
      String content = returnStringIf200.apply(response);
      if (content != null) {
         Matcher matcher = pattern.matcher(content);
         if (matcher.find()) {
            return MessageIdAndMD5.builder()
                                  .id(matcher.group(1))
                                  .md5(HashCodes.fromBytes(base16().lowerCase().decode(matcher.group(2))))
                                  .build();
         }
      }
      return null;
   }

}
