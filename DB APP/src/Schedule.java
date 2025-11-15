import java.sql.Date;
import java.sql.Time;

public class Schedule {
    private int scheduleID;
    private int eventID;
    private Date scheduleDate;
    private Time startTime;
    private Time endTime;

    public Schedule() {}

    public Schedule(int scheduleID, int eventID, Date scheduleDate, Time startTime, Time endTime) {
        this.scheduleID = scheduleID;
        this.eventID = eventID;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public int getEventID() {
        return eventID;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("Schedule[ID=%d, EventID=%d, Date=%s, Start=%s, End=%s]",
                scheduleID, eventID, scheduleDate, startTime, endTime);
    }
}