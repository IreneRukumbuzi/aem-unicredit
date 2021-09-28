package it.codeland.unicreditirenee.core.jobs;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.sling.event.jobs.Job;
import org.osgi.framework.Constants;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.apache.sling.event.jobs.consumer.JobExecutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.codeland.unicreditirenee.core.services.PageService;
import it.codeland.unicreditirenee.core.utils.ResourceUtil;

@Component(service = JobExecutor.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "=Create the pages from the imported file of articles",
        JobExecutor.PROPERTY_TOPICS + "=my/import/articlejob" })
public class ImportJobExecutor implements JobExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(ImportJobExecutor.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public JobExecutionResult process(final Job job, JobExecutionContext context) {
        try {
            ResourceResolver resourceResolver = ResourceUtil.getResourceResolver(resourceResolverFactory);
            PageService ps = new PageService();
            ps.CreatePage(resourceResolver);

            if (context.isStopped()) {
                return context.result().message("Job failed").cancelled();
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n \n  - error [JobExecutionResult] " + ExceptionUtils.getStackTrace(e) + " -\n \n ");
        }
        return context.result().message("Job completed").succeeded();
    }
}