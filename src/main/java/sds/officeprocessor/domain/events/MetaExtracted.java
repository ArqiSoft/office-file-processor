package sds.officeprocessor.domain.events;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import sds.officeprocessor.domain.models.Property;
import com.sds.storage.Guid;
import java.util.List;
import sds.messaging.contracts.AbstractContract;

public class MetaExtracted extends AbstractContract {

    private String bucket;
    private UUID blobId;
    private List<Property> Meta;
    private UUID id;
    private String timeStamp;
    private UUID userId;


    public MetaExtracted() {
        namespace = "Sds.OfficeProcessor.Domain.Events";
        contractName = MetaExtracted.class.getSimpleName();
    }


    public void setBucket(String Bucket) {
        this.bucket = Bucket;
    }

    public void setBlobId(UUID BlobId) {
        this.blobId = BlobId;
    }


    /**
     * @return the id
     */
    @JsonProperty("Id")
    public UUID getId() {
        return id;
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

    @JsonProperty("Bucket")
    public String getBucket() {
        return bucket;
    }

    @JsonProperty("BlobId")
    public UUID getBlobId() {
        return blobId;
    }

    public List<Property> getMeta() {
        return Meta;
    }

    public void setMeta(List<Property> Meta) {
        this.Meta = Meta;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s [id=%s, timeStamp=%s, userId=%s, namespace=%s, contractName=%s, correlationId=%s]",
                MetaExtracted.class.getSimpleName(), id, timeStamp, userId, namespace, contractName, getCorrelationId());
    }

}
