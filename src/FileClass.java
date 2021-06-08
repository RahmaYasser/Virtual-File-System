import java.io.Serializable;
import java.util.ArrayList;

public class FileClass implements Serializable {
    private String name;
    private String storageDetails;
    private int allocationType;
    private ArrayList<Integer> indexes;


    public ArrayList<Integer> getIndexes() {
        return indexes;
    }

    public void setIndexes(ArrayList<Integer> indexes) {
        this.indexes = indexes;
    }

    public FileClass() {
        indexes = new ArrayList<>();
    }

    public FileClass(String name, String storageDetails, int allocationType) {
        this.name = name;
        this.storageDetails = storageDetails;
        this.allocationType = allocationType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStorageDetails(String storageDetails) {
        this.storageDetails = storageDetails;
    }

    public void setAllocationType(int allocationType) {
        this.allocationType = allocationType;
    }

    public String getName() {
        return name;
    }

    public String getStorageDetails() {
        return storageDetails;
    }

    public int getAllocationType() {
        return allocationType;
    }
}
