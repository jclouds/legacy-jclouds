package org.jclouds.vcloud.predicates;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;

import com.google.common.base.Predicate;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class TaskSuccess implements Predicate<URI> {

   private final VCloudAsyncClient client;

   @Inject(optional = true)
   @Named("org.jclouds.vcloud.timeout")
   private long taskTimeout = 30000;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(VCloudAsyncClient client) {
      this.client = client;
   }

   public boolean apply(URI taskUri) {
      logger.trace("looking for status on task %s", taskUri);

      Task task;
      try {
         task = client.getTask(taskUri).get(taskTimeout, TimeUnit.MILLISECONDS);
         logger.trace("%s: looking for status %s: currently: %s", task, TaskStatus.SUCCESS, task
                  .getStatus());
         return task.getStatus() == TaskStatus.SUCCESS;
      } catch (InterruptedException e) {
         logger.warn(e, "%s interrupted, returning false", taskUri);
      } catch (ExecutionException e) {
         logger.warn(e, "%s exception, returning false", taskUri);
      } catch (TimeoutException e) {
         logger.warn(e, "%s timeout, returning false", taskUri);
      }

      return false;
   }

}
