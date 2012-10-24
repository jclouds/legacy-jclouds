/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.monitor.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * This class takes care of monitoring {@link AsyncTask} jobs.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AsyncTaskStatusMonitor implements Function<AsyncTask, MonitorStatus> {
   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public MonitorStatus apply(final AsyncTask asyncTask) {
      checkNotNull(asyncTask, "asyncTask");

      try {
         asyncTask.refresh();

         switch (asyncTask.getState()) {
            case ABORTED:
            case FINISHED_UNSUCCESSFULLY:
               return MonitorStatus.FAILED;
            case FINISHED_SUCCESSFULLY:
               return MonitorStatus.DONE;
            case STARTED:
            case PENDING:
               return MonitorStatus.CONTINUE;
            default:
               throw new IllegalStateException("Unsupported task status");
         }
      } catch (Exception ex) {
         logger.warn(ex, "exception thrown while monitoring %s on %s, returning CONTINUE", asyncTask, getClass()
               .getName());

         return MonitorStatus.CONTINUE;
      }
   }
}
