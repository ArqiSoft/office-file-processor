package sds.officeprocessor.commandhandlers;
import sds.officeprocessor.domain.commands.ConvertToPdf;
import java.util.concurrent.BlockingQueue;
import sds.messaging.callback.AbstractMessageCallback;

public class ConvertToPdfCommandMessageCallback extends AbstractMessageCallback<ConvertToPdf> {

    public ConvertToPdfCommandMessageCallback(Class<ConvertToPdf> tClass, BlockingQueue<ConvertToPdf> queue) {
        super(tClass, queue);
    }

}
