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

public class DataSiswaController{
    
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
    private TextField tSiswaSearch;
    
    @FXML
    private TextField tNISNAdd;
    @FXML
    private TextField tNISAdd;
    @FXML
    private TextField tIdKelasAdd;
    @FXML
    private TextField tNamaAdd;
    @FXML
    private TextField tAlamatAdd;
    @FXML
    private TextField tNomorTeleponAdd;
    @FXML
    private TextField tIdSPPAdd;
    
    @FXML
    private TextField tNISEdit;
    @FXML
    private TextField tIdKelasEdit;
    @FXML
    private TextField tIdSPPEdit;
    @FXML
    private TextField tNomorEdit;
    @FXML
    private TextField tNamaEdit;
    @FXML
    private TextField tAlamatEdit;
    
    @FXML
    private Button bCariSiswa;
    @FXML
    private Button bTambahSiswa;
    @FXML
    private Button bEditSiswa;
    @FXML
    private Button bDeleteSiswa;
    
    @FXML
    private TableView<SiswaData> tvDataSiswa;

    @FXML
    private TableColumn<SiswaData, String> cNISN;
    @FXML
    private TableColumn<SiswaData, String> cNIS;
    @FXML
    private TableColumn<SiswaData, String> cNama;
    @FXML
    private TableColumn<SiswaData, String> cIdKelas;
    @FXML
    private TableColumn<SiswaData, String> cAlamat;
    @FXML
    private TableColumn<SiswaData, String> cNomorTelepon;
    @FXML
    private TableColumn<SiswaData, String> cIdSPP;
    
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
    
    // method inisiasi ketika membuka view data Siswa
    @FXML
    private void initialize() {
        cNISN.setCellValueFactory(new PropertyValueFactory<>("nisn"));
        cNIS.setCellValueFactory(new PropertyValueFactory<>("nis"));
        cNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        cIdKelas.setCellValueFactory(new PropertyValueFactory<>("idKelas"));
        cAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        cNomorTelepon.setCellValueFactory(new PropertyValueFactory<>("nomorTelepon"));
        cIdSPP.setCellValueFactory(new PropertyValueFactory<>("idSPP"));
        
        loadDataFromDatabase(); // mengambil data dari database
    }
    
    // method untuk mengambil data Siswa
    private void loadDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT nisn, nis, nama, id_kelas, alamat, id_spp, no_telp FROM siswa"; // query mengambil data Siswa
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ObservableList<DataSiswaController.SiswaData> data = FXCollections.observableArrayList(); // menyiapkan array untuk menampung data
                    while (resultSet.next()) { // perulanagan untuk mengambil seluruh baris hasil query
                        String nisn = String.valueOf(resultSet.getInt("nisn"));
                        String nis = resultSet.getString("nis");
                        String nama = resultSet.getString("nama");
                        String id_kelas = String.valueOf(resultSet.getInt("id_kelas"));
                        String alamat = resultSet.getString("alamat");
                        String no_telp = resultSet.getString("no_telp");
                        String id_spp = String.valueOf(resultSet.getInt("id_spp"));
                        
                        data.add(new DataSiswaController.SiswaData(nisn, nis, nama, id_kelas, alamat, no_telp, id_spp)); // menambahkan data kedalam array data
                    }
                    tvDataSiswa.setItems(data); // menambahhkan data kedalam table view
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void searchSiswa() {
        String keyword = tSiswaSearch.getText().toLowerCase();
        if (keyword.length() > 0){
            loadDataFromDatabase();
            // Get the original data from the TableView
            ObservableList<SiswaData> originalData = tvDataSiswa.getItems();

            // Create a filtered list to store the search results
            ObservableList<SiswaData> filteredData = FXCollections.observableArrayList();

            // Perform the search
            for (SiswaData siswa : originalData) {
                if (siswa.getNisn().toLowerCase().contains(keyword) || siswa.getNis().toLowerCase().contains(keyword) || siswa.getNama().toLowerCase().contains(keyword) ||
                    siswa.getIdKelas().toLowerCase().contains(keyword) || siswa.getAlamat().toLowerCase().contains(keyword) || siswa.getNomorTelepon().toLowerCase().contains(keyword) ||
                    siswa.getIdSPP().toLowerCase().contains(keyword)) {
                    filteredData.add(siswa);
                }
            }

            // Update the TableView with the search results
            tvDataSiswa.setItems(filteredData);
        }else{
            loadDataFromDatabase();
        }
    }
    
    
    
