package sds.officeprocessor.domain.events;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sds.storage.Guid;
import sds.messaging.contracts.AbstractContract;
import static java.lang.reflect.Array.get;
import java.lang.reflect.Field;
import java.util.List;

public class ConvertedToPdf extends AbstractContract {

    private String bucket;
    private Guid blobId;
    private UUID id;
    private String timeStamp;
    private UUID userId;
    

    public ConvertedToPdf() {
        namespace = "Sds.OfficeProcessor.Domain.Events";
        contractName = ConvertedToPdf.class.getSimpleName();
    }


    /**
     * @return the id
     */
    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }
    
        /**
     * @return the id of blob
     */
    @JsonProperty("BlobId")
    public UUID getBlobId() {
        return blobId.getUUID();
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the timeStamp
     */
    @JsonProperty("TimeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the userId
     */
    @JsonProperty("UserId")
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
    
    @JsonProperty("Bucket")
    public String getBucket() {
        return bucket;
    }

    public void setBlobId(Guid blobId) {
        this.blobId = blobId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s [id=%s, timeStamp=%s, userId=%s, namespace=%s, contractName=%s, correlationId=%s]",
                ConvertedToPdf.class.getSimpleName(), id, timeStamp, userId, namespace, contractName, getCorrelationId());
    }

}
