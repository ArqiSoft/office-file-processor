package sds.officeprocessor.metaextractors;

import com.aspose.words.BuiltInDocumentProperties;
import com.aspose.words.Document;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sds.officeprocessor.domain.models.Property;

public class DocMetaExtractor implements IMetaExtractor {

    @Override
    public List<Property> GetMeta(InputStream stream) {

        List<Property> meta = new ArrayList<>();
        try {
            Document doc = new Document(stream);

            BuiltInDocumentProperties dp = doc.getBuiltInDocumentProperties();

            for (int i = 0; i < dp.getCount(); i++) {

                String propertyValue = dp.get(i).getValue().toString();
                if (propertyValue != null && !propertyValue.isEmpty()) {
                    meta.add(new Property(dp.get(i).getName(), propertyValue));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DocMetaExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return meta;
    }

}
