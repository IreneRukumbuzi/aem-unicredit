// package it.codeland.unicreditirenee.core.servlets;

// import com.day.cq.dam.api.Asset;
// import com.day.cq.dam.api.AssetManager;
// import com.day.cq.wcm.api.Page;
// import com.day.cq.wcm.api.PageManager;
// import it.codeland.unicreditirenee.core.utils.ResourceUtil;
// import java.io.IOException;
// import java.io.InputStream;
// import java.nio.charset.StandardCharsets;
// import java.util.Iterator;
// import java.util.Map;
// import java.util.Objects;
// import javax.jcr.Node;
// import javax.jcr.RepositoryException;
// import javax.jcr.ValueFormatException;
// import javax.jcr.lock.LockException;
// import javax.jcr.nodetype.ConstraintViolationException;
// import javax.jcr.version.VersionException;
// import javax.servlet.Servlet;
// import org.apache.commons.io.FilenameUtils;
// import org.apache.sling.api.SlingHttpServletRequest;
// import org.apache.sling.api.SlingHttpServletResponse;
// import org.apache.sling.api.request.RequestParameter;
// import org.apache.sling.api.resource.LoginException;
// import org.apache.sling.api.resource.Resource;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.api.resource.ResourceResolverFactory;
// import org.apache.sling.api.servlets.SlingAllMethodsServlet;
// import org.apache.sling.servlets.annotations.SlingServletPaths;
// import org.osgi.service.component.annotations.Component;
// import org.osgi.service.component.annotations.Reference;
// import org.osgi.service.component.propertytypes.ServiceDescription;

// @Component(service = { Servlet.class })
// @SlingServletPaths(value = { "/bin/import-csv" })
// @ServiceDescription("Import articles from csv file Servlet")
// public class ImportCsvServlet extends SlingAllMethodsServlet {

//     private static final long serialVersionUID = 1L;
//     private static final String CSV_MIME_TYPE = "text/csv";
//     private static final String CSV_FILE_EXTENSION = ".csv";

//     PageManager pageManager;

//     @Reference
//     protected ResourceResolverFactory resolverFactory;

//     @Override
//     protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
//         final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
//         Iterator<Map.Entry<String, RequestParameter[]>> paramsIterator = params.entrySet().iterator();

//         Map.Entry<String, RequestParameter[]> pairs = paramsIterator.next();
//         RequestParameter[] parameterArray = pairs.getValue();

//         // Uploaded file
//         RequestParameter file = parameterArray[0];

//         try {
//             InputStream stream = file.getInputStream();
//             //
//             Asset fileInJCR = storeFileInJCR("/content/dam/unicreditirenee/articles.csv",
//                     FilenameUtils.getBaseName(file.getFileName()).concat(CSV_FILE_EXTENSION), stream);
//             if (Objects.nonNull(fileInJCR)) {
//                 sendStatus(response, 201, "File uploaded");
//             } else {
//                 sendStatus(response, 500, "Error, file not uploaded");
//             }
//         } catch (Exception e) {
//             sendStatus(response, 500, "Error, server error!");
//             e.printStackTrace();
//         }
//     }

//     private Asset storeFileInJCR(String destinationPath, String fileName, InputStream jsonStream)
//             throws LoginException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
//         ResourceResolver resolver = ResourceUtil.getResourceResolver(resolverFactory);
//         AssetManager assetManager = resolver.adaptTo(AssetManager.class);
//         if (Objects.nonNull(assetManager)) {
//             pageManager = resolver.adaptTo(PageManager.class);
//             Page articles = pageManager.getPage("/content/unicreditirenee/magazine/");

//             Resource articlesContent = articles.getContentResource();
//             Node articlesNode = articlesContent.adaptTo(Node.class); 
//             articlesNode.setProperty("noChange", false);
//             return assetManager.createAsset(destinationPath, jsonStream, CSV_MIME_TYPE, true);
//         } else {
//             return null;
//         }
//     }

//     private void sendStatus(SlingHttpServletResponse response, int status, String message) throws IOException {
//         response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
//         response.setContentType("text/plain");
//         response.setStatus(status);
//         response.getWriter().print(message);
//     }
// }