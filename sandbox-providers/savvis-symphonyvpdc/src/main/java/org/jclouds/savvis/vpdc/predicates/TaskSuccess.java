package org.jclouds.savvis.vpdc.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.savvis.vpdc.VPDCClient;
import org.jclouds.savvis.vpdc.domain.Task;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class TaskSuccess implements Predicate<String> {

   private final VPDCClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(VPDCClient client) {
      this.client = client;
   }

   public boolean apply(String taskId) {
      logger.trace("looking for status on task %s", taskId);

      Task task = client.getBrowsingClient().getTask(taskId);
      logger.trace("%s: looking for status %s: currently: %s", task, Task.Status.SUCCESS, task.getStatus());
      if (task.getStatus() == Task.Status.ERROR || task.getStatus() == Task.Status.NONE)
         throw new RuntimeException("error on task: " + task.getHref() + " error: " + task.getError());
      return task.getStatus() == Task.Status.SUCCESS;
   }

}
