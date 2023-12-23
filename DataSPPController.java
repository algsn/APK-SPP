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

public class DataSPPController{
     
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
    private TextField tSPPSearch;
    
    @FXML
    private TextField tIdSPPAdd;
    @FXML
    private TextField tTahunAdd;
    @FXML
    private TextField tNominalAdd;
    
    @FXML
    private TextField tTahunEdit;
    @FXML
    private TextField tNominalEdit;
    
    @FXML
    private Button bCariSPP;
    @FXML
    private Button bTambahSPP;
    @FXML
    private Button bEditSPP;
    @FXML
    private Button bDeleteSPP;
    
    @FXML
    private TableView<SPPData> tvDataSPP;

    @FXML
    private TableColumn<SPPData, String> cIdSPP;
    @FXML
    private TableColumn<SPPData, String> cTahun;
    @FXML
    private TableColumn<SPPData, String> cNominal;
    
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
    
    // method inisiasi ketika membuka view data SPP
    @FXML
    private void initialize() {
        cIdSPP.setCellValueFactory(new PropertyValueFactory<>("idSPP"));
        cTahun.setCellValueFactory(new PropertyValueFactory<>("tahun"));
        cNominal.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        
        loadDataFromDatabase(); // mengambil data dari database
    }
    
    // method untuk mengambil data SPP
    private void loadDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT id_spp, tahun, nominal FROM spp"; // query mengambil data SPP
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ObservableList<DataSPPController.SPPData> data = FXCollections.observableArrayList(); // menyiapkan array untuk menampung data
                    while (resultSet.next()) { // perulanagan untuk mengambil seluruh baris hasil query
                        String id_spp = String.valueOf(resultSet.getInt("id_spp"));
                        String tahun = resultSet.getString("tahun");
                        String nominal = resultSet.getString("nominal");
                        data.add(new DataSPPController.SPPData(id_spp, tahun, nominal)); // menambahkan data kedalam array data
                    }
                    tvDataSPP.setItems(data); // menambahhkan data kedalam table view
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void searchSPP() {
        String keyword = tSPPSearch.getText().toLowerCase();
        if (keyword.length() > 0){
            loadDataFromDatabase();
            // Get the original data from the TableView
            ObservableList<SPPData> originalData = tvDataSPP.getItems();

            // Create a filtered list to store the search results
            ObservableList<SPPData> filteredData = FXCollections.observableArrayList();

            // Perform the search
            for (SPPData spp : originalData) {
                if (spp.getIdSPP().toLowerCase().contains(keyword) || spp.getTahun().toLowerCase().contains(keyword) || spp.getNominal().toLowerCase().contains(keyword)) {
                    filteredData.add(spp);
                }
            }

            // Update the TableView with the search results
            tvDataSPP.setItems(filteredData);
        }else{
            loadDataFromDatabase();
        }
    }
    
    // method add data
    @FXML
    private void addSPP(){
        int IDSPP;
        try{
            IDSPP = Integer.parseInt(tIdSPPAdd.getText()); //memanggil TextField ID Petugas
        } catch (NumberFormatException e) {
            IDSPP = 0;
        }
        int Tahun;
        try{
            Tahun = Integer.parseInt(tTahunAdd.getText()); //memanggil TextField ID Petugas
        } catch (NumberFormatException e) {
            Tahun = 0;
        }
        int Nominal;
        try{
            Nominal = Integer.parseInt(tNominalAdd.getText()); //memanggil TextField ID Petugas
        } catch (NumberFormatException e) {
            Nominal = 0;
        }
        String sppText = tIdSPPAdd.getText();
        String tahunText = tTahunAdd.getText();
        String nominalText = tNominalAdd.getText();
        
        if (sppText.length() != 0 && tahunText.length() != 0 && nominalText.length() != 0){ //seluruh inputan terisi
            boolean isExist = isExist(IDSPP); //memeriksa ID SPP yang dimasukkan terdaftar atau tidak
            if (isExist == false){ // jika terdaftar
                boolean successRegist = addDataSPP(IDSPP, Tahun, Nominal); //menyimpan data spp registrasi ke database
                if (successRegist) {
                    showAlert("Register Sucess", "Data SPP telah terbentuk.");
                    //mengosongkan input field
                    tIdSPPAdd.setText(null);
                    tTahunAdd.setText(null);
                    tNominalAdd.setText(null);
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Tambah SPP Failed", "Sesuatu error.");
                }
            }else{ //jika tidak terdaftar
                showAlert("Tambah SPP Failed", "ID SPP telah terdaftar.");
            }
        }else{ // ada field yang kosong
            //mengosongkan input field
            tIdSPPAdd.setText(null);
            tTahunAdd.setText(null);
            tNominalAdd.setText(null);
            
            showAlert("Tambah SPP Failed", "Silahkan isi seluruh inputan.");
        }
    }
    
