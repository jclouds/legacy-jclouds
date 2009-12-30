package org.jclouds.aws.ec2.predicates;

import static org.jclouds.aws.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.Snapshot;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.internal.Iterables;

/**
 * 
 * Tests to see if a snapshot is completed.
 * 
 * @author Adrian Cole
 */
@Singleton
public class SnapshotCompleted implements Predicate<Snapshot> {

   private final ElasticBlockStoreClient client;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SnapshotCompleted(ElasticBlockStoreClient client) {
      this.client = client;
   }

   public boolean apply(Snapshot snapshot) {
      logger.trace("looking for status on snapshot %s", snapshot.getId());

      snapshot = Iterables.getOnlyElement(client.describeSnapshotsInRegion(snapshot.getRegion(),
               snapshotIds(snapshot.getId())));
      logger.trace("%s: looking for status %s: currently: %s; progress %d/100", snapshot,
               Snapshot.Status.COMPLETED, snapshot.getStatus(), snapshot.getProgress());
      return snapshot.getStatus() == Snapshot.Status.COMPLETED;
   }

}
