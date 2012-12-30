package org.jclouds.openstack.cinder.v1.features;

import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.options.CreateSnapshotOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volume Snapshots via their REST API.
 * 
 * @see SnapshotAsyncApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 */
public interface SnapshotApi {
   /**
    * Returns a summary list of Snapshots.
    *
    * @return The list of Snapshots
    */
   FluentIterable<? extends Snapshot> list();

   /**
    * Returns a detailed list of Snapshots.
    *
    * @return The list of Snapshots
    */
   FluentIterable<? extends Snapshot> listInDetail();

   /**
    * Return data about the given Snapshot.
    *
    * @param snapshotId Id of the Snapshot
    * @return Details of a specific Snapshot
    */
   Snapshot get(String snapshotId);

   /**
    * Creates a new Snapshot. The Volume status must be Available.
    * 
    * @param volumeId The Volume Id from which to create the Snapshot
    * @param options See CreateSnapshotOptions
    * @return The new Snapshot
    */
   Snapshot create(String volumeId, CreateSnapshotOptions... options);

   /**
    * Delete a Snapshot.
    *
    * @param snapshotId Id of the Snapshot
    * @return true if successful, false otherwise
    */
   boolean delete(String snapshotId);
}
