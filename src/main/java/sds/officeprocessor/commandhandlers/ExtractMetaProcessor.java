package sds.officeprocessor.commandhandlers;

import sds.officeprocessor.domain.events.ConvertToPdfFailed;
import java.io.IOException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.sds.storage.BlobInfo;
import com.sds.storage.BlobStorage;
import com.sds.storage.Guid;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import sds.messaging.callback.AbstractMessageProcessor;
import sds.officeprocessor.domain.commands.ExtractMeta;
import sds.officeprocessor.domain.events.MetaExtracted;
import sds.officeprocessor.domain.models.Property;
import sds.officeprocessor.metaextractors.DocMetaExtractor;
import sds.officeprocessor.metaextractors.ExcelMetaExtractor;
import sds.officeprocessor.metaextractors.IMetaExtractor;
import sds.officeprocessor.metaextractors.PresentationMetaExtractor;

@Component
public class ExtractMetaProcessor extends AbstractMessageProcessor<ExtractMeta> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractMetaProcessor.class);

    ReceiverBusControl receiver;
    IBusControl bus;
    BlobStorage storage;

    @Autowired
    public ExtractMetaProcessor(ReceiverBusControl receiver, IBusControl bus,
            BlobStorage storage) throws JTransitLightException, IOException {
        this.bus = bus;
        this.receiver = receiver;
        this.storage = storage;
    }

    public void process(ExtractMeta message) {
        try {
            BlobInfo blob = storage.getFileInfo(new Guid(message.getBlobId()), message.getBucket());
            List<Property> meta = null;
            IMetaExtractor extractor = null;

            File directory = new File(System.getenv("OSDR_TEMP_FILES_FOLDER"));
            File tempFile = File.createTempFile("temp", ".tmp", directory);

            try (InputStream fs = Files.newInputStream(Paths.get(tempFile.getCanonicalPath()), StandardOpenOption.DELETE_ON_CLOSE)) {

                switch (FilenameUtils.getExtension(blob.getFileName()).toLowerCase()) {
                    case "doc":
                    case "docx":
                    case "odt":
                        extractor = new DocMetaExtractor();
                        meta = extractor.GetMeta(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                        break;

                    case "xls":
                    case "xlsx":
                    case "ods":
                        extractor = new ExcelMetaExtractor();
                        meta = extractor.GetMeta(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                        break;

                    case "ppt":
                    case "pptx":
                    case "odp":
                        extractor = new PresentationMetaExtractor();
                        meta = extractor.GetMeta(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                        break;

                    default:
                        ConvertToPdfFailed convertToPdfFailed = new ConvertToPdfFailed();
                        convertToPdfFailed.setId(message.getId());
                        convertToPdfFailed.setMessage(String.format("Cannot find file converter for %s.", blob.getFileName()));
                        convertToPdfFailed.setCorrelationId(message.getCorrelationId());
                        convertToPdfFailed.setUserId(message.getUserId());
                        convertToPdfFailed.setTimeStamp(getTimestamp());

                        bus.publish(convertToPdfFailed);

                        return;
                }
                if (meta != null) {
                    MetaExtracted metaExtracted = new MetaExtracted();
                    metaExtracted.setMeta(meta);
                    metaExtracted.setCorrelationId(message.getCorrelationId());
                    metaExtracted.setTimeStamp(getTimestamp());
                    metaExtracted.setUserId(message.getUserId());
                    metaExtracted.setId(message.getId());
                    metaExtracted.setBlobId(message.getBlobId());
                    metaExtracted.setBucket(message.getBucket());
                   
                    bus.publish(metaExtracted);
                }
            }

        } catch (IOException ex) {
            ConvertToPdfFailed convertToPdfFailed = new ConvertToPdfFailed();
            convertToPdfFailed.setId(message.getId());
            convertToPdfFailed.setMessage("Cannot convert file to pdf from bucket " + message.getBucket() + " with Id " + message.getBlobId() + ". Error: " + ex.getMessage());
            convertToPdfFailed.setCorrelationId(message.getCorrelationId());
            convertToPdfFailed.setUserId(message.getUserId());
            convertToPdfFailed.setTimeStamp(getTimestamp());

            bus.publish(convertToPdfFailed);
        }
    }

    private String getTimestamp() {
        //("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return LocalDateTime.now().toString();
    }

}
