package ru.nsu.egorov.view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class WorkersFrame extends JFrame {
    private JTextArea resultArea;
    private JComboBox<String> queryComboBox;
    private JTextField[] parameterFields;
    private JLabel[] parameterLabels;

    public WorkersFrame() {
        setTitle("Библиотекари");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton executeButton = new JButton("Выполнить");
        resultArea = new JTextArea(20, 40);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        queryComboBox = new JComboBox<>(new String[]{
                "Получить данные о выработке библиотекарей",
                "Выдать список библиотекарей, работающих в указанном читальном зале"
        });

        parameterLabels = new JLabel[2];
        parameterFields = new JTextField[2];
        for (int i = 0; i < 2; i++) {
            parameterLabels[i] = new JLabel("Параметр " + (i + 1) + ":");
            parameterFields[i] = new JTextField(20);
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(queryComboBox);

        queryComboBox.addActionListener(e -> updateParameterFields());

        for (int i = 0; i < 2; i++) {
            JPanel parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            parameterPanel.add(parameterLabels[i]);
            parameterPanel.add(parameterFields[i]);
            topPanel.add(parameterPanel);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(executeButton);
        topPanel.add(buttonPanel);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        setContentPane(contentPane);
        setLocationRelativeTo(null);

        executeButton.addActionListener(e -> executeQuery());

        setVisible(true);
        updateParameterFields();
    }

    private void updateParameterFields() {
        int selectedQueryIndex = queryComboBox.getSelectedIndex();
        switch (selectedQueryIndex) {
            case 0:
                parameterLabels[0].setVisible(false);
                parameterFields[0].setVisible(false);
                parameterLabels[1].setVisible(false);
                parameterFields[1].setVisible(false);
                break;
            case 1:
                parameterLabels[0].setVisible(true);
                parameterLabels[0].setText("Читательский зал");
                parameterFields[0].setVisible(true);
                parameterLabels[1].setVisible(true);
                parameterLabels[1].setText("Библиотека");
                parameterFields[1].setVisible(true);
                break;
            default:
                break;
        }
    }

    private void executeQuery() {
        String queryName = (String) queryComboBox.getSelectedItem();
        String[] parameters = getParameters();
        String queryResult = executeSomeQuery(queryName, parameters);
        displayResult(queryResult);
    }

    private String[] getParameters() {
        String[] parameters = new String[parameterFields.length];
        for (int i = 0; i < parameterFields.length; i++) {
            parameters[i] = parameterFields[i].getText();
        }
        return parameters;
    }

    private String executeSomeQuery(String queryName, String[] parameters) {
        StringBuilder queryResult = new StringBuilder();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Library", "root", "1234");
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;

            switch (queryName) {
                case "Получить данные о выработке библиотекарей":
                    resultSet = statement.executeQuery("SELECT lw.id AS ID, lw.worker_name AS Имя, lw.worker_surname as Фамилия, lw.birth_date As Дата_рождения, lw.work_place AS Место_работы, COUNT(i.id) AS Выдачи FROM ISSUANCE i JOIN LIBRARY_WORKERS lw ON i.worker = lw.id WHERE i.issuance_date BETWEEN '2023-01-01' AND '2025-01-01' GROUP BY lw.id");
                    break;
                case "Выдать список библиотекарей, работающих в указанном читальном зале":
                    resultSet = statement.executeQuery("SELECT lw.id AS ID, lw.worker_name AS Имя, lw.worker_surname as Фамилия, lw.birth_date As Дата_рождения, lw.work_place AS Место_работы FROM LIBRARY_WORKERS lw JOIN READING_ROOM rr ON lw.work_place = rr.id WHERE rr.room = '" + parameters[0] + "' AND rr.library = '" + parameters[1] + "'");
                    break;
                default:
                    queryResult.append("Invalid query name");
            }

            if (resultSet != null) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        queryResult.append(metaData.getColumnName(i)).append(": ").append(resultSet.getString(i)).append("\n");
                    }
                    queryResult.append("\n");
                }
                resultSet.close();
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryResult.toString();
    }

    private void displayResult(String queryResult) {
        resultArea.setText(queryResult);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WorkersFrame::new);
    }
}
