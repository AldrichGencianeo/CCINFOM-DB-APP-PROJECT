public class Section {
    private int sectionID;
    private String sectionName;
    private int capacity;

    public Section() {}

    public Section(int sectionID, String sectionName, int capacity) {
        this.sectionID = sectionID;
        this.sectionName = sectionName;
        this.capacity = capacity;
    }

    public int getSectionID() { 
        return sectionID; 
    }
    
    public String getSectionName() { 
        return sectionName; 
    }
    
    public int getCapacity() { 
        return capacity; 
    }

    public void setSectionID(int sectionID) {
        this.sectionID = sectionID;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format("Section[ID=%d, Name=%s, Capacity=%d]",
                sectionID, sectionName, capacity);
    }
}