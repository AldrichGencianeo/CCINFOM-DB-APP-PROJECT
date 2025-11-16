package model;

public class ScheduleSection {
    private int scheduleID;
    private int sectionID;
    private double price;

    public ScheduleSection() {}

    public ScheduleSection(int scheduleID, int sectionID, double price) {
        this.scheduleID = scheduleID;
        this.sectionID = sectionID;
        this.price = price;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public int getSectionID() {
        return sectionID;
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

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("model.ScheduleSection[ScheduleID=%d, SectionID=%d, Price=%.2f]",
                scheduleID, sectionID, price);
    }
}