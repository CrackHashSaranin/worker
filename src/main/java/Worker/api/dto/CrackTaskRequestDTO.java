package Worker.api.dto;

public class CrackTaskRequestDTO {
    private String requestId;
    private String hash;
    private int maxLength;
    private int partNumber;
    private int partCount;

    public String getRequestId() { return requestId; }
    public String getHash() { return hash; }
    public int getMaxLength() { return maxLength; }
    public int getPartNumber() { return partNumber; }
    public int getPartCount() { return partCount; }

    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setHash(String hash) { this.hash = hash; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }
    public void setPartNumber(int partNumber) { this.partNumber = partNumber; }
    public void setPartCount(int partCount) { this.partCount = partCount; }
}
