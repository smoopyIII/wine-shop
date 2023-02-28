package com.example.progettodbfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ShowWineByNameFXController  implements Initializable{

    private static final String DBURL = "jdbc:mysql://127.0.0.1:3306/wineshop?";
    private static final String ARGS = "createDatabaseIfNotExist=true&serverTimezone=UTC";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "";
    ObservableList<Wine> w = FXCollections.observableArrayList();

    @FXML
    public TableColumn <Wine, Integer> cId;
    @FXML
    public TableColumn <Wine, String> cName;
    @FXML
    public TableColumn <Wine, String> cProducer;
    @FXML
    public TableColumn <Wine, Integer> cOrigin;
    @FXML
    public TableColumn <Wine, Integer> cYear;
    @FXML
    public TableColumn <Wine, Integer> cAvailability;
    @FXML
    public TableColumn <Wine, String> cTecnicalNotes;
    @FXML
    public TableColumn <Wine, String> cGrape;
    @FXML
    public TableColumn <Wine, Float> cPrice;
    @FXML
    public TableView <Wine> date_textfield;

    @FXML
    public TextField search_wine_by_name_textfield;
    @FXML
    public Button src_wine_by_name_btn;
    @FXML
    private Button account_btn;
    @FXML
    private Label client_name_label;
    @FXML
    private ComboBox Date_ComboBox;

    MainFXController mfxc = new MainFXController();
    int clientId = mfxc.clientID;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        MainFXController mfxc = new MainFXController();
        int clientId = mfxc.clientID;

        Socket socket = null;
        try {
            socket = mfxc.getSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        out.println("GET_CLIENT_USERNAME");
        out.println(clientId);
        try {
            client_name_label.setVisible(true);
            client_name_label.setText(in.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObservableList<String> year = FXCollections.observableArrayList();
        int i;
        year.add("null");
        for(i=1990;i<2023;i++){
            year.add(Integer.toString(i));
        }
        Date_ComboBox.setItems(year);


    }


    public void buttonClick(ActionEvent actionEvent){

        try (Connection conn = DriverManager.getConnection(DBURL + ARGS , LOGIN, PASSWORD);
             Statement stmt = conn.createStatement();)
        {
            w.clear();
            cId.setCellValueFactory(new PropertyValueFactory<Wine,Integer>("id"));
            cName.setCellValueFactory(new PropertyValueFactory<Wine,String>("name"));
            cProducer.setCellValueFactory(new PropertyValueFactory<Wine,String>("producer"));
            cOrigin.setCellValueFactory(new PropertyValueFactory<Wine,Integer>("origin"));
            cYear.setCellValueFactory(new PropertyValueFactory<Wine,Integer>("year"));
            cTecnicalNotes.setCellValueFactory(new PropertyValueFactory<Wine,String>("technicalNotes"));
            cGrape.setCellValueFactory(new PropertyValueFactory<Wine,String>("grapes"));
            cPrice.setCellValueFactory(new PropertyValueFactory<Wine,Float>("price"));
            cAvailability.setCellValueFactory(new PropertyValueFactory<Wine,Integer>("availability"));

            Socket socket = mfxc.getSocket();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            out.println("SHOW_WINES_BY_NAME");
            out.println(search_wine_by_name_textfield.getText());
            out.println(Date_ComboBox.getValue());
            System.out.println("Year selected: " + Date_ComboBox.getValue());
            System.out.println("Wine selected: " + search_wine_by_name_textfield.getText());

            String line;

            while (!(line = in.readLine()).equals("null")) {
                String[] lineList = line.split("/");
                int id = Integer.parseInt(lineList[0]);
                String namewine = lineList[1];
                String producer =lineList[2];
                String origin = lineList[3];
                String year = lineList[4];
                String technicalNotes = lineList[5];
                String grapes = lineList[6];
                float price =Float.parseFloat(lineList[7]);
                int availability = Integer.parseInt(lineList[8]);

                w.add(new Wine(id,namewine,producer,origin,(Integer.parseInt(year)),grapes,technicalNotes,price,availability));
            }
            date_textfield.setItems(w);

        }catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }

    public Button logout_btn;
    public void logoutIsClicked(ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("Login_page.fxml"));
        Stage window = (Stage) logout_btn.getScene().getWindow();
        window.setScene(new Scene(fxmlLoader.load()));
    }

    @FXML
    public Button alarm_btn;
    public void alarmIsClicked(ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("View_proposal.fxml"));
        Stage window = (Stage) alarm_btn.getScene().getWindow();
        window.setScene(new Scene(fxmlLoader.load()));
    }

    public void mailIsClicked(ActionEvent actionEvent) {
        //FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("Notifica.fxml"));
        //Stage window = (Stage) mail_btn.getScene().getWindow();
        //window.setScene(new Scene(fxmlLoader.load()));
    }

    public void accountIsClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("ClientData_page.fxml"));
        Stage window = (Stage) account_btn.getScene().getWindow();
        window.setScene(new Scene(fxmlLoader.load()));
    }
    public Button home_btn;
    public void homeIsClicked(ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("Home_client.fxml"));
        Stage window = (Stage) home_btn.getScene().getWindow();
        window.setScene(new Scene(fxmlLoader.load()));
    }
}