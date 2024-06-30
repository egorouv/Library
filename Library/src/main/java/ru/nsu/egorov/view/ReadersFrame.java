package ru.nsu.egorov.view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReadersFrame extends JFrame {
    private JTextArea resultArea;
    private JComboBox<String> queryComboBox;
    private JTextField[] parameterFields;
    private JLabel[] parameterLabels;

    public ReadersFrame() {
        setTitle("Читатели");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton executeButton = new JButton("Выполнить");
        resultArea = new JTextArea(20, 40);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        queryComboBox = new JComboBox<>(new String[]{
                "Список читателей с заданными характеристиками",
                "Список читателей, на руках у которых находится указанное произведение",
                "Список читателей, на руках у которых находится указанное издание",
                "Список читателей, которые в течение периода получали издание с некоторым произведением",
                "Список читателей, которые в течение периода были обслужены указанным библиотекарем",
                "Список читателей с просроченным сроком литературы",
                "Список читателей, не посещавших библиотеку в течение указанного времени"
        });

        parameterLabels = new JLabel[3];
        parameterFields = new JTextField[3];
        for (int i = 0; i < 3; i++) {
            parameterLabels[i] = new JLabel("Параметр " + (i + 1) + ":");
            parameterFields[i] = new JTextField(20);
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(queryComboBox);

        queryComboBox.addActionListener(e -> updateParameterFields());

        for (int i = 0; i < 3; i++) {
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
        updateParameterFields(); // Initialize parameter fields based on the first query
    }

    private void updateParameterFields() {
        int selectedQueryIndex = queryComboBox.getSelectedIndex();
        switch (selectedQueryIndex) {
            case 0:
                parameterLabels[0].setText("Категория:");
                setParametersVisibility(true, false, false);
                break;
            case 1:
                parameterLabels[0].setText("Произведение:");
                setParametersVisibility(true, false, false);
                break;
            case 2:
                parameterLabels[0].setText("Издание:");
                setParametersVisibility(true, false, false);
                break;
            case 3:
                parameterLabels[0].setText("Дата начала:");
                parameterLabels[1].setText("Дата окончания:");
                parameterLabels[2].setText("Произведение:");
                setParametersVisibility(true, true, true);
                break;
            case 4:
                parameterLabels[0].setText("Библиотекарь ID:");
                parameterLabels[1].setText("Дата начала:");
                parameterLabels[2].setText("Дата окончания:");
                setParametersVisibility(true, true, true);
                break;
            case 5:
                setParametersVisibility(false, false, false);
                break;
            case 6:
                parameterLabels[0].setText("Дата начала:");
                parameterLabels[1].setText("Дата окончания:");
                setParametersVisibility(true, true, false);
                break;
            default:
                break;
        }
    }

    private void setParametersVisibility(boolean p1, boolean p2, boolean p3) {
        parameterLabels[0].setVisible(p1);
        parameterFields[0].setVisible(p1);
        parameterLabels[1].setVisible(p2);
        parameterFields[1].setVisible(p2);
        parameterLabels[2].setVisible(p3);
        parameterFields[2].setVisible(p3);
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
            parameters[i] = parameterFields[i].getText().trim();
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
                case "Список читателей с заданными характеристиками":
                    if (parameters[0].isEmpty()) {
                        queryResult.append("Параметр должен быть заполнен для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория " +
                                    "FROM READERS r " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE c.category = '" + parameters[0] + "'"
                    );
                    break;
                case "Список читателей, на руках у которых находится указанное произведение":
                    if (parameters[0].isEmpty()) {
                        queryResult.append("Параметр должен быть заполнен для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория " +
                                    "FROM READERS r " +
                                    "JOIN ISSUANCE i ON r.id = i.reader " +
                                    "JOIN PUBLICATION p ON i.publication = p.id " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON p.description = pd.id " +
                                    "JOIN PUBLICATION_TITLE pt ON pd.title = pt.id " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE pt.title = '" + parameters[0] + "'"
                    );
                    break;
                case "Список читателей, на руках у которых находится указанное издание":
                    if (parameters[0].isEmpty()) {
                        queryResult.append("Параметр должен быть заполнен для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория " +
                                    "FROM READERS r " +
                                    "JOIN ISSUANCE i ON r.id = i.reader " +
                                    "JOIN PUBLICATION p ON i.publication = p.id " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON p.description = pd.id " +
                                    "JOIN PUBLICATION_TYPE pt ON pd.type = pt.id " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE pt.type = '" + parameters[0] + "'"
                    );
                    break;
                case "Список читателей, которые в течение периода получали издание с некоторым произведением":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty() || parameters[2].isEmpty()) {
                        queryResult.append("Все параметры должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория, pt.title AS Произведение " +
                                    "FROM READERS r " +
                                    "JOIN ISSUANCE i ON r.id = i.reader " +
                                    "JOIN PUBLICATION p ON i.publication = p.id " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON p.description = pd.id " +
                                    "JOIN PUBLICATION_TITLE pt ON pd.title = pt.id " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE i.issuance_date BETWEEN '" + parameters[0] + "' AND '" + parameters[1] + "' " +
                                    "AND pt.title = '" + parameters[2] + "'"
                    );
                    break;
                case "Список читателей, которые в течение периода были обслужены указанным библиотекарем":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty() || parameters[2].isEmpty()) {
                        queryResult.append("Все параметры должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT DISTINCT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория " +
                                    "FROM READERS r " +
                                    "JOIN ISSUANCE i ON r.id = i.reader " +
                                    "JOIN LIBRARY_WORKERS lw ON i.worker = lw.id " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE lw.id = " + parameters[0] + " AND i.issuance_date BETWEEN '" + parameters[1] + "' AND '" + parameters[2] + "'"
                    );
                    break;
                case "Список читателей с просроченным сроком литературы":
                    resultSet = statement.executeQuery(
                            "SELECT DISTINCT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория " +
                                    "FROM READERS r " +
                                    "JOIN ISSUANCE i ON r.id = i.reader " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE i.return_date < CURRENT_DATE AND i.actual_return_date IS NULL"
                    );
                    break;
                case "Список читателей, не посещавших библиотеку в течение указанного времени":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty()) {
                        queryResult.append("Оба параметра должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT r.id, r.reader_name AS Имя, r.reader_surname AS Фамилия, r.birth_date AS Дата_рождения, " +
                                    "l.library_name AS Библиотека, c.category AS Категория " +
                                    "FROM READERS r " +
                                    "JOIN LIBRARY l ON r.library = l.id " +
                                    "JOIN CATEGORY c ON r.category = c.id " +
                                    "WHERE r.id NOT IN (SELECT reader FROM ISSUANCE WHERE issuance_date BETWEEN '" + parameters[0] + "' AND '" + parameters[1] + "')"
                    );
                    break;
                default:
                    queryResult.append("Некорректное название запроса");
            }

            if (resultSet != null) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        queryResult.append(metaData.getColumnLabel(i)).append(": ").append(resultSet.getString(i)).append("\n");
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
        SwingUtilities.invokeLater(ReadersFrame::new);
    }
}
