public class CommonConfiguration {
    private Integer numberOfPreferredNeighbors;
    private String unchokingInterval;
    private String optimisticUnchokingInterval;
    private String fileName;
    private Integer fileSize;
    private Integer pieceSize;

    private CommonConfiguration(String numberOfPreferredNeighbors, String unchokingInterval, String optimisticUnchokingInterval, String fileName, Integer fileSize, Integer pieceSize) {
        this.numberOfPreferredNeighbors = Integer.parseInt(numberOfPreferredNeighbors);
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }

    public Integer getNumberOfPreferredNeighbors() {
        return numberOfPreferredNeighbors;
    }

    public String getUnchokingInterval() {
        return unchokingInterval;
    }

    public String getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public Integer getPieceSize() {
        return pieceSize;
    }

    public static final class CommonConfigurationBuilder {
        private String numberOfPreferredNeighbors;
        private String unchokingInterval;
        private String optimisticUnchokingInterval;
        private String fileName;
        private Integer fileSize;
        private Integer pieceSize;

        private CommonConfigurationBuilder() {
        }

        public static CommonConfigurationBuilder builder() {
            return new CommonConfigurationBuilder();
        }

        public CommonConfigurationBuilder withNumberOfPreferredNeighbors(String numberOfPreferredNeighbors) {
            this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
            return this;
        }

        public CommonConfigurationBuilder withUnchokingInterval(String unchokingInterval) {
            this.unchokingInterval = unchokingInterval;
            return this;
        }

        public CommonConfigurationBuilder withOptimisticUnchokingInterval(String optimisticUnchokingInterval) {
            this.optimisticUnchokingInterval = optimisticUnchokingInterval;
            return this;
        }

        public CommonConfigurationBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public CommonConfigurationBuilder withFileSize(Integer fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public CommonConfigurationBuilder withPieceSize(Integer pieceSize) {
            this.pieceSize = pieceSize;
            return this;
        }

        public CommonConfiguration build() {
            return new CommonConfiguration(numberOfPreferredNeighbors, unchokingInterval, optimisticUnchokingInterval, fileName, fileSize, pieceSize);
        }
    }
}
