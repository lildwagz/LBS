package util;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    public static class DatePicker {
        private JPanel panel;
        private JTextField dateField;

        public DatePicker() {
            panel = new JPanel(new BorderLayout(5, 5));
            dateField = new JTextField(10);
            JButton pickButton = new JButton("...");

            pickButton.addActionListener(this::showPickerDialog);

            panel.add(dateField, BorderLayout.CENTER);
            panel.add(pickButton, BorderLayout.EAST);
        }

        private void showPickerDialog(ActionEvent e) {
            JDialog dialog = new JDialog();
            dialog.setModal(true);
            dialog.setTitle("Pilih Tanggal");

            JPanel spinnerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            LocalDate initialDate = getDate();
            if (initialDate == null) {
                initialDate = LocalDate.now();
            }

            // Model untuk spinner tahun, bulan, dan hari
            SpinnerNumberModel yearModel = new SpinnerNumberModel(
                    initialDate.getYear(), 1900, 2100, 1);
            SpinnerNumberModel monthModel = new SpinnerNumberModel(
                    initialDate.getMonthValue(), 1, 12, 1);
            SpinnerNumberModel dayModel = new SpinnerNumberModel(
                    initialDate.getDayOfMonth(), 1, initialDate.lengthOfMonth(), 1);

            JSpinner yearSpinner = new JSpinner(yearModel);
            JSpinner monthSpinner = new JSpinner(monthModel);
            JSpinner daySpinner = new JSpinner(dayModel);

            // Listener untuk update hari saat tahun/bulan berubah
            ChangeListener updateListener = changeEvent -> updateDayModel(
                    (Integer) yearModel.getNumber(),
                    (Integer) monthModel.getNumber(),
                    dayModel
            );

            yearModel.addChangeListener(updateListener);
            monthModel.addChangeListener(updateListener);

            spinnerPanel.add(new JLabel("Tahun:"));
            spinnerPanel.add(yearSpinner);
            spinnerPanel.add(new JLabel("Bulan:"));
            spinnerPanel.add(monthSpinner);
            spinnerPanel.add(new JLabel("Hari:"));
            spinnerPanel.add(daySpinner);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(ev -> {
                try {
                    int year = (Integer) yearSpinner.getValue();
                    int month = (Integer) monthSpinner.getValue();
                    int day = (Integer) daySpinner.getValue();

                    LocalDate selectedDate = LocalDate.of(year, month, day);
                    dateField.setText(formatForDisplay(selectedDate));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "Tanggal tidak valid!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                dialog.dispose();
            });

            JButton cancelButton = new JButton("Batal");
            cancelButton.addActionListener(ev -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(spinnerPanel, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setContentPane(contentPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(panel);
            dialog.setVisible(true);
        }

        private void updateDayModel(int year, int month, SpinnerNumberModel dayModel) {
            LocalDate tempDate = LocalDate.of(year, month, 1);
            int maxDay = tempDate.lengthOfMonth();
            dayModel.setMaximum(maxDay);
            if ((Integer) dayModel.getValue() > maxDay) {
                dayModel.setValue(maxDay);
            }
        }

        public JComponent getComponent() {
            return panel;
        }

        public LocalDate getDate() {
            try {
                return LocalDate.parse(
                        dateField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                );
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        public void clearDate() {
            dateField.setText("");
        }
    }

    public static DatePicker createDatePicker() {
        return new DatePicker();
    }

    public static boolean isValidRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) return true;
        return !start.isAfter(end);
    }

    public static String formatForDisplay(LocalDate date) {
        if (date == null) return "-";
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}