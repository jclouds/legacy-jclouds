package org.jclouds.aws.ec2.binders;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.ec2.binders.IfNotNullBindAvailabilityZoneToFormParam;

/**
 * Binds the AvailabilityZone to a form parameter if set.
 * 
 * @author Adrian Cole
 */
@Singleton
public class IfNotNullBindAvailabilityZoneToLaunchSpecificationFormParam extends IfNotNullBindAvailabilityZoneToFormParam {

   @Inject
   protected IfNotNullBindAvailabilityZoneToLaunchSpecificationFormParam() {
      super("LaunchSpecification.Placement.AvailabilityZone");
   }

}
