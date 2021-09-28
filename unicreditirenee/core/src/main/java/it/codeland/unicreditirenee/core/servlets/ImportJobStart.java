package it.codeland.unicreditirenee.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.codeland.unicreditirenee.core.utils.ResourceUtil;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.http.Part;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/articles/import" })
@ServiceDescription("Import CSV file and creates article pages automatically")
public class ImportJobStart extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ImportJobStart.class);
    private static final String CSV_MIME_TYPE = "text/csv";
    String destinationPath = "/content/dam/unicreditirenee/articles.csv";
    String pagePath = "/content/unicreditirenee/magazine/";

    @Reference
    private JobManager jobManager;
    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            Part filePart = request.getPart("file");
            // Asset createdFile = saveFile(filePart.getInputStream());

            JSONObject json = new JSONObject();

            json.put("message", "Something may have gone wrong");
            // if (createdFile != null) {
            //     final Map<String, Object> props = new HashMap<String, Object>();
            //     Job job = jobManager.addJob("my/import/articlejob", props);
            //     json.put("jobId", job.getId());
            //     json.put("message", "File Uploaded Successfully");
            // }

            response.setContentType("application/json");
            response.getWriter().println(json);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n \n  > error [importServlet.doPost] " + ExceptionUtils.getStackTrace(e) + " < \n \n ");
        }
    }

    private Asset saveFile(InputStream uploadedFile) {
        try {
            ResourceResolver resolver = ResourceUtil.getResourceResolver(resourceResolverFactory);

            Resource artilclesParent = resolver.getResource(pagePath);

            Node articlesContentNode = artilclesParent.adaptTo(Node.class);
            Node articlesJcrContent = articlesContentNode.getNode("jcr:content");
            articlesJcrContent.setProperty("state", "processing");

            AssetManager assetManager = resolver.adaptTo(AssetManager.class);
            return assetManager.createAsset(destinationPath, uploadedFile, CSV_MIME_TYPE, true);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n \n  > error [saveFileInDam] " + ExceptionUtils.getStackTrace(e) + " < \n \n ");
            return null;
        }
    }
}
