package gui;

import service.BillingService;
import model.Bill;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportPanel extends JPanel {

    private BillingService billingService;

    // Components
    private JButton loadReportButton;
    private JTextArea reportArea;

    public ReportPanel(BillingService billingService) {
        this.billingService = billingService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel with button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadReportButton = new JButton("Load Report");
        loadReportButton.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(loadReportButton);

        add(topPanel, BorderLayout.NORTH);

        // Create report area
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        // Add button listener
        loadReportButton.addActionListener(e -> loadReport());

        // Load initial report
        loadReport();
    }

    private void loadReport() {
        try {
            List<Bill> bills = billingService.getAllBills();

            StringBuilder report = new StringBuilder();
            report.append("==============================================================\n");
            report.append("                    BILLING REPORTS\n");
            report.append("==============================================================\n\n");

            if (bills.isEmpty()) {
                report.append("No bills found in the system.\n");
            } else {
                report.append(String.format("Total Bills: %d\n\n", bills.size()));
                report.append("--------------------------------------------------------------\n");
                report.append(String.format("%-8s %-20s %-15s %-15s\n",
                    "Bill ID", "Date", "Total Amount", "Payment"));
                report.append("--------------------------------------------------------------\n");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                double grandTotal = 0.0;

                for (Bill bill : bills) {
                    report.append(String.format("%-8d %-20s Rs. %-11.2f %-15s\n",
                        bill.getBillId(),
                        bill.getDate().format(formatter),
                        bill.getTotalAmount(),
                        bill.getPaymentMethod()));

                    grandTotal += bill.getTotalAmount();
                }

                report.append("--------------------------------------------------------------\n");
                report.append(String.format("%-28s Rs. %-11.2f\n", "GRAND TOTAL:", grandTotal));
                report.append("==============================================================\n");
            }

            reportArea.setText(report.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading report: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}