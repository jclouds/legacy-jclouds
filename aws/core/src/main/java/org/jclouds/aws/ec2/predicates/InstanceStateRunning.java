package org.jclouds.aws.ec2.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class InstanceStateRunning implements Predicate<RunningInstance> {

   private final InstanceClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public InstanceStateRunning(InstanceClient client) {
      this.client = client;
   }

   public boolean apply(RunningInstance instance) {
      logger.trace("looking for state on instance %s", instance);

      instance = refresh(instance.getInstanceId());
      logger.trace("%s: looking for instance state %s: currently: %s", instance.getInstanceId(),
               InstanceState.RUNNING, instance.getInstanceState());
      return instance.getInstanceState() == InstanceState.RUNNING;
   }

   private RunningInstance refresh(String instanceId) {
      return Iterables.getLast(Iterables.getLast(client.describeInstances(instanceId))
               .getRunningInstances());
   }
}
