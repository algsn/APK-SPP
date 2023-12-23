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

public class DataKelasController{
        
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
    private TextField tKelasSearch;
    
    @FXML
    private TextField tIdKelasAdd;
    @FXML
    private TextField tNamaKelasAdd;
    @FXML
    private TextField tKompetensiAdd;
    
    @FXML
    private TextField tNamaKelasEdit;
    @FXML
    private TextField tKompetensiEdit;
    
    @FXML
    private Button bCariPetugas;
    @FXML
    private Button bTambahKelas;
    @FXML
    private Button bEditKelas;
    @FXML
    private Button bDeleteKelas;
    
    @FXML
    private TableView<KelasData> tvDataKelas;

    @FXML
    private TableColumn<KelasData, String> cIdKelas;
    @FXML
    private TableColumn<KelasData, String> cNamakelas;
    @FXML
    private TableColumn<KelasData, String> cKompetensi;
    
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
    
    // method inisiasi ketika membuka view data kelas
    @FXML
    private void initialize() {
        cIdKelas.setCellValueFactory(new PropertyValueFactory<>("idKelas"));
        cNamakelas.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        cKompetensi.setCellValueFactory(new PropertyValueFactory<>("kompetensi"));
        
        loadDataFromDatabase(); // mengambil data dari database
    }
    
    // method untuk mengambil data kelas
    private void loadDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT id_kelas, nama_kelas, kompetensi_keahlian FROM kelas"; // query mengambil data kelas
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ObservableList<DataKelasController.KelasData> data = FXCollections.observableArrayList(); // menyiapkan array untuk menampung data
                    while (resultSet.next()) { // perulanagan untuk mengambil seluruh baris hasil query
                        String id_kelas = String.valueOf(resultSet.getInt("id_kelas"));
                        String nama_kelas = resultSet.getString("nama_kelas");
                        String kompetensi_keahlian = resultSet.getString("kompetensi_keahlian");
                        data.add(new DataKelasController.KelasData(id_kelas, nama_kelas, kompetensi_keahlian)); // menambahkan data kedalam array data
                    }
                    tvDataKelas.setItems(data); // menambahhkan data kedalam table view
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void searchKelas() {
        String keyword = tKelasSearch.getText().toLowerCase();
        if (keyword.length() > 0){
            // Get the original data from the TableView
            ObservableList<KelasData> originalData = tvDataKelas.getItems();

            // Create a filtered list to store the search results
            ObservableList<KelasData> filteredData = FXCollections.observableArrayList();

            // Perform the search
            for (KelasData kelas : originalData) {
                if (kelas.getIdKelas().toLowerCase().contains(keyword) || kelas.getNamaKelas().toLowerCase().contains(keyword) || kelas.getKompetensi().toLowerCase().contains(keyword)) {
                    filteredData.add(kelas);
                }
            }

            // Update the TableView with the search results
            tvDataKelas.setItems(filteredData);
        }else{
            loadDataFromDatabase();
        }
    }
    
    // method add data
    @FXML
    private void addKelas(){
        int IDKelas;
        try{
            IDKelas = Integer.parseInt(tIdKelasAdd.getText()); //memanggil TextField ID Petugas
        } catch (NumberFormatException e) {
            IDKelas = 0;
        }
        String kelasText = tIdKelasAdd.getText();
        String NamaKelas = tNamaKelasAdd.getText();
        String Kompetensi = tKompetensiAdd.getText();
        
        if (kelasText.length() != 0 && NamaKelas.length() != 0 && Kompetensi.length() != 0){ //seluruh inputan terisi
            boolean isExist = isExist(IDKelas); //memeriksa ID Petugas yang dimasukkan terdaftar atau tidak
            if (isExist == false){ // jika terdaftar
                boolean successRegist = addDataKelas(IDKelas, NamaKelas, Kompetensi); //menyimpan data registrasi ke database
                if (successRegist) {
                    showAlert("Register Sucess", "Data Kelas telah terbentuk.");
                    //mengosongkan input field
                    tIdKelasAdd.setText(null);
                    tNamaKelasAdd.setText(null);
                    tKompetensiAdd.setText(null);
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Tambah Kelas Failed", "Sesuatu error.");
                }
            }else{ //jika tidak terdaftar
                showAlert("Tambah Kelas Failed", "ID Petugas telah terdaftar.");
            }
        }else{ // ada field yang kosong
            //mengosongkan input field
            tIdKelasAdd.setText(null);
            tNamaKelasAdd.setText(null);
            tKompetensiAdd.setText(null);
            
            showAlert("Tambah Kelas Failed", "Silahkan isi seluruh inputan.");
        }
    }
    
