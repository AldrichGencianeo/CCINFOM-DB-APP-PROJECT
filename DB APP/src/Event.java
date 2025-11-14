public class Event {
    private int eventID;
    private String eventName;
    private String eventType;
    private double bookingFee;

    public Event() {}

    public Event(int eventID, String eventName, String eventType, double bookingFee) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.bookingFee = bookingFee;
    }

    public int getEventID() {

        return eventID;
    }

    public String getEventName() {

        return eventName;
    }

    public String getEventType() {

        return eventType;
    }

    public double getBookingFee() {
        return bookingFee;
    }

    public void setEventID(int eventID) {

        this.eventID = eventID;
    }

    public void setEventName(String eventName) {

        this.eventName = eventName;
    }

    public void setEventType(String eventType) {

        this.eventType = eventType;
    }

    public void setBookingFee(double bookingFee) {

        this.bookingFee = bookingFee;
    }

    @Override
    public String toString() {
        return String.format("Event[ID=%d, Name=%s, Type=%s, Booking Fee=%.2f]",
                eventID, eventName, eventType, bookingFee);
    }
}