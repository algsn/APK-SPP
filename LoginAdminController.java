package sekolah;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginAdminController {

    @FXML
    private TextField fUsername;
    
    @FXML
    private TextField fPassword;
    
    @FXML
    private Button bLogin;
    
    @FXML
    private Button bBack;
    
    //data petugas
    private int id_petugas;
    private String username;
    private String password;
    private String nama_petugas;
    
    @FXML
    private void Login() {
        String Username = fUsername.getText();
        String Password = fPassword.getText();
        
        if (Username.length() != 0 && Password.length() != 0){ //inputan terisi semua
            // Authenticate user and get user data
            Boolean userData = authenticateUser(Username, Password); //memeriksa username dan password benar atau tidak
            
            if (userData == true) { //user ditemukan
                System.out.println("Masoook");
                System.out.println(id_petugas + username + nama_petugas);
                openAdminHome(id_petugas, username, password, nama_petugas);
            } else {//user tidak ditemukan
                showAlert("Login Failed", "Invalid username atau password.");
            }
        } else { // ada inputan yang kosong
            showAlert("Login Failed", "Silahkan isi seluruh inputan.");
        }
        System.out.println("Login Admin");
    }
    
    //    Auth method untuk mencari usernam password yang sama
    private Boolean authenticateUser(String Username, String Password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT * FROM petugas WHERE username = ? AND password = ?"; // query untuk cari username dan password yang sama
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, Username);
                preparedStatement.setString(2, Password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) { //menemukan user
                        id_petugas = resultSet.getInt("id_petugas");
                        username = resultSet.getString("username");
                        password = resultSet.getString("password");
                        nama_petugas = resultSet.getString("nama_petugas");
                        return true;
                    }
                    resultSet.close();
                }
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Authentication failed
    }
    
    private void openAdminHome(Integer id_petugas, String username, String password, String nama_petugas) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataSiswa.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            DataSiswaController siswa = loader.getController();

            // mengirimkan user data ke DataSiswaController
            siswa.setUserData(id_petugas, username, password, nama_petugas);

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bLogin.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void Siswa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginSiswa.fxml"));
            Parent root = loader.load();
            
            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bBack.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
