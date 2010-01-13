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
package org.jclouds.aws.ec2;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class TestLogger {
   static class AppenderForTesting extends AppenderSkeleton {
      private static List<String> messages = Lists.newArrayList();

      protected void append(LoggingEvent event) {
         messages.add(event.getLoggerName() + ":" + event.getRenderedMessage());
      }

      public void close() {
      }

      public boolean requiresLayout() {
         return false;
      }

      public static List<String> getMessages() {
         return messages;
      }

      public static void clear() {
         messages.clear();
      }
   }

   static class LogApples {
      final Logger logger = LogManager.getLogger(LogApples.class);

      public void log(String in) {
         logger.info(in);
      }
   }

   @Test
   void testFoo() {
      Logger.getRootLogger().addAppender(new AppenderForTesting());
      Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));

      new LogApples().log("foo");
      assertEquals(AppenderForTesting.getMessages(), ImmutableList.of("foo"));
   }
}
