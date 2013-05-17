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
package org.jclouds.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.google.common.base.Predicate;

/** A logger implementation for use in testing; all log messages are remembered, \
 * but not written anywhere. The messages can then be inspected {@link #getMessages()}
 * or certain assertions applied (see assertXxx methods on these instances) */
public class BufferLogger extends BaseLogger {

   final String category;
   Level level = Level.INFO;
   List<Record> messages = Collections.synchronizedList(new ArrayList<Record>());

   public static class Record {
      Level level;
      String message;
      Throwable trace;
      public Record(Level level, String message, Throwable trace) {
         this.level = level;
         this.message = message;
         this.trace = trace;
      }
      public Level getLevel() {
         return level;
      }
      public String getMessage() {
         return message;
      }
      public Throwable getTrace() {
         return trace;
      }
   }

   
   public BufferLogger(String category) {
      this.category = category;
   }
   
   public List<Record> getMessages() {
      return messages;
   }
   
   /** throws AssertionError if the log does not contain the indicated fragment;
    * otherwise returns a record which does satisfy the constraint 
    */
   public Record assertLogContains(String fragment) {
      for (Record r: messages) {
         if (r.getMessage()!=null && r.getMessage().contains(fragment)) return r;
      }
      throw new AssertionError("log did not contain expected '"+fragment+"'");
   }
   /** fails if log _does_ contain the indicated fragment */
   public void assertLogDoesntContain(String fragment) {
      for (Record r: messages) {
         if (r.getMessage()!=null && r.getMessage().contains(fragment)) 
            throw new AssertionError("log contained unexpected '"+fragment+"'");
      }
   }

   /** throws AssertionError if the log does not contain the indicated fragment;
    * otherwise returns a record which does satisfy the constraint 
    */
   public Record assertLogContains(Predicate<Record> test) {
      for (Record r: messages) {
         if (r.getMessage()!=null && test.apply(r)) return r;
      }
      throw new AssertionError("log did not contain any records satisfying expected predicate");      
   }
   
   @Override
   public String getCategory() {
      return category;
   }

   public void setLevel(Level level) {
      this.level = level;
   }
   public void setAllLevelsEnabled() {
      level = Level.ALL;
   }
   public void setAllLevelsDisabled() {
      level = Level.OFF;
   }
   
   @Override
   public boolean isTraceEnabled() {
      return level.intValue() <= Level.FINER.intValue();
   }
   @Override
   public boolean isDebugEnabled() {
      return level.intValue() <= Level.FINE.intValue();
   }

   @Override
   public boolean isInfoEnabled() {
      return level.intValue() <= Level.INFO.intValue();
   }

   @Override
   public boolean isWarnEnabled() {
      return level.intValue() <= Level.WARNING.intValue();
   }

   @Override
   public boolean isErrorEnabled() {
      return level.intValue() <= Level.SEVERE.intValue();
   }
   
   
   @Override
   protected void logError(String message, Throwable e) {
      getMessages().add(new Record(Level.SEVERE, message, e));
   }

   @Override
   protected void logError(String message) {
      getMessages().add(new Record(Level.SEVERE, message, null));
   }

   @Override
   protected void logWarn(String message, Throwable e) {
      getMessages().add(new Record(Level.WARNING, message, e));   
   }

   @Override
   protected void logWarn(String message) {
      getMessages().add(new Record(Level.WARNING, message, null));   
   }

   @Override
   protected void logInfo(String message) {
      getMessages().add(new Record(Level.INFO, message, null));   
   }

   @Override
   protected void logDebug(String message) {
      getMessages().add(new Record(Level.FINE, message, null));   
   }

   @Override
   protected void logTrace(String message) {
      getMessages().add(new Record(Level.FINER, message, null));   
   }

}
