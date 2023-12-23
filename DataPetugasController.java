package sekolah;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DataPetugasController{
     
    // Atribut FXML       
    @FXML
    private Label lWelcome;
    @FXML
    private Button bLogout;
    @FXML
    private Button bSiswa;
    @FXML
    private Button bPetugas;
    @FXML
    private Button bKelas;
    @FXML
    private Button bSpp;
    @FXML
    private Button bPembayaran;
    @FXML
    private Button bHistory;
    
    @FXML
    private TextField tPetugasSearch;
    
    @FXML
    private TextField tIdPetugasAdd;
    @FXML
    private TextField tUsernameAdd;
    @FXML
    private TextField tPasswordAdd;
    @FXML
    private TextField tNamaPetugasAdd;
    
    @FXML
    private TextField tUsernameEdit;
    @FXML
    private TextField tPasswordEdit;
    @FXML
    private TextField tNamaPetugasEdit;
    
    @FXML
    private Button bCariPetugas;
    @FXML
    private Button bTambahPetugas;
    @FXML
    private Button bEditPetugas;
    @FXML
    private Button bDeletePetugas;
    
    
    
    @FXML
    private TableView<PetugasData> tvDataPetugas;

    @FXML
    private TableColumn<PetugasData, String> cIdPetugas;
    @FXML
    private TableColumn<PetugasData, String> cUsername;
    @FXML
    private TableColumn<PetugasData, String> cPassword;
    @FXML
    private TableColumn<PetugasData, String> cNamaPetugas;
    
    
    
    //data petugas
    private int id_petugas;
    private String username;
    private String password;
    private String nama_petugas;
    
    // variabel untuk konfirmasi popup
    private boolean continueProcess;
    
    // memberikan nilai pada data petugas, dan membuat kalimat penyapa pada label lWelcome
    void setUserData(Integer Id_petugas, String Username, String Password, String Nama_petugas) {
        id_petugas = Id_petugas;
        username = Username;
        password = Password;
        nama_petugas = Nama_petugas;
        
        lWelcome.setText("Hallo, " + nama_petugas);
    }
    
    // method inisiasi ketika membuka view data petugas
    @FXML
    private void initialize() {
        cIdPetugas.setCellValueFactory(new PropertyValueFactory<>("idPetugas"));
        cUsername.setCellValueFactory(new PropertyValueFactory<>("Username"));
        cPassword.setCellValueFactory(new PropertyValueFactory<>("Password"));
        cNamaPetugas.setCellValueFactory(new PropertyValueFactory<>("NamaPetugas"));
        
        loadDataFromDatabase(); // mengambil data dari database
    }
    
    // method untuk mengambil data petugas
    private void loadDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT id_petugas, username, password, nama_petugas FROM petugas"; // query mengambil data petugas
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ObservableList<PetugasData> data = FXCollections.observableArrayList(); // menyiapkan array untuk menampung data
                    while (resultSet.next()) { // perulanagan untuk mengambil seluruh baris hasil query
                        String id_petugas = String.valueOf(resultSet.getInt("id_petugas"));
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String nama_petugas = resultSet.getString("nama_petugas");
                        data.add(new PetugasData(id_petugas, username, password, nama_petugas)); // menambahkan data kedalam array data
                    }
                    tvDataPetugas.setItems(data); // menambahhkan data kedalam table view
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void searchPetugas() {
        String keyword = tPetugasSearch.getText().toLowerCase();
        if (keyword.length() > 0){
            loadDataFromDatabase();
            // Get the original data from the TableView
            ObservableList<PetugasData> originalData = tvDataPetugas.getItems();

            // Create a filtered list to store the search results
            ObservableList<PetugasData> filteredData = FXCollections.observableArrayList();

            // Perform the search
            for (PetugasData petugas : originalData) {
                if (petugas.getIdPetugas().toLowerCase().contains(keyword) || petugas.getUsername().toLowerCase().contains(keyword) || petugas.getPassword().toLowerCase().contains(keyword) || petugas.getNamaPetugas().toLowerCase().contains(keyword)) {
                    filteredData.add(petugas);
                }
            }

            // Update the TableView with the search results
            tvDataPetugas.setItems(filteredData);
        }else{
            loadDataFromDatabase();
        }
    }

    // method add data
    @FXML
    private void addPetugas(){
        int IDPetugas;
        try{
            IDPetugas = Integer.parseInt(tIdPetugasAdd.getText()); //memanggil TextField ID Petugas
        } catch (NumberFormatException e) {
            IDPetugas = 0;
        }
        String petugasText = tIdPetugasAdd.getText();
        String UsernamePetugas = tUsernameAdd.getText();
        String PasswordPetugas = tPasswordAdd.getText();
        String NamaPetugas = tNamaPetugasAdd.getText();
        
        if (petugasText.length() != 0 && UsernamePetugas.length() != 0 && PasswordPetugas.length() != 0 && NamaPetugas.length() != 0){ //seluruh inputan terisi
            boolean isExist = isExist(IDPetugas); //memeriksa ID Petugas yang dimasukkan terdaftar atau tidak
            if (isExist == false){ // jika terdaftar
                boolean successRegist = addDataPetugas(IDPetugas, UsernamePetugas, PasswordPetugas, NamaPetugas); //menyimpan data registrasi ke database
                if (successRegist) {
                    showAlert("Register Sucess", "Data Petugas telah terbentuk.");
                    tIdPetugasAdd.setText(null);
                    tUsernameAdd.setText(null);
                    tPasswordAdd.setText(null);
                    tNamaPetugasAdd.setText(null);
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Tambah Petugas Failed", "Sesuatu error.");
                }
            }else{ //jika tidak terdaftar
                showAlert("Tambah Petugas Failed", "ID Petugas telah terdaftar.");
            }
        }else{ // ada field yang kosong
            //mengosongkan input field
            tIdPetugasAdd.setText(null);
            tUsernameAdd.setText(null);
            tPasswordAdd.setText(null);
            tNamaPetugasAdd.setText(null);
            
            showAlert("Tambah Petugas Failed", "Silahkan isi seluruh inputan.");
        }
    }
    
    //    method untuk memeriksa ID Petugas yang dimasukkan terdaftar atau tidak
    private boolean isExist(Integer Id_petugas){
        boolean isExist = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT id_petugas FROM petugas WHERE id_petugas = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_petugas);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        isExist = true; //found same Petugas
                    }
                    resultSet.close();
                }
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isExist;
    }
    
    
    // menyimpan data ke table petugas
    private boolean addDataPetugas(Integer Id_petugas, String Username, String Password, String Nama_petugas){
        boolean isStored = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "INSERT INTO petugas (id_petugas, username, password, nama_petugas) VALUES (?, ?, ?, ?)"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_petugas);
                preparedStatement.setString(2, Username);
                preparedStatement.setString(3, Password);
                preparedStatement.setString(4, Nama_petugas);
                
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    isStored = true; //Data saved successfully!
                } else {
                    isStored = false; //Failed to save data.
                }
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isStored;
    }
    
    // method edit data
    @FXML
    private void editPetugas(){
        PetugasData selectedItem = tvDataPetugas.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
                int IDPetugas;
                try{
                    IDPetugas = Integer.parseInt(selectedItem.getIdPetugas()); //memanggil TextField ID Petugas
                } catch (NumberFormatException e) {
                    IDPetugas = 0;
                }
                String UsernamePetugas = tUsernameEdit.getText();
                String PasswordPetugas = tPasswordEdit.getText();
                String NamaPetugas = tNamaPetugasEdit.getText();
                
                if (UsernamePetugas.length() != 0 && PasswordPetugas.length() != 0 && NamaPetugas.length() != 0){ //seluruh inputan terisi
                    boolean successEdit = editDataPetugas(IDPetugas, UsernamePetugas, PasswordPetugas, NamaPetugas); //menyimpan data ke database
                    if (successEdit) {
                        showAlert("Edit Data Sucess", "Data Petugas telah diperbarui.");
                        loadDataFromDatabase(); // mengambil data dari database
                    } else {
                        showAlert("Edit Petugas Failed", "Sesuatu error.");
                    }
                    //mengosongkan field inputan
                    tUsernameEdit.setText(null);
                    tPasswordEdit.setText(null);
                    tNamaPetugasEdit.setText(null);
                }else{ // ada field yang kosong
                    showAlert("Edit Data Failed", "Silahkan isi seluruh inputan.");
                }
        }else{
            showAlert("Edit Data Failed", "Silahkan pilih data yang ingin diedit.");
        }
    }
    
    // menyimpan data ke table petugas
    private boolean editDataPetugas(Integer Id_petugas, String Username, String Password, String Nama_petugas){
        boolean isEdited = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "UPDATE petugas SET username = ?, password = ?, nama_petugas = ? WHERE id_petugas = ?"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, Username);
                preparedStatement.setString(2, Password);
                preparedStatement.setString(3, Nama_petugas);
                preparedStatement.setInt(4, Id_petugas);
                
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    isEdited = true; //Data saved successfully!
                } else {
                    isEdited = false; //Failed to save data.
                }
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isEdited;
    }
    
    // method delete data
    @FXML
    private void deletePetugas(){
        PetugasData selectedItem = tvDataPetugas.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
                int IDPetugas;
                try{
                    IDPetugas = Integer.parseInt(selectedItem.getIdPetugas()); //memanggil TextField ID Petugas
                } catch (NumberFormatException e) {
                    IDPetugas = 0;
                }
                Boolean isDeleted = deleteDataPetugas(IDPetugas);
                if (isDeleted == true){
                    showAlert("Delete Petugas Success", "Data Petugas Berhasil Dihapus.");
                    tvDataPetugas.getItems().remove(selectedItem);
                } else {
                    showAlert("Delete Petugas Failed", "Silahkan coba kembali.");
                }
        }else{
            showAlert("Delete Data Failed", "Silahkan pilih data yang ingin dihapus.");
        }
    }
    
    //    method untuk menghapus Data
    private boolean deleteDataPetugas(Integer Id_petugas){
        boolean isDeleted = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "DELETE FROM petugas WHERE id_petugas = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_petugas);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    isDeleted = true; //Data saved successfully!
                } else {
                    isDeleted = false; //Failed to save data.
                }
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isDeleted;
    }
    
    
    //    data class untuk menyimpan data petugas
    public static class PetugasData {
        private String idPetugas;
        private String Username;
        private String Password;
        private String NamaPetugas;

        public PetugasData(String idPetugas, String Username, String Password, String NamaPetugas) {
            this.idPetugas = idPetugas;
            this.Username = Username;
            this.Password = Password;
            this.NamaPetugas = NamaPetugas;
        }
        public String getIdPetugas() {
            return idPetugas;
        }

        public String getUsername() {
            return Username;
        }

        public String getPassword() {
            return Password;
        }

        public String getNamaPetugas() {
            return NamaPetugas;
        }
    }
    
    // method untuk logout, action dari button logout
    @FXML
    private void Logout() {
        showAlertWarning("Warning", "Apakah yakin untuk logout?");
        if (continueProcess) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginAdmin.fxml")); // memanggil view login bagi admin
                Parent root = loader.load();

                // Create a new stage and set the scene
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                // Close the login window
                Stage loginStage = (Stage) bLogout.getScene().getWindow();
                loginStage.close();

                // Show the new stage
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // method untuk membuka menu data siswa 
    @FXML
    private void openDataSiswa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataSiswa.fxml")); //membuka view data siswa
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            DataSiswaController siswa = loader.getController();

            // mengirimkan user data ke DataSiswaController
            siswa.setUserData(id_petugas, username, password, nama_petugas);

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bSiswa.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //method untuk membuka menu data kelas
    @FXML
    private void openDataKelas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataKelas.fxml")); //memanggil view data kelas
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            DataKelasController kelas = loader.getController();

            // mengirimkan user data ke DataKelasController
            kelas.setUserData(id_petugas, username, password, nama_petugas);

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bSiswa.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // method untuk membuka menu data spp
    @FXML
    private void openDataSPP() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataSPP.fxml")); // memanggil view data spp
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            DataSPPController spp = loader.getController();

            // mengirimkan user data ke DataSPPController
            spp.setUserData(id_petugas, username, password, nama_petugas);

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bSiswa.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //method untuk membuka menu pembayaran
    @FXML
    private void openPembayaran() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PembayaranAdmin.fxml")); //memanggil view pembayaran
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            PembayaranAdminController pembayaran = loader.getController();

            // mengirimkan user data ke PembayaranAdminController
            pembayaran.setUserData(id_petugas, username, password, nama_petugas);

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bSiswa.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //method untuk membuka menu history pembayaran
    @FXML
    private void openHistoryPembayaran() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistoryPembayaranAdmin.fxml")); //memanggil view history pembayaran
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            HistoryPembayaranAdminController history = loader.getController();

            // mengirimkan user data ke HistoryPembayaranAdminController
            history.setUserData(id_petugas, username, password, nama_petugas);

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close the login window
            Stage loginStage = (Stage) bSiswa.getScene().getWindow();
            loginStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // method untuk alert warning
    private void showAlertWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isCancelButton()) {
                // User clicked the close button (X)
                continueProcess = false;
            } else {
                // User clicked OK or another button
                continueProcess = true;
            }
        });
    }
     //method untuk alert information
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

