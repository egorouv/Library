package ru.nsu.egorov.model;

import ru.nsu.egorov.view.AddFrame;
import ru.nsu.egorov.view.LibraryFrame;
import ru.nsu.egorov.view.ReadersFrame;
import ru.nsu.egorov.view.WorkersFrame;

import javax.swing.*;
import java.awt.*;

public class LibraryApp extends JFrame {
    private JButton readersButton;
    private JButton libraryButton;
    private JButton workersButton;
    private JButton addButton;
    private JFrame readersFrame;
    private JFrame libraryFrame;
    private JFrame workersFrame;
    private JFrame addFrame;

    public LibraryApp() {
        setTitle("Библиотека");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));
        setLocationRelativeTo(null);

        readersButton = new JButton("Читатели");
        libraryButton = new JButton("Библиотека");
        workersButton = new JButton("Работники");
        addButton = new JButton("Редактирование");

        readersButton.addActionListener(e -> openReadersFrame());
        libraryButton.addActionListener(e -> openLibraryFrame());
        workersButton.addActionListener(e -> openWorkersFrame());
        addButton.addActionListener(e -> openAddFrame());

        add(readersButton);
        add(libraryButton);
        add(workersButton);
        add(addButton);
    }

    private void openReadersFrame() {
        if (readersFrame == null) {
            readersFrame = new ReadersFrame();
        }
        readersFrame.setVisible(true);
    }

    private void openLibraryFrame() {
        if (libraryFrame == null) {
            libraryFrame = new LibraryFrame();
        }
        libraryFrame.setVisible(true);
    }

    private void openWorkersFrame() {
        if (workersFrame == null) {
            workersFrame = new WorkersFrame();
        }
        workersFrame.setVisible(true);
    }

    private void openAddFrame() {
        if (addFrame == null) {
            addFrame = new AddFrame();
        }
        addFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryApp app = new LibraryApp();
            app.setVisible(true);
        });
    }
}
