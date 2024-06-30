package ru.nsu.egorov.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class AddFrame extends JFrame {
    private JComboBox<String> tableComboBox;
    private JPanel inputPanel;
    private JTextField[] inputFields;
    private JTextField idField; // Поле для ID
    private JTable table;
    private DefaultTableModel tableModel;
    private Vector<String> tableNames = new Vector<>();

    private Connection connection;

    public AddFrame() {
        setTitle("Добавить, удалить или редактировать запись");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize the database connection
//        try {
//            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Library", "root", "1234");
//            loadTriggersAndProcedures();
//        } catch (SQLException | IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Ошибка подключения к базе данных: " + e.getMessage());
//            System.exit(1);
//        }

        String[] tables = {"READERS", "ISSUANCE", "PUBLICATION", "LIBRARY_WORKERS",
                "LIBRARY", "CATEGORY", "READING_ROOM", "PUBLICATION_TITLE", "AUTHORS",
                "PUBLICATION_TYPE", "PUBLICATION_DESCRIPTION", "LOCATION_HALL",
                "LOCATION_RACK", "LOCATION_SHELF", "REPLENISHMENT", "WRITE_OFF",
                "SCHOOLBOY", "STUDENT", "TEACHER", "RESEARCHER", "WORKER"};
        tableComboBox = new JComboBox<>(tables);
        tableComboBox.addActionListener(e -> {
            updateInputFields();
            updateTableData();
        });

        inputPanel = new JPanel(new GridBagLayout());
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        JButton addButton = new JButton("Добавить");
        addButton.addActionListener(e -> {
            addRecord();
            updateTableData();
        });

        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(e -> {
            deleteRecord();
            updateTableData();
        });

        JButton editButton = new JButton("Редактировать");
        editButton.addActionListener(e -> {
            editRecord();
            updateTableData();
        });

        idField = new JTextField(10);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tableComboBox, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(new JLabel("ID для удаления/редактирования:"));
        buttonPanel.add(idField);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        updateInputFields();
        updateTableData();
    }

//    private void loadTriggersAndProcedures() throws IOException, SQLException {
//        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\source\\Library\\Library\\src\\main\\resources\\database\\triggers.sql"))) {
//            StringBuilder sqlBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sqlBuilder.append(line).append("\n");
//            }
//            try (Statement stmt = connection.createStatement()) {
//                stmt.execute(sqlBuilder.toString());
//            }
//        }
//    }

    private void updateInputFields() {
        inputPanel.removeAll();

        String selectedTable = (String) tableComboBox.getSelectedItem();
        String[] fieldNames = getFieldNamesForTable(selectedTable);

        inputFields = new JTextField[fieldNames.length];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        for (int i = 0; i < fieldNames.length; i++) {
            JLabel label = new JLabel(fieldNames[i] + ":");
            inputFields[i] = new JTextField(20);

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weighty = 0;
            inputPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.weighty = 0;
            inputPanel.add(inputFields[i], gbc);
        }

        // Add a filler component to take up any remaining space
        gbc.gridx = 0;
        gbc.gridy = fieldNames.length;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        inputPanel.add(new JPanel(), gbc);

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void updateTableData() {
        String selectedTable = (String) tableComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Library", "root", "1234");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + selectedTable)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                data.add(row);
            }

            tableModel.setDataVector(data, columnNames);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при обновлении данных таблицы: " + e.getMessage());
        }
    }

    private void addRecord() {
        String selectedTable = (String) tableComboBox.getSelectedItem();
        String[] fieldNames = getFieldNamesForTable(selectedTable);

        StringBuilder query = new StringBuilder("INSERT INTO " + selectedTable + " (");
        for (int i = 0; i < fieldNames.length; i++) {
            query.append(fieldNames[i]);
            if (i < fieldNames.length - 1) {
                query.append(", ");
            }
        }
        query.append(") VALUES (");
        for (int i = 0; i < inputFields.length; i++) {
            query.append("?");
            if (i < inputFields.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Library", "root", "1234");
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < inputFields.length; i++) {
                setPreparedStatementValue(preparedStatement, i + 1, fieldNames[i], inputFields[i].getText());
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Запись добавлена успешно!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении записи: " + e.getMessage());
        }
    }

    private void deleteRecord() {
        String selectedTable = (String) tableComboBox.getSelectedItem();
        String id = idField.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите ID для удаления записи.");
            return;
        }

        String query = "DELETE FROM " + selectedTable + " WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Library", "root", "1234");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(id));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Запись удалена успешно!");
            } else {
                JOptionPane.showMessageDialog(this, "Запись с указанным ID не найдена!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при удалении записи: " + e.getMessage());
        }
    }

    private void editRecord() {
        String selectedTable = (String) tableComboBox.getSelectedItem();
        String id = idField.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите ID для редактирования записи.");
            return;
        }

        String[] fieldNames = getFieldNamesForTable(selectedTable);
        StringBuilder query = new StringBuilder("UPDATE " + selectedTable + " SET ");
        for (int i = 0; i < fieldNames.length; i++) {
            query.append(fieldNames[i]).append(" = ?");
            if (i < fieldNames.length - 1) {
                query.append(", ");
            }
        }
        query.append(" WHERE id = ?");

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Library", "root", "1234");
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < inputFields.length; i++) {
                setPreparedStatementValue(preparedStatement, i + 1, fieldNames[i], inputFields[i].getText());
            }
            preparedStatement.setInt(inputFields.length + 1, Integer.parseInt(id));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Запись обновлена успешно!");
            } else {
                JOptionPane.showMessageDialog(this, "Запись с указанным ID не найдена!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при обновлении записи: " + e.getMessage());
        }
    }

    private String[] getFieldNamesForTable(String tableName) {
        switch (tableName) {
            case "READERS":
                return new String[]{"reader_name", "reader_surname", "birth_date", "library", "category"};
            case "ISSUANCE":
                return new String[]{"reader", "worker", "publication", "issuance_date", "return_date", "actual_return_date"};
            case "PUBLICATION":
                return new String[]{"location", "description", "allow_take", "write_off"};
            case "LIBRARY_WORKERS":
                return new String[]{"worker_name", "worker_surname", "birth_date", "work_place"};
            case "LIBRARY":
                return new String[]{"library_name", "address"};
            case "CATEGORY":
                return new String[]{"category"};
            case "READING_ROOM":
                return new String[]{"library", "room"};
            case "PUBLICATION_TITLE":
                return new String[]{"title"};
            case "AUTHORS":
                return new String[]{"author_name", "author_surname"};
            case "PUBLICATION_TYPE":
                return new String[]{"type"};
            case "PUBLICATION_DESCRIPTION":
                return new String[]{"title", "author", "type"};
            case "LOCATION_HALL":
                return new String[]{"library", "location_hall"};
            case "LOCATION_RACK":
                return new String[]{"location_hall", "location_rack"};
            case "LOCATION_SHELF":
                return new String[]{"location_rack", "location_shelf"};
            case "REPLENISHMENT":
                return new String[]{"replenishment_date", "library_worker", "publication"};
            case "WRITE_OFF":
                return new String[]{"write_off_date", "library_worker", "publication"};
            case "SCHOOLBOY":
                return new String[]{"id", "school", "class"};
            case "STUDENT":
                return new String[]{"id", "university", "course", "student_group"};
            case "TEACHER":
                return new String[]{"id", "school", "subject"};
            case "RESEARCHER":
                return new String[]{"id", "university", "faculty"};
            case "WORKER":
                return new String[]{"id", "work_place"};
            default:
                throw new IllegalArgumentException("Неизвестная таблица: " + tableName);
        }
    }

    private void setPreparedStatementValue(PreparedStatement preparedStatement, int parameterIndex, String fieldName, String fieldValue) throws SQLException {
        switch (fieldName) {
            case "reader":
            case "worker":
            case "publication":
            case "library":
            case "room":
            case "category":
            case "course":
            case "description":
            case "work_place":
            case "location":
            case "library_worker":
                preparedStatement.setInt(parameterIndex, Integer.parseInt(fieldValue));
                break;
            case "issuance_date":
            case "return_date":
            case "actual_return_date":
            case "birth_date":
            case "replenishment_date":
            case "write_off_date":
                preparedStatement.setDate(parameterIndex, Date.valueOf(fieldValue));
                break;
            case "allow_take":
            case "write_off":
                preparedStatement.setBoolean(parameterIndex, Boolean.parseBoolean(fieldValue));
                break;
            default:
                preparedStatement.setString(parameterIndex, fieldValue);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddFrame::new);
    }
}


