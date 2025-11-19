package report;

public class TicketSalesReport {
    private int totalTicketsSold;
    private double totalRevenue;
    private double averagePrice;
    private String timePeriod;
    private int day;
    private int month;
    private int year;

    public TicketSalesReport() {}

    public TicketSalesReport(int totalTicketsSold, double totalRevenue, double averagePrice,
                             String timePeriod, int day, int month, int year) {
        this.totalTicketsSold = totalTicketsSold;
        this.totalRevenue = totalRevenue;
        this.averagePrice = averagePrice;
        this.timePeriod = timePeriod;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getTotalTicketsSold() { 
        return totalTicketsSold; 
    }

    public double getTotalRevenue() { 
        return totalRevenue;
    }

    public double getAveragePrice() { 
        return averagePrice; 
    }

    public String getTimePeriod() { 
        return timePeriod; 
    }

    public int getDay() { 
        return day; 
    }

    public int getMonth() { 
        return month; 
    }

    public int getYear() { 
        return year; 
    }

    public void setTotalTicketsSold(int totalTicketsSold) { 
        this.totalTicketsSold = totalTicketsSold; 
    }

    public void setTotalRevenue(double totalRevenue) { 
        this.totalRevenue = totalRevenue; 
    }

    public void setAveragePrice(double averagePrice) { 
        this.averagePrice = averagePrice; 
    }

    public void setTimePeriod(String timePeriod) { 
        this.timePeriod = timePeriod; 
    }

    public void setDay(int day) { 
        this.day = day; 
    }

    public void setMonth(int month) { 
        this.month = month; 
    }

    public void setYear(int year) { 
        this.year = year; 
    }

    @Override
    public String toString() {
        return String.format("TicketSalesReport[Period=%s, Date=%02d/%02d/%04d, TotalSold=%d, AvgPrice=%.2f, Revenue=%.2f]",
                timePeriod, day, month, year, totalTicketsSold, averagePrice, totalRevenue);
    }
}
