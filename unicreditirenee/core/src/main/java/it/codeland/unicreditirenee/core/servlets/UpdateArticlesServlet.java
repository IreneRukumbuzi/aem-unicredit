// package it.codeland.unicreditirenee.core.servlets;

// import com.day.cq.wcm.api.Page;
// import com.day.cq.wcm.api.PageManager;
// import com.google.gson.Gson;
// import it.codeland.unicreditirenee.core.services.PageService;
// import it.codeland.unicreditirenee.core.utils.ResourceUtil;
// import javax.jcr.Node;
// import javax.servlet.Servlet;
// import org.apache.commons.lang.exception.ExceptionUtils;
// import org.apache.sling.api.SlingHttpServletRequest;
// import org.apache.sling.api.SlingHttpServletResponse;
// import org.apache.sling.api.resource.Resource;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.api.resource.ResourceResolverFactory;
// import org.apache.sling.api.servlets.SlingAllMethodsServlet;
// import org.apache.sling.servlets.annotations.SlingServletPaths;
// import org.osgi.service.component.annotations.Component;
// import org.osgi.service.component.annotations.Reference;
// import org.osgi.service.component.propertytypes.ServiceDescription;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Component(service = { Servlet.class })
// @SlingServletPaths(value = { "/bin/articles" })
// @ServiceDescription("Update articles Servlet")
// public class UpdateArticlesServlet extends SlingAllMethodsServlet {

//     private final Logger LOG = LoggerFactory.getLogger(UpdateArticlesServlet.class);
//     private static final long serialVersionUID = 1L;

//     PageManager pageManager;

//     @Reference
//     protected ResourceResolverFactory resolverFactory;

//     public class ImportInfo {
//         String total;
//         String skipped;
//         String created;
//         String failedArticle;
//         Boolean fileStatus;
//         Boolean noChange;
//     }

//     @Override
//     protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
//         try {
//             ResourceResolver resolver = ResourceUtil.getResourceResolver(resolverFactory);
//             PageService ps = new PageService();
//             ps.CreatePage("/content/dam/unicreditirenee/articles.csv", resolver);
          
//             pageManager = resolver.adaptTo(PageManager.class);
//             Page magazine = pageManager.getPage("/content/unicreditirenee/magazine/");
//             Resource magazineContent = magazine.getContentResource();
//             Node magazineNode = magazineContent.adaptTo(Node.class);

//             ImportInfo imp = new ImportInfo();
//             imp.total = magazineNode.getProperty("totalArticles").getValue().toString();
//             imp.skipped = magazineNode.getProperty("skippedArticle").getValue().toString();
//             imp.created = magazineNode.getProperty("createdArticle").getValue().toString();
//             imp.failedArticle = magazineNode.getProperty("failedArticle").getValue().toString();
//             imp.fileStatus = magazineNode.getProperty("isCsvFileValid").getBoolean();
//             imp.noChange = magazineNode.getProperty("noChange").getBoolean();

//             String json = new Gson().toJson(imp);

//             resp.setCharacterEncoding("UTF-8");
//             resp.setContentType("application/json");
//             resp.getWriter().write(json);
//         } catch (Exception e) {
//             LOG.error("\n\n\nError: " + ExceptionUtils.getStackTrace(e));
//         }
//     }

//     @Override
//     protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
//         try {
//             ResourceResolver resolver = ResourceUtil.getResourceResolver(resolverFactory);

//             pageManager = resolver.adaptTo(PageManager.class);
//             Page magazine = pageManager.getPage("/content/unicreditirenee/magazine/");
//             Resource magazineContent = magazine.getContentResource();
//             Node magazineNode = magazineContent.adaptTo(Node.class);

//             ImportInfo imp = new ImportInfo();
//             imp.total = magazineNode.getProperty("totalArticles").getValue().toString();
//             imp.skipped = magazineNode.getProperty("skippedArticle").getValue().toString();
//             imp.created = magazineNode.getProperty("createdArticle").getValue().toString();
//             imp.failedArticle = magazineNode.getProperty("failedArticle").getValue().toString();
//             imp.fileStatus = magazineNode.getProperty("isCsvFileValid").getValue().getBoolean();
//             imp.noChange = magazineNode.getProperty("noChange").getBoolean();

//             LOG.info(imp.failedArticle);

//             String json = new Gson().toJson(imp);

//             resp.setCharacterEncoding("UTF-8");
//             resp.setContentType("application/json");
//             resp.getWriter().write(json);
//         } catch (Exception e) {
//             LOG.debug("\n\n\n Error: " + ExceptionUtils.getStackTrace(e));
//         }
//     }

// }