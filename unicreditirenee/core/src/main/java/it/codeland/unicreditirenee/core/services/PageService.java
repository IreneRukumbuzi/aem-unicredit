package it.codeland.unicreditirenee.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import it.codeland.unicreditirenee.core.utils.CsvUtil;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.joda.time.DateTime;
import javax.jcr.Session;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PageService {
    private static final Logger LOG = LoggerFactory.getLogger(PageService.class);

    private Session session;

    PageManager pageManager;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    int createdRows = 0;
    int skippedRows = 0;
    int csvRows = 0;
    int processedRows = 0;

    DateTime savedDateTime = null;

    String pagePath = "/content/unicreditirenee/magazine/";
    String templatePath = "/apps/unicreditirenee/templates/article-page";

    public void CreatePage(ResourceResolver resourceResolver) {
        try {
            session = resourceResolver.adaptTo(Session.class);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            Resource artilclesParent = resourceResolver.getResource(pagePath);

            if (artilclesParent == null) {
                LOG.error("\n\n > pgsrvc [1] artilclesParentPage not found! \n\n");
                return;
            }

            CsvUtil csvFile = new CsvUtil();
            Iterator<String[]> csvData = csvFile.readCsvFile(resourceResolver);

            if (csvData == null) {
                LOG.error("\n\n > pgsrvc [1] File not found! \n\n");
                return;
            }
            Node articlesContentNode = artilclesParent.adaptTo(Node.class);
            Node articlesJcrContent = articlesContentNode.getNode("jcr:content");

            savedDateTime = new DateTime(articlesJcrContent.getProperty("lastModifiedAt").getValue().toString());

            Resource filePath = resourceResolver.getResource("/content/dam/unicreditirenee/articles.csv");

            Asset csvAsset = filePath.adaptTo(Asset.class);
            DateTime fileDateTime = new DateTime(csvAsset.getLastModified());

            if ((savedDateTime != null) && savedDateTime.equals(fileDateTime)) {
                LOG.error("\n\n > pgsrvc [1] File has not updated yet! \n\n");
                return;
            }

            articlesJcrContent.setProperty("lastModifiedAt", fileDateTime.toString());

            List<Map<String, String>> importedArticles = new ArrayList<Map<String, String>>();

            while (csvData.hasNext()) {
                String[] row = csvData.next();
                if (row.length == 1) {
                    row = row[0].split(",");
                }

                if (row.length >= 5) {
                    Map<String, String> currRow = new HashMap<String, String>();

                    currRow.put("title", row[0]);
                    currRow.put("name", row[1]);
                    currRow.put("tags", row[2]);
                    currRow.put("image", row[3]);
                    currRow.put("description", row[4]);
                    importedArticles.add(currRow);
                }
            }
            if (importedArticles.size() > 0) {
                csvRows = importedArticles.size() - 1;
                resetReport(articlesJcrContent);

                for (int i = 1; i < importedArticles.size(); i++) {
                    processedRows++;
                    articlesJcrContent.setProperty("processedRows", processedRows);
                    Map<String, String> art = importedArticles.get(i);
                    Page currPage = pageManager.getPage(pagePath + art.get("name"));
                    if (currPage != null) {
                        skippedRows++;
                        articlesJcrContent.setProperty("skippedRows", skippedRows);
                        continue;
                    }
                    Page newPage = pageManager.create(pagePath, art.get("name"), templatePath, art.get("title"));
                    if (newPage != null) {
                        Node articlePageNode = newPage.adaptTo(Node.class);
                        Node jcrContent = articlePageNode.getNode("jcr:content");
                        if (jcrContent != null) {
                            updateJcrContent(jcrContent, art);
                        }
                        createdRows++;
                        articlesJcrContent.setProperty("createdRows", createdRows);
                    }
                }
            }
            articlesJcrContent.setProperty("state", "completed");
            session.save();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n \n  --------- error [pageService] " + ExceptionUtils.getStackTrace(e) + " --------- \n \n ");
        }
    }

    public void updateJcrContent(Node jcrContent, Map<String, String> art) {
        try {
            jcrContent.setProperty("jcr:title", art.get("title"));
            jcrContent.setProperty("image", art.get("image"));
            jcrContent.setProperty("description", art.get("description"));
            jcrContent.setProperty("cq:tags", art.get("tags").split("&"));
            jcrContent.setProperty("date", savedDateTime.toString());
        } catch (RepositoryException e) {
            e.printStackTrace();
            LOG.error("\n \n  --------- error [updateJcrContent] " + ExceptionUtils.getStackTrace(e)
                    + " --------- \n \n ");
        }
    }

    public void resetReport(Node articlesJcrContent) {
        try {
            articlesJcrContent.setProperty("state", "processing");
            articlesJcrContent.setProperty("csvRows", csvRows);
            articlesJcrContent.setProperty("createdRows", 0);
            articlesJcrContent.setProperty("skippedRows", 0);
            articlesJcrContent.setProperty("processedRows", 0);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n \n  - error [resetReport] " + ExceptionUtils.getStackTrace(e) + " -\n \n ");
        }
        return;
    }
}