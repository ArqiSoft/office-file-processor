package sds.officeprocessor.commandhandlers;
import java.util.concurrent.BlockingQueue;
import sds.messaging.callback.AbstractMessageCallback;
import sds.officeprocessor.domain.commands.ExtractMeta;

public class ExtractMetaCommandMessageCallback extends AbstractMessageCallback<ExtractMeta> {

    public ExtractMetaCommandMessageCallback(Class<ExtractMeta> tClass, BlockingQueue<ExtractMeta> queue) {
        super(tClass, queue);
    }

}
