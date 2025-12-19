package main;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    BorderPane bp;
    GridPane gp;
    Label lblName, lblId, lblDept, lblSalary;
    TextField tfName, tfId, tfDept, tfSalary;
    ArrayList<Employee> employeeList = new ArrayList<>();
    TableView<Employee> tableEmployee;
    Scene scene;
    Button addBtn, updateBtn, deleteBtn;
    Connect connect;

    public static void main(String[] args) {
        launch(args);
    }

    public void init() {
        bp = new BorderPane();
        gp = new GridPane();
        scene = new Scene(bp, 900, 600);

        gp.setPadding(new Insets(20));
        gp.setHgap(10);
        gp.setVgap(20);

        lblName = new Label("Name: ");
        tfName = new TextField();
        gp.add(lblName, 0, 0);
        gp.add(tfName, 1, 0);

        lblId = new Label("ID: ");
        tfId = new TextField();
        gp.add(lblId, 3, 0);
        gp.add(tfId, 4, 0);

        lblDept = new Label("Department: ");
        tfDept = new TextField();
        gp.add(lblDept, 0, 1);
        gp.add(tfDept, 1, 1);

        lblSalary = new Label("Salary: ");
        tfSalary = new TextField();
        gp.add(lblSalary, 3, 1);
        gp.add(tfSalary, 4, 1);

        tableEmployee = new TableView<>();

        TableColumn<Employee, String> colName = new TableColumn<Employee, String>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
        colName.setMinWidth(bp.getWidth() / 4);

        TableColumn<Employee, Integer> colId = new TableColumn<Employee, Integer>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        colId.setMinWidth(bp.getWidth() / 4);

        TableColumn<Employee, String> colDept = new TableColumn<Employee, String>("Department");
        colDept.setCellValueFactory(new PropertyValueFactory<Employee, String>("department"));
        colDept.setMinWidth(bp.getWidth() / 4);

        TableColumn<Employee, Double> colSalary = new TableColumn<Employee, Double>("Salary");
        colSalary.setCellValueFactory(new PropertyValueFactory<Employee, Double>("salary"));
        colSalary.setMinWidth(bp.getWidth() / 4);

        tableEmployee.getColumns().addAll(colName, colId, colDept, colSalary);

        bp.setCenter(tableEmployee);

        addBtn = new Button("Add Employee");
        updateBtn = new Button("Update Employee");
        deleteBtn = new Button("Delete Employee");
        
        gp.add(addBtn, 0, 3);
        gp.add(updateBtn, 2, 3);
        gp.add(deleteBtn, 3, 3);

        addBtn.setOnAction(e -> {
            if (!tfName.getText().isEmpty() && !tfId.getText().isEmpty() 
                && !tfDept.getText().isEmpty() && !tfSalary.getText().isEmpty()) {

                connect = Connect.getInstance();

                try {
                    int id = Integer.parseInt(tfId.getText());
                    String name = tfName.getText();
                    String dept = tfDept.getText();
                    double salary = Double.parseDouble(tfSalary.getText());

                    String query = String.format(
                        "INSERT INTO employee (id, name, department, salary) VALUES (%d, '%s', '%s', %f)",
                        id, name, dept, salary);

                    connect.executeUpdate(query);
                    getData();

                    tfName.clear();
                    tfId.clear();
                    tfDept.clear();
                    tfSalary.clear();

                } catch (NumberFormatException ex) {
                    System.out.println("ID and Salary must be numbers");
                }
            }
        });

        // Event tombol Update
        updateBtn.setOnAction(e -> {
            if (!tfName.getText().isEmpty() && !tfId.getText().isEmpty() && !tfDept.getText().isEmpty()
                    && !tfSalary.getText().isEmpty()) {
                connect = Connect.getInstance();

                try {
                    int id = Integer.parseInt(tfId.getText());
                    String name = tfName.getText();
                    String dept = tfDept.getText();
                    double salary = Double.parseDouble(tfSalary.getText());

                    String query = String.format("UPDATE employee SET name = '%s', department = '%s', salary = %f WHERE id = %d",
                            name, dept, salary, id);
                    connect.executeUpdate(query);
                    getData();

                    tfName.clear();
                    tfId.clear();
                    tfDept.clear();
                    tfSalary.clear();
                } catch (NumberFormatException ex) {
                    System.out.println("ID and Salary must be numbers");
                }
            }
        });

        // Event tombol Delete
        deleteBtn.setOnAction(e -> {
            if (!tfId.getText().isEmpty()) {
                connect = Connect.getInstance();

                try {
                    int id = Integer.parseInt(tfId.getText());
                    String query = String.format("DELETE FROM employee WHERE id = %d", id);
                    connect.executeUpdate(query);
                    getData();

                    tfName.clear();
                    tfId.clear();
                    tfDept.clear();
                    tfSalary.clear();
                } catch (NumberFormatException ex) {
                    System.out.println("ID must be a number");
                }
            }
        });
        
        tableEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tfName.setText(newSelection.getName());
                tfId.setText(String.valueOf(newSelection.getId()));
                tfDept.setText(newSelection.getDepartment());
                tfSalary.setText(String.valueOf(newSelection.getSalary()));
            }
        });
        
        bp.setTop(gp);

        getData();
    }

    public void getData() {
        employeeList.clear();
        connect = Connect.getInstance();
        String query = "SELECT * FROM employee";

        connect.rs = connect.executeQuery(query);

        try {
            while (connect.rs.next()) {
                int id = connect.rs.getInt("id");
                String name = connect.rs.getString("name");
                String dept = connect.rs.getString("department");
                double salary = connect.rs.getDouble("salary");

                Employee emp = new Employee(id, name, dept, salary);
                employeeList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<Employee> obsEmployee = FXCollections.observableArrayList(employeeList);
        tableEmployee.setItems(obsEmployee);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Employee Management System");
        primaryStage.show();
    }
}