    // method add data Siswa
    @FXML
    private void addSiswa(){
        int NISN;
        try{
            NISN = Integer.parseInt(tNISNAdd.getText()); //memanggil TextField NISN
        } catch (NumberFormatException e) {
            NISN = 0;
        }
        String nisnText = tNISNAdd.getText();
        
        int NIS;
        try{
            NIS = Integer.parseInt(tNISAdd.getText()); //memanggil TextField NIS
        } catch (NumberFormatException e) {
            NIS = 0;
        }
        String nisText = tNISAdd.getText();
        
        String Nama = tNamaAdd.getText();
        
        int IdKelas;
        try{
            IdKelas = Integer.parseInt(tIdKelasAdd.getText()); //memanggil TextField ID Kelas
        } catch (NumberFormatException e) {
            IdKelas = 0;
        }
        String idKelasText = tIdKelasAdd.getText();
        
        String Alamat = tAlamatAdd.getText();
        
        String NomorTelepon = tNomorTeleponAdd.getText();
        
        int IdSPP;
        try{
            IdSPP = Integer.parseInt(tIdSPPAdd.getText()); //memanggil TextField ID SPP
        } catch (NumberFormatException e) {
            IdSPP = 0;
        }
        String sppText = tIdSPPAdd.getText();
        
        if (nisnText.length() != 0 && nisText.length() != 0 && Nama.length() != 0 &&
            idKelasText.length() != 0 && Alamat.length() != 0 && NomorTelepon.length() != 0 &&
            sppText.length() != 0){ //seluruh inputan terisi
            boolean isExist = isExist(NISN); //memeriksa NISN yang dimasukkan terdaftar atau tidak
            if (isExist == false){ // jika terdaftar
                boolean successRegist = addDataSiswa(NISN, NIS, Nama, IdKelas, Alamat, NomorTelepon, IdSPP); //menyimpan data siswa registrasi ke database
                if (successRegist) {
                    showAlert("Tambah Siswa Sucess", "Data Siswa telah terbentuk.");
                    //mengosongkan input field
                    tNISNAdd.setText(null);
                    tNISAdd.setText(null);
                    tNamaAdd.setText(null);
                    tIdKelasAdd.setText(null);
                    tAlamatAdd.setText(null);
                    tNomorTeleponAdd.setText(null);
                    tIdSPPAdd.setText(null);
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Tambah Siswa Failed", "ID Kelas atau ID SPP tidak terdaftar.");
                }
            }else{ //jika tidak terdaftar
                showAlert("Tambah Siswa Failed", "ID Siswa telah terdaftar.");
            }
        }else{ // ada field yang kosong
            //mengosongkan input field
            tNISNAdd.setText(null);
            tNISAdd.setText(null);
            tNamaAdd.setText(null);
            tIdKelasAdd.setText(null);
            tAlamatAdd.setText(null);
            tNomorTeleponAdd.setText(null);
            tIdSPPAdd.setText(null);
            
            showAlert("Tambah Siswa Failed", "Silahkan isi seluruh inputan.");
        }
    }
    
