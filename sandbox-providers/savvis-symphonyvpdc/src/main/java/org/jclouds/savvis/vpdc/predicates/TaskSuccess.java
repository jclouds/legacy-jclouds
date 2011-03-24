package org.jclouds.savvis.vpdc.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

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
      logger.trace("looking for status on task %s", checkNotNull(taskId, "taskId"));
      Task task = refresh(taskId);
      if (task == null)
         return false;
      logger.trace("%s: looking for task status %s: currently: %s", task.getId(), Task.Status.SUCCESS, task.getStatus());
      if (task.getError() != null)
         throw new IllegalStateException(String.format("task %s failed with exception %s", task.getId(), task
               .getError().toString()));
      return task.getStatus() == Task.Status.SUCCESS;
   }

   private Task refresh(String taskId) {
      return client.getBrowsingClient().getTask(taskId);
   }
}
