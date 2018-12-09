package sds.officeprocessor.commandhandlers;

import sds.officeprocessor.domain.commands.ConvertToPdf;
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
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;
import sds.messaging.callback.AbstractMessageProcessor;
import sds.officeprocessor.converters.*;
import sds.officeprocessor.domain.events.ConvertedToPdf;

@Component
public class ConvertToPdfProcessor extends AbstractMessageProcessor<ConvertToPdf> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertToPdfProcessor.class);

    ReceiverBusControl receiver;
    IBusControl bus;
    BlobStorage storage;

    @Autowired
    public ConvertToPdfProcessor(ReceiverBusControl receiver, IBusControl bus,
            BlobStorage storage) throws JTransitLightException, IOException {
        this.bus = bus;
        this.receiver = receiver;
        this.storage = storage;
    }

    public void process(ConvertToPdf message) {
        try {
            BlobInfo blob = storage.getFileInfo(new Guid(message.getBlobId()), message.getBucket());
            InputStream data = null;
            IConvert converter = null;

            switch (FilenameUtils.getExtension(blob.getFileName()).toLowerCase()) {
                case "doc":
                    converter = new DocToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                case "docx":
                    converter = new DocxToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                case "odt":
                    converter = new OdtToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                case "xls":
                    converter = new XlsToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
//                case "ods":
//                    converter = new OdsToPdf();
//                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
//                    break;
                case "xlsx":
                    converter = new XlsxToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                case "ppt":
                    converter = new PptToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                case "pptx":
                    converter = new PptxToPdf();
                    data = converter.Convert(storage.getFileStream(new Guid(message.getBlobId()), message.getBucket()));
                    break;
                //case "odp":
                //now thinking...

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

            String bucket = message.getBucket();

            if (data != null) {
                Guid blobId = Guid.newGuid();

                storage.addFile(blobId, blobId.toString() + ".pdf", data, "application/pdf", bucket, null);

                ConvertedToPdf convertedToPdf = new ConvertedToPdf();
                convertedToPdf.setCorrelationId(message.getCorrelationId());
                convertedToPdf.setBlobId(blobId);
                convertedToPdf.setBucket(bucket);
                convertedToPdf.setTimeStamp(getTimestamp());
                convertedToPdf.setUserId(message.getUserId());
                convertedToPdf.setId(message.getId());

                bus.publish(convertedToPdf);
            } else {

                ConvertToPdfFailed convertToPdfFailed = new ConvertToPdfFailed();
                convertToPdfFailed.setId(message.getId());
                convertToPdfFailed.setMessage("Cannot convert file to pdf from bucket " + message.getBucket() + " with Id " + message.getBlobId());
                convertToPdfFailed.setCorrelationId(message.getCorrelationId());
                convertToPdfFailed.setUserId(message.getUserId());
                convertToPdfFailed.setTimeStamp(getTimestamp());

                bus.publish(convertToPdfFailed);
            }

        } catch (Exception ex) {
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
