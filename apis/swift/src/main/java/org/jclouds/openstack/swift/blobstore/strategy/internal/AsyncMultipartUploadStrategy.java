package org.jclouds.openstack.swift.blobstore.strategy.internal;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ImplementedBy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;

@ImplementedBy(ParallelMultipartUploadStrategy.class)
public interface AsyncMultipartUploadStrategy {
    ListenableFuture<String> execute(String container, Blob blob, PutOptions options, BlobToObject blob2Object);
}
