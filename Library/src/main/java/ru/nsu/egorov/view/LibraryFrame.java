package ru.nsu.egorov.view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LibraryFrame extends JFrame {
    private JTextArea resultArea;
    private JComboBox<String> queryComboBox;
    private JTextField[] parameterFields;
    private JLabel[] parameterLabels;

    public LibraryFrame() {
        setTitle("Библиотека");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton executeButton = new JButton("Выполнить");
        resultArea = new JTextArea(20, 40);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        queryComboBox = new JComboBox<>(new String[]{
                "Выдать список изданий, которые в течение некоторого времени получал указанный читатель",
                "Получить перечень изданий, которыми пользовался указанный читатель из фонда другой библиотеки",
                "Получить список литературы, выданной с определенной полки",
                "Получить перечень литературы, поступившей в течение некоторого периода",
                "Получить перечень литературы, списанной в течение некоторого периода",
                "Получить список инвентарных номеров и названий по указанному произведению",
                "Выдать список инвентарных номеров и названий по указанным автору",
                "Получить список самых популярных произведений"
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
                parameterLabels[0].setText("Читатель ID:");
                parameterLabels[1].setText("Дата начала:");
                parameterLabels[2].setText("Дата окончания:");
                setParametersVisibility(true, true, true);
                break;
            case 1:
                parameterLabels[0].setText("Читатель ID:");
                parameterLabels[1].setText("Дата начала:");
                parameterLabels[2].setText("Дата окончания:");
                setParametersVisibility(true, true, true);
                break;
            case 2:
                parameterLabels[0].setText("Библиотека ID:");
                parameterLabels[1].setText("Полка ID:");
                setParametersVisibility(true, true, false);
                break;
            case 3:
                parameterLabels[0].setText("Дата начала:");
                parameterLabels[1].setText("Дата окончания:");
                setParametersVisibility(true, true, false);
                break;
            case 4:
                parameterLabels[0].setText("Дата начала:");
                parameterLabels[1].setText("Дата окончания:");
                setParametersVisibility(true, true, false);
                break;
            case 5:
                parameterLabels[0].setText("Произведение:");
                setParametersVisibility(true, false, false);
                break;
            case 6:
                parameterLabels[0].setText("Имя автора:");
                parameterLabels[1].setText("Фамилия автора:");
                setParametersVisibility(true, true, false);
                break;
            case 7:
                setParametersVisibility(false, false, false);
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
                case "Выдать список изданий, которые в течение некоторого времени получал указанный читатель":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty() || parameters[2].isEmpty()) {
                        queryResult.append("Все параметры должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT pt.title AS Название " +
                                    "FROM PUBLICATION_TITLE pt " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON pt.id = pd.title " +
                                    "JOIN PUBLICATION p ON pd.id = p.description " +
                                    "JOIN ISSUANCE i ON p.id = i.publication " +
                                    "JOIN READERS r ON i.reader = r.id " +
                                    "JOIN LIBRARY_WORKERS lw ON i.worker = lw.id " +
                                    "JOIN READING_ROOM rr ON lw.work_place = rr.id " +
                                    "WHERE r.id = '" + parameters[0] + "' " +
                                    "AND i.issuance_date BETWEEN '" + parameters[1] + "' AND '" + parameters[2] + "' " +
                                    "AND r.library = rr.library"
                    );
                    break;
                case "Получить перечень изданий, которыми пользовался указанный читатель из фонда другой библиотеки":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty() || parameters[2].isEmpty()) {
                        queryResult.append("Все параметры должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT pt.title AS Название " +
                                    "FROM PUBLICATION_TITLE pt " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON pt.id = pd.title " +
                                    "JOIN PUBLICATION p ON pd.id = p.description " +
                                    "JOIN ISSUANCE i ON p.id = i.publication " +
                                    "JOIN READERS r ON i.reader = r.id " +
                                    "JOIN LIBRARY_WORKERS lw ON i.worker = lw.id " +
                                    "JOIN READING_ROOM rr ON lw.work_place = rr.id " +
                                    "WHERE r.id = '" + parameters[0] + "' " +
                                    "AND i.issuance_date BETWEEN '" + parameters[1] + "' AND '" + parameters[2] + "' " +
                                    "AND r.library <> rr.library"
                    );
                    break;
                case "Получить список литературы, выданной с определенной полки":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty()) {
                        queryResult.append("Все параметры должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT pt.title AS Название " +
                                    "FROM PUBLICATION_TITLE pt " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON pt.id = pd.title " +
                                    "JOIN PUBLICATION p ON pd.id = p.description " +
                                    "JOIN LOCATION_SHELF ls ON p.location = ls.id " +
                                    "JOIN LOCATION_RACK lr ON ls.location_rack = lr.id " +
                                    "JOIN LOCATION_HALL lh ON lr.location_hall = lh.id " +
                                    "WHERE lh.library = '" + parameters[0] + "' " +
                                    "AND ls.location_shelf = '" + parameters[1] + "'"
                    );
                    break;
                case "Получить перечень литературы, поступившей в течение некоторого периода":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty()) {
                        queryResult.append("Оба параметра должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT pt.title AS Название " +
                                    "FROM PUBLICATION_TITLE pt " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON pt.id = pd.title " +
                                    "JOIN PUBLICATION p ON pd.id = p.description " +
                                    "WHERE p.id IN (SELECT publication " +
                                    "FROM REPLENISHMENT " +
                                    "WHERE replenishment_date BETWEEN '" + parameters[0] + "' AND '" + parameters[1] + "')"
                    );
                    break;
                case "Получить перечень литературы, списанной в течение некоторого периода":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty()) {
                        queryResult.append("Оба параметра должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT pt.title AS Название " +
                                    "FROM PUBLICATION_TITLE pt " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON pt.id = pd.title " +
                                    "JOIN PUBLICATION p ON pd.id = p.description " +
                                    "WHERE p.id IN (SELECT publication " +
                                    "FROM WRITE_OFF " +
                                    "WHERE write_off_date BETWEEN '" + parameters[0] + "' AND '" + parameters[1] + "')"
                    );
                    break;
                case "Получить список инвентарных номеров и названий по указанному произведению":
                    if (parameters[0].isEmpty()) {
                        queryResult.append("Параметр должен быть заполнен для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT p.id AS Инвентарный_номер, pt.title AS Название " +
                                    "FROM PUBLICATION p " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON p.description = pd.id " +
                                    "JOIN PUBLICATION_TITLE pt ON pd.title = pt.id " +
                                    "WHERE pt.title = '" + parameters[0] + "'"
                    );
                    break;
                case "Выдать список инвентарных номеров и названий по указанным автору":
                    if (parameters[0].isEmpty() || parameters[1].isEmpty()) {
                        queryResult.append("Оба параметра должны быть заполнены для выполнения этого запроса.");
                        break;
                    }
                    resultSet = statement.executeQuery(
                            "SELECT p.id AS Инвентарный_номер, pt.title AS Название " +
                                    "FROM PUBLICATION p " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON p.description = pd.id " +
                                    "JOIN PUBLICATION_TITLE pt ON pd.title = pt.id " +
                                    "JOIN AUTHORS a ON pd.author = a.id " +
                                    "WHERE a.author_name = '" + parameters[0] + "' " +
                                    "AND a.author_surname = '" + parameters[1] + "'"
                    );
                    break;
                case "Получить список самых популярных произведений":
                    resultSet = statement.executeQuery(
                            "SELECT pt.title AS Название, COUNT(i.id) AS Количество_выдач " +
                                    "FROM ISSUANCE i " +
                                    "JOIN PUBLICATION p ON i.publication = p.id " +
                                    "JOIN PUBLICATION_DESCRIPTION pd ON p.description = pd.id " +
                                    "JOIN PUBLICATION_TITLE pt ON pd.title = pt.id " +
                                    "GROUP BY pt.title " +
                                    "ORDER BY Количество_выдач DESC"
                    );
                    break;
                default:
                    queryResult.append("Неверное название запроса.");
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
            queryResult.append("Ошибка выполнения запроса: ").append(e.getMessage());
        }
        return queryResult.toString();
    }

    private void displayResult(String queryResult) {
        resultArea.setText(queryResult);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryFrame::new);
    }
}
