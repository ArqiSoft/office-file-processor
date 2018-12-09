package sds.officeprocessor.metaextractors;

import java.io.InputStream;
import java.util.List;
import sds.officeprocessor.domain.models.Property;

public interface IMetaExtractor {
    List<Property> GetMeta(InputStream stream);
}
