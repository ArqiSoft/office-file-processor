package sds.officeprocessor.metaextractors;

import com.aspose.slides.IDocumentProperties;
import com.aspose.slides.Presentation;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import sds.officeprocessor.domain.models.Property;

public class PresentationMetaExtractor implements IMetaExtractor {

    @Override
    public List<Property> GetMeta(InputStream stream) {

        Presentation presentation = new Presentation(stream);

        IDocumentProperties dp = presentation.getDocumentProperties();

        List<Property> meta = new ArrayList<>();
        
        for (int i = 0; i < dp.getCount(); i++) {
          
            String propertyValue = dp.get_Item(dp.getPropertyName(i)).toString();
            if(propertyValue != null && !propertyValue.isEmpty())
            {
                meta.add(new Property(dp.getPropertyName(i), propertyValue));
            }
        }

        return meta;
    }
}

