package it.codeland.unicreditirenee.core.utils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.text.csv.Csv;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CsvUtil.class);

    public Iterator<String[]> readCsvFile(ResourceResolver resolver) {
        try {
            Resource filePath = resolver.getResource("/content/dam/unicreditirenee/articles.csv");
            if (filePath == null) {
                LOG.error("\n > CSV Util[0] File not found ");
                return null;
            }

            Asset asset = filePath.adaptTo(Asset.class);
            Rendition assetRendition = asset.getOriginal();
            InputStream inputStream = assetRendition.getStream();

            Csv csv = new Csv();
            csv.setFieldDelimiter('\"');
            csv.setFieldSeparatorRead('|');
            Iterator<String[]> articlesIter = csv.read(inputStream, null);

            return articlesIter;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("\n > Error: " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}