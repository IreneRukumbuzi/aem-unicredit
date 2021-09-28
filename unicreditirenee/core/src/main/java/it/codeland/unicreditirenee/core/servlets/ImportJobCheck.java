package it.codeland.unicreditirenee.core.servlets;

import com.day.cq.wcm.api.PageManager;

import com.day.cq.wcm.api.Page;
import javax.jcr.Node;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.codeland.unicreditirenee.core.utils.ResourceUtil;

import javax.servlet.Servlet;

@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/import/status" })
@ServiceDescription("Import CSV file and creates article pages automatically")
public class ImportJobCheck extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ImportJobCheck.class);

    String pagePath = "/content/unicreditirenee/magazine/";

    @Reference
    private JobManager jobManager;

    PageManager pageManager;

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
        try {
            ResourceResolver resourceResolver = ResourceUtil.getResourceResolver(resourceResolverFactory);

            pageManager = resourceResolver.adaptTo(PageManager.class);
            Page magazine = pageManager.getPage(pagePath);
            Resource magazineContent = magazine.getContentResource();
            Node magazineNode = magazineContent.adaptTo(Node.class);

            JSONObject json = new JSONObject();
            json.put("createdRows", magazineNode.getProperty("createdRows").getValue().toString());
            json.put("skippedRows", magazineNode.getProperty("skippedRows").getValue().toString());
            json.put("csvRows", magazineNode.getProperty("csvRows").getValue().toString());
            json.put("processedRows", magazineNode.getProperty("processedRows").getValue().toString());
            json.put("state", magazineNode.getProperty("state").getValue().toString());

            resp.setContentType("application/json");
            resp.getWriter().println(json);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n \n  > error [ProgressCheckServlet.doGet] " + ExceptionUtils.getStackTrace(e) + " < \n \n ");
        }
    }

}
