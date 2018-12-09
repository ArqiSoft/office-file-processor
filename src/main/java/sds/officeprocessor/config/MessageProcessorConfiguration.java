package sds.officeprocessor.config;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.publisher.IBusControl;
import sds.officeprocessor.domain.commands.ConvertToPdf;
import sds.officeprocessor.commandhandlers.ConvertToPdfCommandMessageCallback;
import sds.messaging.callback.AbstractMessageProcessor;
import sds.officeprocessor.commandhandlers.ExtractMetaCommandMessageCallback;
import sds.officeprocessor.domain.commands.ExtractMeta;

@Component
public class MessageProcessorConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorConfiguration.class);

    
    @Autowired
    public MessageProcessorConfiguration(IBusControl busControl, 
            ReceiverBusControl receiver, 
            AbstractMessageProcessor<ConvertToPdf> convertToPdfProcessor,
            BlockingQueue<ConvertToPdf> convertToPdfQueue,
            @Value("${convertToPdfQueueName}") String convertToPdfQueueName,
            AbstractMessageProcessor<ExtractMeta> extractMetaProcessor,
            BlockingQueue<ExtractMeta> extractMetaQueue,
            @Value("${extractMetaQueueName}") String extractMetaQueueName,
            @Value("${EXECUTORS_THREAD_COUNT:2}") Integer threadCount) 
                    throws JTransitLightException, IOException, InterruptedException {
        
        receiver.subscribe(new ConvertToPdf().getQueueName(), convertToPdfQueueName, 
                ConsumerSettings.newBuilder().withDurable(true).build(), 
                new ConvertToPdfCommandMessageCallback(ConvertToPdf.class, convertToPdfQueue));
        
        LOGGER.debug("EXECUTORS_THREAD_COUNT is set to {}", threadCount);
        
        Executors.newSingleThreadExecutor().submit(() -> {
            final ExecutorService threadPool = 
                    Executors.newFixedThreadPool(threadCount);
            
            while (true) {
                // wait for message
                final ConvertToPdf message = convertToPdfQueue.take();
                
                // submit to processing pool
                threadPool.submit(() -> convertToPdfProcessor.doProcess(message));
                Thread.sleep(10);
            }
        });
        
        receiver.subscribe(new ExtractMeta().getQueueName(), extractMetaQueueName, 
                ConsumerSettings.newBuilder().withDurable(true).build(), 
                new ExtractMetaCommandMessageCallback(ExtractMeta.class, extractMetaQueue));
        
        Executors.newSingleThreadExecutor().submit(() -> {
            final ExecutorService threadPool = 
                    Executors.newFixedThreadPool(threadCount);
            
            while (true) {
                // wait for message
                final ExtractMeta message = extractMetaQueue.take();
                
                // submit to processing pool
                threadPool.submit(() -> extractMetaProcessor.doProcess(message));
                Thread.sleep(10);
            }
        });
        
    }
}
