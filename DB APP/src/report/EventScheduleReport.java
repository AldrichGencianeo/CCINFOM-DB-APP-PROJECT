package report;

public class EventScheduleReport {
    private int totalEvents;
    private double averageBookingFee;
    private int totalSchedules;
    private double averageSchedulesPerEvent;
    private String timePeriod;
    private int month;
    private int year;

    public EventScheduleReport() {}

    public EventScheduleReport(int totalEvents, double averageBookingFee, int totalSchedules,
                               double averageSchedulesPerEvent, String timePeriod, int month, int year) {
        this.totalEvents = totalEvents;
        this.averageBookingFee = averageBookingFee;
        this.totalSchedules = totalSchedules;
        this.averageSchedulesPerEvent = averageSchedulesPerEvent;
        this.timePeriod = timePeriod;
        this.month = month;
        this.year = year;
    }

    public int getTotalEvents() {
        return totalEvents;
    }

    public double getAverageBookingFee() {
        return averageBookingFee;
    }

    public int getTotalSchedules() {
        return totalSchedules;
    }

    public double getAverageSchedulesPerEvent() {
        return averageSchedulesPerEvent;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setTotalEvents(int totalEvents) {
        this.totalEvents = totalEvents;
    }

    public void setAverageBookingFee(double averageBookingFee) {
        this.averageBookingFee = averageBookingFee;
    }

    public void setTotalSchedules(int totalSchedules) {
        this.totalSchedules = totalSchedules;
    }

    public void setAverageSchedulesPerEvent(double averageSchedulesPerEvent) {
        this.averageSchedulesPerEvent = averageSchedulesPerEvent;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("Report[Period=%s, Events=%d, AvgFee=%.2f, Schedules=%d, AvgSchedules/model.Event=%.2f]",
                timePeriod, totalEvents, averageBookingFee, totalSchedules, averageSchedulesPerEvent);
    }
}