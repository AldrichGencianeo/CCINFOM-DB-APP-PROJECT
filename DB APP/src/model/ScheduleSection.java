package model;

public class ScheduleSection {
    private int scheduleID;
    private int sectionID;
    private int availableSlots;
    private double price;

    public ScheduleSection() {}

    public ScheduleSection(int scheduleID, int sectionID, int availableSlots, double price) {
        this.scheduleID = scheduleID;
        this.sectionID = sectionID;
        this.availableSlots = availableSlots;
        this.price = price;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public int getSectionID() {
        return sectionID;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public double getPrice() {
        return price;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public void setSectionID(int sectionID) {
        this.sectionID = sectionID;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("ScheduleSection[ScheduleID=%d, SectionID=%d, Slots=%d Price=%.2f]",
                scheduleID, sectionID, availableSlots, price);
    }
}