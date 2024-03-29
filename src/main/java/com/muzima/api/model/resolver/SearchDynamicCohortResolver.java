package com.muzima.api.model.resolver;

import com.muzima.api.model.algorithm.CohortAlgorithm;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
public class SearchDynamicCohortResolver extends BaseOpenmrsResolver {

    private static final String REPRESENTATION =
            "?v=custom:" + CohortAlgorithm.COHORT_STANDARD_REPRESENTATION;

    /**
     * Return the full REST resource based on the parameters passed to the method.
     *
     * @param resourceParams the parameters of the resource to resolved.
     * @return full uri to the REST resource.
     */
    public String resolve(final Map<String, String> resourceParams) throws IOException {
        StringBuilder paramBuilder = new StringBuilder();
        for (String key : resourceParams.keySet()) {
            paramBuilder.append("&").append(key).append("=").append(URLEncoder.encode(resourceParams.get(key), "UTF-8"));
        }
        return getConfiguration().getServer() + "/ws/rest/v1/reportingrest/cohortDefinition" + REPRESENTATION + paramBuilder.toString();
    }
}
