package gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import dao.ReportDAO;
import report.EventScheduleReport;
import report.MerchSalesReport;
import repository.MerchReceiptRepoImpl;
import repository.MerchRepoImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReportsScene {
    private BorderPane root;
    private Connection connection;
    private TextArea reportDisplay;

    public ReportsScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        root = new BorderPane();

        HBox backButton = SceneUtils.createBackButton(mainMenu, connection);
        root.setTop(backButton);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        Label title = new Label("Event & Schedule Report Generator");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox reportSelection = createReportSelectionSection();

        VBox reportArea = createReportDisplayArea();

        mainContent.getChildren().addAll(title, reportSelection, reportArea);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
    }

    private VBox createReportSelectionSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 15;");

        Label header = new Label("Generate Report");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        VBox monthlySection = new VBox(10);
        monthlySection.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        Label monthlyLabel = new Label("Monthly Report");
        monthlyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox monthlyControls = new HBox(10);
        monthlyControls.setAlignment(Pos.CENTER_LEFT);

        Label lblMonth = new Label("Month:");
        ComboBox<Integer> cmbMonth = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cmbMonth.getItems().add(i);
        }
        cmbMonth.setPromptText("Select Month");
        cmbMonth.setPrefWidth(120);

        Label lblYear = new Label("Year:");
        ComboBox<Integer> cmbYear = new ComboBox<>();
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            cmbYear.getItems().add(i);
        }
        cmbYear.setValue(currentYear);
        cmbYear.setPrefWidth(100);

        Button btnGenerateMonthly = new Button("Generate Monthly Report");
        btnGenerateMonthly.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");

        monthlyControls.getChildren().addAll(lblMonth, cmbMonth, lblYear, cmbYear, btnGenerateMonthly);
        monthlySection.getChildren().addAll(monthlyLabel, monthlyControls);

        VBox yearlySection = new VBox(10);
        yearlySection.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        Label yearlyLabel = new Label("Yearly Report");
        yearlyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox yearlyControls = new HBox(10);
        yearlyControls.setAlignment(Pos.CENTER_LEFT);

        Label lblYearOnly = new Label("Year:");
        ComboBox<Integer> cmbYearOnly = new ComboBox<>();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            cmbYearOnly.getItems().add(i);
        }
        cmbYearOnly.setValue(currentYear);
        cmbYearOnly.setPrefWidth(100);

        Button btnGenerateYearly = new Button("Generate Yearly Report");
        btnGenerateYearly.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");

        yearlyControls.getChildren().addAll(lblYearOnly, cmbYearOnly, btnGenerateYearly);
        yearlySection.getChildren().addAll(yearlyLabel, yearlyControls);

        VBox periodsSection = new VBox(10);
        periodsSection.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        Label periodsLabel = new Label("View Available Periods");
        periodsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button btnViewPeriods = new Button("Show Available Periods");
        btnViewPeriods.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");

        periodsSection.getChildren().addAll(periodsLabel, btnViewPeriods);

        btnGenerateMonthly.setOnAction(e -> {
            Integer month = cmbMonth.getValue();
            Integer year = cmbYear.getValue();

            if (month == null || year == null) {
                showWarning("Please select both month and year");
                return;
            }

            generateMonthlyReport(month, year);
        });

        btnGenerateYearly.setOnAction(e -> {
            Integer year = cmbYearOnly.getValue();

            if (year == null) {
                showWarning("Please select a year");
                return;
            }

            generateYearlyReport(year);
        });

        btnViewPeriods.setOnAction(e -> viewAvailablePeriods());

        // ================= MERCH REPORT SECTION =======================
        VBox merchSection = new VBox(10);
        merchSection.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        Label merchLabel = new Label("🛒 Merchandise Sales Report");
        merchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox merchControls = new HBox(10);
        merchControls.setAlignment(Pos.CENTER_LEFT);

        // Per Event
        Label lblEvent = new Label("Event ID:");
        TextField txtEventID = new TextField();
        txtEventID.setPromptText("ex: 101");
        txtEventID.setPrefWidth(120);

        Button btnEventReport = new Button("Generate Per Event");
        btnEventReport.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 8 15;");

        // Per Month
        Label lblMonth2 = new Label("Month:");
        ComboBox<Integer> cmbMonth2 = new ComboBox<>();
        for (int i = 1; i <= 12; i++) cmbMonth2.getItems().add(i);
        cmbMonth2.setPrefWidth(80);

        Label lblYear2 = new Label("Year:");
        ComboBox<Integer> cmbYear2 = new ComboBox<>();
        int currYear = java.time.Year.now().getValue();
        for (int i = currYear - 5; i <= currYear + 1; i++) cmbYear2.getItems().add(i);
        cmbYear2.setValue(currYear);
        cmbYear2.setPrefWidth(100);

        Button btnMonthlyMerch = new Button("Generate Monthly Merch");
        btnMonthlyMerch.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 8 15;");

        merchControls.getChildren().addAll(lblEvent, txtEventID, btnEventReport, lblMonth2, cmbMonth2, lblYear2, cmbYear2, btnMonthlyMerch);
        merchSection.getChildren().addAll(merchLabel, merchControls);

        // Event Handler for Per Event Report
        btnEventReport.setOnAction(e -> {
            try {
                if (txtEventID.getText().isEmpty()) {
                    showWarning("Please enter an Event ID.");
                    return;
                }

                int eventID = Integer.parseInt(txtEventID.getText());
                ReportDAO dao = new ReportDAO(connection, new MerchReceiptRepoImpl(connection), new MerchRepoImpl(connection));
                List<MerchSalesReport> list = dao.generateReportPerEvent(eventID);

                String title = "MERCH SALES REPORT (EVENT ID: " + eventID + ")";
                reportDisplay.setText(formatMerchReport(title, list));

            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter a valid Event ID.");
            }
        });

        // Event Handler for Monthly Merch Report
        btnMonthlyMerch.setOnAction(e -> {
            Integer month = cmbMonth2.getValue();
            Integer year = cmbYear2.getValue();

            if (month == null || year == null) {
                showWarning("Please select both month and year.");
                return;
            }

            ReportDAO dao = new ReportDAO(connection, new MerchReceiptRepoImpl(connection), new MerchRepoImpl(connection));
            List<MerchSalesReport> list = dao.generateReportPerMonth(year, month);

            String title = "MERCH SALES REPORT (" + getMonthName(month) + " " + year + ")";
            reportDisplay.setText(formatMerchReport(title, list));
        });

        VBox ticketSection = new VBox(10);
        ticketSection.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        Label ticketLabel = new Label("🎟 Ticket Sales Analytics");
        ticketLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox ticketControls = new HBox(10);
        ticketControls.setAlignment(Pos.CENTER_LEFT);

        Label lblMonth3 = new Label("Month:");
        ComboBox<Integer> cmbMonth3 = new ComboBox<>();
        for (int i = 1; i <= 12; i++) cmbMonth3.getItems().add(i);
        cmbMonth3.setPrefWidth(80);

        Label lblYear3 = new Label("Year:");
        ComboBox<Integer> cmbYear3 = new ComboBox<>();
        for (int i = currYear - 5; i <= currYear + 1; i++) cmbYear3.getItems().add(i);
        cmbYear3.setValue(currYear);
        cmbYear3.setPrefWidth(100);

        Button btnDailyTicket = new Button("Daily Report");
        Button btnWeeklyTicket = new Button("Weekly Report");
        Button btnMonthlyTicket = new Button("Monthly Report");

        btnDailyTicket.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 8 15;");
        btnWeeklyTicket.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 8 15;");
        btnMonthlyTicket.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 8 15;");

        ticketControls.getChildren().addAll(lblMonth3, cmbMonth3, lblYear3, cmbYear3, btnDailyTicket, btnWeeklyTicket, btnMonthlyTicket);
        ticketSection.getChildren().addAll(ticketLabel, ticketControls);

        btnDailyTicket.setOnAction(e -> {
            Integer month = cmbMonth3.getValue();
            Integer year = cmbYear3.getValue();
            if (month == null || year == null) {
                showWarning("Please select Month and Year for Daily Report.");
                return;
            }
            try {
                ReportDAO dao = new ReportDAO(connection, null, null);
                List<report.TicketSalesReport> list = dao.generateDailyTicketReport(year, month);
                String reportTitle = "DAILY TICKET SALES - " + getMonthName(month) + " " + year;
                reportDisplay.setText(formatTicketReport(reportTitle, list));
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
            }
        });

        btnWeeklyTicket.setOnAction(e -> {
            Integer year = cmbYear3.getValue();
            if (year == null) {
                showWarning("Please select a Year for Weekly Report.");
                return;
            }
            try {
                ReportDAO dao = new ReportDAO(connection, null, null);
                List<report.TicketSalesReport> list = dao.generateWeeklyTicketReport(year);
                String reportTitle = "WEEKLY TICKET SALES - YEAR " + year;
                reportDisplay.setText(formatTicketReport(reportTitle, list));
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
            }
        });

        btnMonthlyTicket.setOnAction(e -> {
            Integer year = cmbYear3.getValue();
            if (year == null) {
                showWarning("Please select a Year for Monthly Report.");
                return;
            }
            try {
                ReportDAO dao = new ReportDAO(connection, null, null);
                List<report.TicketSalesReport> list = dao.generateMonthlyTicketReport(year);
                String reportTitle = "MONTHLY TICKET SALES - YEAR " + year;
                reportDisplay.setText(formatTicketReport(reportTitle, list));
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
            }
        });

        section.getChildren().addAll(header, monthlySection, yearlySection, periodsSection, merchSection, ticketSection);

        return section;
    }

    private VBox createReportDisplayArea() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #95a5a6; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 15;");

        Label header = new Label("Report Output");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        reportDisplay = new TextArea();
        reportDisplay.setEditable(false);
        reportDisplay.setPrefHeight(400);
        reportDisplay.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        reportDisplay.setText("Select a report type above to generate...");

        Button btnClear = new Button("Clear Report");
        btnClear.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> reportDisplay.setText("Select a report type above to generate..."));

        Button btnExport = new Button("Export to Text File");
        btnExport.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold;");
        btnExport.setOnAction(e -> exportReport());

        HBox buttonBox = new HBox(10, btnClear, btnExport);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        section.getChildren().addAll(header, reportDisplay, buttonBox);
        return section;
    }

    private void generateMonthlyReport(int month, int year) {
        try {
            ReportDAO dao = new ReportDAO(connection, null, null);
            EventScheduleReport report = dao.generateMonthlyReport(month, year);

            if (report == null || report.getTotalEvents() == 0) {
                reportDisplay.setText(formatNoDataMessage(month, year));
                return;
            }

            String monthName = getMonthName(month);
            StringBuilder output = new StringBuilder();

            output.append("╔════════════════════════════════════════════════════════════════════╗\n");
            output.append("║          EVENT & SCHEDULE REPORT - MONTHLY ANALYSIS                ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append(String.format("║ Period: %-58s ║\n", monthName + " " + year));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                         EVENT METRICS                              ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append(String.format("║ Total Number of Events:          %-33d ║\n", report.getTotalEvents()));
            output.append(String.format("║ Average Booking Fee:             ₱%-32.2f ║\n", report.getAverageBookingFee()));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                       SCHEDULE METRICS                             ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append(String.format("║ Total Number of Schedules:       %-33d ║\n", report.getTotalSchedules()));
            output.append(String.format("║ Average Schedules per Event:     %-33.2f ║\n", report.getAverageSchedulesPerEvent()));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                         SUMMARY                                    ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");

            if (report.getTotalEvents() > 0) {
                output.append("║ ✓ Events were scheduled during this period                         ║\n");

                if (report.getAverageSchedulesPerEvent() >= 2.0) {
                    output.append("║ ✓ Good scheduling frequency per event                              ║\n");
                } else if (report.getAverageSchedulesPerEvent() >= 1.0) {
                    output.append("║ ⚠ Moderate scheduling frequency                                    ║\n");
                } else {
                    output.append("║ ⚠ Low scheduling frequency per event                               ║\n");
                }

                if (report.getAverageBookingFee() > 5000) {
                    output.append("║ ✓ Premium pricing tier (Avg > ₱5,000)                              ║\n");
                } else if (report.getAverageBookingFee() > 2000) {
                    output.append("║ ○ Standard pricing tier (Avg ₱2,000-5,000)                         ║\n");
                } else {
                    output.append("║ ○ Budget pricing tier (Avg < ₱2,000)                               ║\n");
                }
            }

            output.append("╚════════════════════════════════════════════════════════════════════╝\n");
            output.append("\nReport Generated: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            reportDisplay.setText(output.toString());

        } catch (SQLException ex) {
            showError("Report Generation Failed", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void generateYearlyReport(int year) {
        try {
            ReportDAO dao = new ReportDAO(connection, null, null);
            EventScheduleReport report = dao.generateYearlyReport(year);

            if (report == null || report.getTotalEvents() == 0) {
                reportDisplay.setText(formatNoDataMessageYear(year));
                return;
            }

            StringBuilder output = new StringBuilder();

            output.append("╔════════════════════════════════════════════════════════════════════╗\n");
            output.append("║          EVENT & SCHEDULE REPORT - YEARLY ANALYSIS                 ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append(String.format("║ Period: Year %-54d ║\n", year));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                         EVENT METRICS                              ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append(String.format("║ Total Number of Events:          %-33d ║\n", report.getTotalEvents()));
            output.append(String.format("║ Average Booking Fee:             ₱%-32.2f ║\n", report.getAverageBookingFee()));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                       SCHEDULE METRICS                             ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append(String.format("║ Total Number of Schedules:       %-33d ║\n", report.getTotalSchedules()));
            output.append(String.format("║ Average Schedules per Event:     %-33.2f ║\n", report.getAverageSchedulesPerEvent()));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                    ANNUAL PERFORMANCE                              ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");

            double avgEventsPerMonth = report.getTotalEvents() / 12.0;
            double avgSchedulesPerMonth = report.getTotalSchedules() / 12.0;

            output.append(String.format("║ Average Events per Month:        %-33.2f ║\n", avgEventsPerMonth));
            output.append(String.format("║ Average Schedules per Month:     %-33.2f ║\n", avgSchedulesPerMonth));
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                         SUMMARY                                    ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");

            if (report.getTotalEvents() >= 50) {
                output.append("║ ✓ High activity year (50+ events)                                  ║\n");
            } else if (report.getTotalEvents() >= 20) {
                output.append("║ ✓ Moderate activity year (20-49 events)                            ║\n");
            } else if (report.getTotalEvents() > 0) {
                output.append("║ ○ Low activity year (<20 events)                                   ║\n");
            }

            if (avgEventsPerMonth >= 4.0) {
                output.append("║ ✓ Consistent monthly event scheduling                              ║\n");
            } else if (avgEventsPerMonth >= 2.0) {
                output.append("║ ○ Moderate monthly event frequency                                 ║\n");
            } else {
                output.append("║ ⚠ Sparse event scheduling throughout the year                      ║\n");
            }

            output.append("╚════════════════════════════════════════════════════════════════════╝\n");
            output.append("\nReport Generated: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            reportDisplay.setText(output.toString());

        } catch (SQLException ex) {
            showError("Report Generation Failed", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String formatMerchReport(String title, List<MerchSalesReport> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("╔════════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║ %62s ║\n", title));
        sb.append("╠════════════════════════════════════════════════════════════════════╣\n");

        if (list == null || list.isEmpty()) {
            sb.append("║ No merchandise sales found for this period.                        ║\n");
            sb.append("╚════════════════════════════════════════════════════════════════════╝\n");
            return sb.toString();
        }

        sb.append("║  ID   | MERCH NAME                     | SOLD | REVENUE    | STOCK ║\n");
        sb.append("╠═══════╬════════════════════════════════╬══════╬════════════╬═══════╣\n");

        for (MerchSalesReport r : list) {
            sb.append(String.format(
                    "║ %-5d | %-30s | %-4d | ₱%-9.2f | %-5d ║\n",
                    r.getMerchandiseID(),
                    r.getName(),
                    r.getSold(),
                    r.getRevenue(),
                    r.getRemainingStock()
            ));
        }

        sb.append("╚════════════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    private void viewAvailablePeriods() {
        try {
            ReportDAO dao = new ReportDAO(connection, null, null);
            List<String> months = dao.getAvailableMonths();
            List<Integer> years = dao.getAvailableYears();

            StringBuilder output = new StringBuilder();

            output.append("╔════════════════════════════════════════════════════════════════════╗\n");
            output.append("║              AVAILABLE REPORTING PERIODS                           ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                    AVAILABLE MONTHS                                ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");

            if (months.isEmpty()) {
                output.append("║ No schedule data available in the database.                        ║\n");
            } else {
                for (String monthStr : months) {
                    output.append(String.format("║ • %-64s ║\n", monthStr));
                }
            }

            output.append("╠════════════════════════════════════════════════════════════════════╣\n");
            output.append("║                     AVAILABLE YEARS                                ║\n");
            output.append("╠════════════════════════════════════════════════════════════════════╣\n");

            if (years.isEmpty()) {
                output.append("║ No schedule data available in the database.                        ║\n");
            } else {
                StringBuilder yearLine = new StringBuilder("║ Years: ");
                for (int i = 0; i < years.size(); i++) {
                    yearLine.append(years.get(i));
                    if (i < years.size() - 1) {
                        yearLine.append(", ");
                    }
                }
                while (yearLine.length() < 69) {
                    yearLine.append(" ");
                }
                yearLine.append("║");
                output.append(yearLine).append("\n");
            }

            output.append("╚════════════════════════════════════════════════════════════════════╝\n");
            output.append("\nTip: Use the periods above to generate monthly or yearly reports.");

            reportDisplay.setText(output.toString());

        } catch (SQLException ex) {
            showError("Failed to Load Available Periods", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String formatNoDataMessage(int month, int year) {
        String monthName = getMonthName(month);
        return String.format("""
            ╔════════════════════════════════════════════════════════════════════╗
            ║                      NO DATA AVAILABLE                             ║
            ╠════════════════════════════════════════════════════════════════════╣
            ║ Period: %-58s ║
            ╠════════════════════════════════════════════════════════════════════╣
            ║ No events or schedules found for this period.                      ║
            ║                                                                    ║
            ║ Suggestions:                                                       ║
            ║ • Check if events have been added to the system                    ║
            ║ • Verify that schedules are associated with events                 ║
            ║ • Try a different month or year                                    ║
            ║ • Click "Show Available Periods" to see valid date ranges          ║
            ╚════════════════════════════════════════════════════════════════════╝
            """, monthName + " " + year);
    }

    private String formatNoDataMessageYear(int year) {
        return String.format("""
            ╔════════════════════════════════════════════════════════════════════╗
            ║                      NO DATA AVAILABLE                             ║
            ╠════════════════════════════════════════════════════════════════════╣
            ║ Period: Year %-54d ║
            ╠════════════════════════════════════════════════════════════════════╣
            ║ No events or schedules found for this year.                        ║
            ║                                                                    ║
            ║ Suggestions:                                                       ║
            ║ • Check if events have been added to the system                    ║
            ║ • Verify that schedules are associated with events                 ║
            ║ • Try a different year                                             ║
            ║ • Click "Show Available Periods" to see valid date ranges          ║
            ╚════════════════════════════════════════════════════════════════════╝
            """, year);
    }

    private void exportReport() {
        if (reportDisplay.getText().isEmpty() ||
                reportDisplay.getText().equals("Select a report type above to generate...")) {
            showWarning("No report to export. Please generate a report first.");
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Export Report");
        fileChooser.setInitialFileName("event_schedule_report_" +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        java.io.File file = fileChooser.showSaveDialog(root.getScene().getWindow());

        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(reportDisplay.getText());
                showSuccess("Report exported successfully to:\n" + file.getAbsolutePath());
            } catch (java.io.IOException ex) {
                showError("Export Failed", "Could not save file: " + ex.getMessage());
            }
        }
    }

    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return months[month];
    }

    public BorderPane getRoot() {
        return root;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String formatTicketReport(String title, List<report.TicketSalesReport> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("╔════════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║ %-66s ║\n", title));
        sb.append("╠════════════════════════════════════════════════════════════════════╣\n");

        if (list == null || list.isEmpty()) {
            sb.append("║ No ticket sales found for this period.                             ║\n");
            sb.append("╚════════════════════════════════════════════════════════════════════╝\n");
            return sb.toString();
        }

        sb.append("║ PERIOD (Day/Week/Month) | TICKETS SOLD | AVG PRICE  | REVENUE      ║\n");
        sb.append("╠═════════════════════════╬══════════════╬════════════╬══════════════╣\n");

        double grandTotalRevenue = 0;
        int grandTotalTickets = 0;

        for (report.TicketSalesReport r : list) {
            String periodLabel = "";
            
            if (title.contains("DAILY")) {
                periodLabel = "Day " + r.getDay();
            } else if (title.contains("WEEKLY")) {
                periodLabel = "Week " + r.getDay(); 
            } else if (title.contains("MONTHLY")) {
                periodLabel = getMonthName(r.getMonth());
            }

            sb.append(String.format(
                    "║ %-23s | %-12d | ₱%-9.2f | ₱%-11.2f ║\n",
                    periodLabel,
                    r.getTotalTicketsSold(),
                    r.getAveragePrice(),
                    r.getTotalRevenue()
            ));

            grandTotalRevenue += r.getTotalRevenue();
            grandTotalTickets += r.getTotalTicketsSold();
        }

        sb.append("╠═════════════════════════╬══════════════╬════════════╬══════════════╣\n");
        sb.append(String.format("║ TOTALS                  | %-12d |            | ₱%-11.2f ║\n", grandTotalTickets, grandTotalRevenue));
        sb.append("╚═════════════════════════╩══════════════╩════════════╩══════════════╝\n");

        return sb.toString();
    }
}