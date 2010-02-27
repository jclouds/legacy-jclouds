package org.jclouds.gogrid.binders;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oleksiy Yarmula
 */
public class BindIdsToQueryParams  implements Binder {

    /**
     * Binds the ids to query parameters. The pattern, as
     * specified by GoGrid's specification, is:
     *
     * https://api.gogrid.com/api/grid/server/get
     *      ?id=5153
     *      &id=3232
     *
     * @param request
     *          request where the query params will be set
     * @param input array of String params
     */
    @Override
    public void bindToRequest(HttpRequest request, Object input) {
        checkArgument(checkNotNull(request, "request is null") instanceof GeneratedHttpRequest,
                "this binder is only valid for GeneratedHttpRequests!");
        checkArgument(checkNotNull(input, "input is null") instanceof Long[],
                "this binder is only valid for Long[] arguments");

        Long[] names = (Long[]) input;
        GeneratedHttpRequest generatedRequest = (GeneratedHttpRequest) request;

        for(Long id : names) {
            generatedRequest.addQueryParam("id", checkNotNull(id.toString(),
                                                        /*or throw*/ "id must have a non-null value"));
        }

    }

}