    //    method untuk memeriksa NISN yang dimasukkan terdaftar atau tidak
    private boolean isExist(Integer NISN){
        boolean isExist = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "SELECT nisn FROM siswa WHERE nisn = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, NISN);

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
    
    
    // menyimpan data ke table siswa
    private boolean addDataSiswa(Integer NISN, Integer NIS, String Nama, Integer IdKelas, String Alamat, String NomorTelepon, Integer IdSPP){
        boolean isStored = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "INSERT INTO siswa (nisn, nis, nama, id_kelas, alamat, no_telp, id_spp) VALUES (?, ?, ?, ?, ?, ?, ?)"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, NISN);
                preparedStatement.setInt(2, NIS);
                preparedStatement.setString(3, Nama);
                preparedStatement.setInt(4, IdKelas);
                preparedStatement.setString(5, Alamat);
                preparedStatement.setString(6, NomorTelepon);
                preparedStatement.setInt(7, IdSPP);
                
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
    
    
    // method edit siswa
    @FXML
    private void editSiswa(){
        SiswaData selectedItem = tvDataSiswa.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int NISN;
            try{
                NISN = Integer.parseInt(selectedItem.getNisn()); //memanggil TextField NISN
            } catch (NumberFormatException e) {
                NISN = 0;
            }
            int NIS;
            try{
                NIS = Integer.parseInt(tNISEdit.getText()); //memanggil TextField NIS
            } catch (NumberFormatException e) {
                NIS = 0;
            }
            String nisText = tNISEdit.getText();

            String Nama = tNamaEdit.getText();

            int IdKelas;
            try{
                IdKelas = Integer.parseInt(tIdKelasEdit.getText()); //memanggil TextField ID Kelas
            } catch (NumberFormatException e) {
                IdKelas = 0;
            }
            String idKelasText = tIdKelasEdit.getText();

            String Alamat = tAlamatEdit.getText();

            String NomorTelepon = tNomorEdit.getText();

            int IdSPP;
            try{
                IdSPP = Integer.parseInt(tIdSPPEdit.getText()); //memanggil TextField ID SPP
            } catch (NumberFormatException e) {
                IdSPP = 0;
            }
            String sppText = tIdSPPEdit.getText();
                
            if (nisText.length() != 0 && Nama.length() != 0 && 
                idKelasText.length() != 0 && Alamat.length() != 0 && 
                NomorTelepon.length() != 0 && sppText.length() != 0){ //seluruh inputan terisi
                
                boolean successEdit = editDataSiswa(NISN, NIS, Nama, IdKelas, Alamat, NomorTelepon, IdSPP); //menyimpan data ke database
                if (successEdit) {
                    showAlert("Edit Data Sucess", "Data Siswa telah diperbarui.");
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Edit Data Failed", "ID Kelas atau ID SPP tidak terdaftar.");
                }
                //mengosongkan field inputan
                tNISEdit.setText(null);
                tNamaEdit.setText(null);
                tIdKelasEdit.setText(null);
                tIdSPPEdit.setText(null);
                tNomorEdit.setText(null);
                tAlamatEdit.setText(null);
            }else{ // ada field yang kosong
                showAlert("Edit Data Failed", "Silahkan isi seluruh inputan.");
            }
        }else{
            showAlert("Edit Data Failed", "Silahkan pilih data yang ingin diedit.");
        }
    }
    
    // menyimpan data ke table siswa
    private boolean editDataSiswa(Integer NISN, Integer NIS, String Nama, Integer IdKelas, String Alamat, String NomorTelepon, Integer IdSPP){
        boolean isEdited = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "UPDATE siswa SET nis = ?, nama = ?, id_kelas = ?, alamat = ?, no_telp = ?, id_spp  = ? WHERE nisn  = ?"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, NIS);
                preparedStatement.setString(2, Nama);
                preparedStatement.setInt(3, IdKelas);
                preparedStatement.setString(4, Alamat);
                preparedStatement.setString(5, NomorTelepon);
                preparedStatement.setInt(6, IdSPP);
                preparedStatement.setInt(7, NISN);
                
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
    private void deleteSiswa(){
        SiswaData selectedItem = tvDataSiswa.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
                int NISN;
                try{
                    NISN = Integer.parseInt(selectedItem.getNisn()); //memanggil TextField NISN
                } catch (NumberFormatException e) {
                    NISN = 0;
                }
                Boolean isDeleted = deleteDataSiswa(NISN);
                if (isDeleted == true){
                    showAlert("Delete Siswa Success", "Data Siswa Berhasil Dihapus.");
                    loadDataFromDatabase(); // mengambil data dari database
                } else {
                    showAlert("Delete Siswa Failed", "Silahkan coba kembali.");
                }
        }else{
            showAlert("Delete Siswa Failed", "Silahkan pilih data yang ingin dihapus.");
        }
    }
    
    //    method untuk menghapus Data
    private boolean deleteDataSiswa(Integer NISN){
        boolean isDeleted = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "DELETE FROM siswa WHERE nisn = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, NISN);

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
    
    
    
    //    data class untuk menyimpan data Siswa
    public static class SiswaData {
        private String nisn;
        private String nis;
        private String nama;
        private String idKelas;
        private String alamat;
        private String nomorTelepon;
        private String idSPP;

        public SiswaData(String NISN, String NIS, String Nama, String IdKelas, String Alamat, String NomorTelepon, String IdSPP) {
            this.nisn = NISN;
            this.nis = NIS;
            this.nama = Nama;
            this.idKelas = IdKelas;
            this.alamat = Alamat;
            this.nomorTelepon = NomorTelepon;
            this.idSPP = IdSPP;
        }
        // Getter methods
        public String getNisn() {
            return nisn;
        }

        public String getNis() {
            return nis;
        }

        public String getNama() {
            return nama;
        }

        public String getIdKelas() {
            return idKelas;
        }

        public String getAlamat() {
            return alamat;
        }

        public String getNomorTelepon() {
            return nomorTelepon;
        }

        public String getIdSPP() {
            return idSPP;
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
    
    // method untuk membuka menu Data petugas
    @FXML
    private void openDataPetugas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataPetugas.fxml")); //memanggil view data petugas
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