    //    method untuk memeriksa ID SPP yang dimasukkan terdaftar atau tidak
    private boolean isExist(Integer Id_spp){
        boolean isExist = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT id_spp FROM spp WHERE id_spp = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_spp);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        isExist = true; //found same Kelas
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
    
    
    // menyimpan data ke table spp
    private boolean addDataSPP(Integer Id_spp, Integer Tahun, Integer Nominal){
        boolean isStored = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "INSERT INTO spp (id_spp, tahun, nominal) VALUES (?, ?, ?)"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_spp);
                preparedStatement.setInt(2, Tahun);
                preparedStatement.setInt(3, Nominal);
                
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
    private void editSPP(){
        SPPData selectedItem = tvDataSPP.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
                
                int IDSPP;
                try{
                    IDSPP = Integer.parseInt(selectedItem.getIdSPP()); //memanggil TextField ID Kelas
                } catch (NumberFormatException e) {
                    IDSPP = 0;
                }
                int Tahun;
                try{
                    Tahun = Integer.parseInt(tTahunEdit.getText()); //memanggil TextField ID Petugas
                } catch (NumberFormatException e) {
                    Tahun = 0;
                }
                int Nominal;
                try{
                    Nominal = Integer.parseInt(tNominalEdit.getText()); //memanggil TextField ID Petugas
                } catch (NumberFormatException e) {
                    Nominal = 0;
                }
                String tahunText = tTahunEdit.getText();
                String nominalText = tNominalEdit.getText();
                
                if (tahunText.length() != 0 && nominalText.length() != 0){ //seluruh inputan terisi
                    boolean successEdit = editDataSPP(IDSPP, Tahun, Nominal); //menyimpan data ke database
                    if (successEdit) {
                        showAlert("Edit Data Sucess", "Data SPP telah diperbarui.");
                        loadDataFromDatabase(); // mengambil data dari database
                    } else {
                        showAlert("Edit Data Failed", "Sesuatu error.");
                    }
                    //mengosongkan field inputan
                    tTahunEdit.setText(null);
                    tNominalEdit.setText(null);
                }else{ // ada field yang kosong
                    showAlert("Edit Data Failed", "Silahkan isi seluruh inputan.");
                }
        }else{
            showAlert("Edit Data Failed", "Silahkan pilih data yang ingin diedit.");
        }
    }
    
    // menyimpan data ke table spp
    private boolean editDataSPP(Integer Id_spp, Integer Tahun, Integer Nominal){
        boolean isEdited = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "UPDATE spp SET tahun = ?, nominal = ? WHERE id_spp = ?"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Tahun);
                preparedStatement.setInt(2, Nominal);
                preparedStatement.setInt(3, Id_spp);
                
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
    private void deleteSPP(){
        SPPData selectedItem = tvDataSPP.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
                int IDSPP;
                try{
                    IDSPP = Integer.parseInt(selectedItem.getIdSPP()); //memanggil TextField ID Kelas
                } catch (NumberFormatException e) {
                    IDSPP = 0;
                }
                Boolean isDeleted = deleteDataSPP(IDSPP);
                if (isDeleted == true){
                    showAlert("Delete SPP Success", "Data SPP Berhasil Dihapus.");
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Delete SPP Failed", "Silahkan coba kembali.");
                }
        }else{
            showAlert("Delete SPP Failed", "Silahkan pilih data yang ingin dihapus.");
        }
    }
    
    //    method untuk menghapus Data
    private boolean deleteDataSPP(Integer Id_spp){
        boolean isDeleted = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "DELETE FROM spp WHERE id_spp = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_spp);

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
    
    
    
    //    data class untuk menyimpan data SPP
    public static class SPPData {
        private String idSPP;
        private String tahun;
        private String nominal;

        public SPPData(String IdKelas, String Tahun, String Nominal) {
            this.idSPP = IdKelas;
            this.tahun = Tahun;
            this.nominal = Nominal;
        }
        public String getIdSPP() {
            return idSPP;
        }

        public String getTahun() {
            return tahun;
        }

        public String getNominal() {
            return nominal;
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
    
    //method untuk membuka menu data petugas
    @FXML
    private void openDataPetugas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataPetugas.fxml")); //memenggil view data petugas
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            DataPetugasController petugas = loader.getController();

            // mengirimkan user data ke DataPetugasController
            petugas.setUserData(id_petugas, username, password, nama_petugas);

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