    //    method untuk memeriksa ID Petugas yang dimasukkan terdaftar atau tidak
    private boolean isExist(Integer Id_kelas){
        boolean isExist = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT id_kelas FROM kelas WHERE id_kelas = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_kelas);

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
    
    
    // menyimpan data ke table kelas
    private boolean addDataKelas(Integer Id_kelas, String NamaKelas, String Kompetensi){
        boolean isStored = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "INSERT INTO kelas (id_kelas, nama_kelas, kompetensi_keahlian) VALUES (?, ?, ?)"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_kelas);
                preparedStatement.setString(2, NamaKelas);
                preparedStatement.setString(3, Kompetensi);
                
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
    private void editKelas(){
        KelasData selectedItem = tvDataKelas.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            loadDataFromDatabase();
                int IDKelas;
                try{
                    IDKelas = Integer.parseInt(selectedItem.getIdKelas()); //memanggil TextField ID Kelas
                } catch (NumberFormatException e) {
                    IDKelas = 0;
                }
                String NamaKelas = tNamaKelasEdit.getText();
                String Kompetensi = tKompetensiEdit.getText();
                
                if (NamaKelas.length() != 0 && Kompetensi.length() != 0){ //seluruh inputan terisi
                    boolean successEdit = editDataKelas(IDKelas, NamaKelas, Kompetensi); //menyimpan data ke database
                    if (successEdit) {
                        showAlert("Edit Data Sucess", "Data Kelas telah diperbarui.");
                        loadDataFromDatabase(); // mengambil data dari database
                    } else {
                        showAlert("Edit Data Failed", "Sesuatu error.");
                    }
                    //mengosongkan field inputan
                    tNamaKelasEdit.setText(null);
                    tKompetensiEdit.setText(null);
                }else{ // ada field yang kosong
                    showAlert("Edit Data Failed", "Silahkan isi seluruh inputan.");
                }
        }else{
            showAlert("Edit Data Failed", "Silahkan pilih data yang ingin diedit.");
        }
    }
    
    // menyimpan data ke table kelas
    private boolean editDataKelas(Integer Id_kelas, String NamaKelas, String Kompetensi){
        boolean isEdited = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "UPDATE kelas SET nama_kelas = ?, kompetensi_keahlian = ? WHERE id_kelas = ?"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, NamaKelas);
                preparedStatement.setString(2, Kompetensi);
                preparedStatement.setInt(3, Id_kelas);
                
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
    private void deleteKelas(){
        KelasData selectedItem = tvDataKelas.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
                int IDKelas;
                try{
                    IDKelas = Integer.parseInt(selectedItem.getIdKelas()); //memanggil TextField ID Kelas
                } catch (NumberFormatException e) {
                    IDKelas = 0;
                }
                Boolean isDeleted = deleteDataKelas(IDKelas);
                if (isDeleted == true){
                    showAlert("Delete Kelas Success", "Data Kelas Berhasil Dihapus.");
                    tvDataKelas.getItems().remove(selectedItem);
                } else {
                    showAlert("Delete Kelas Failed", "Silahkan coba kembali.");
                }
        }else{
            showAlert("Delete Kelas Failed", "Silahkan pilih data yang ingin dihapus.");
        }
    }
    
    //    method untuk menghapus Data
    private boolean deleteDataKelas(Integer Id_kelas){
        boolean isDeleted = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "DELETE FROM kelas WHERE id_kelas = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Id_kelas);

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
    
    
    //    data class untuk menyimpan data kelas
    public static class KelasData {
        private String idKelas;
        private String namaKelas;
        private String kompetensi;

        public KelasData(String IdKelas, String NamaKelas, String Kompetensi) {
            this.idKelas = IdKelas;
            this.namaKelas = NamaKelas;
            this.kompetensi = Kompetensi;
        }
        public String getIdKelas() {
            return idKelas;
        }

        public String getNamaKelas() {
            return namaKelas;
        }

        public String getKompetensi() {
            return kompetensi;
        }
    }
    
    // method untuk logout, action dari button logout
    @FXML
    private void Logout() {
        showAlertWarning("Warning", "Apakah yakin untuk logout?");
        if (continueProcess) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginAdmin.fxml"));  // memanggil view login bagi admin
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataSiswa.fxml"));//membuka view data siswa
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataPetugas.fxml")); //membuka view data petugas
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
