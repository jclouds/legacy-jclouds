package org.jclouds.aws.ec2.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.Region;
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
public class InstanceStateStopped implements Predicate<RunningInstance> {

   private final InstanceClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public InstanceStateStopped(InstanceClient client) {
      this.client = client;
   }

   public boolean apply(RunningInstance instance) {
      logger.trace("looking for state on instance %s", instance);

      instance = refresh(instance.getId());
      logger.trace("%s: looking for instance state %s: currently: %s", instance.getId(),
               InstanceState.STOPPED, instance.getInstanceState());
      return instance.getInstanceState() == InstanceState.STOPPED;
   }

   private RunningInstance refresh(String instanceId) {
      return Iterables.getLast(Iterables.getLast(client.describeInstancesInRegion(
               Region.DEFAULT,instanceId))
               .getRunningInstances());
   }
}
