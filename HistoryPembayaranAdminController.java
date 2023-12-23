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
    import sekolah.DataSiswaController.SiswaData;

    public class HistoryPembayaranAdminController{

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
        private Button bCariPembayaran;

        @FXML
        private TextField tPembayaranSearch;

        @FXML
        private TableView<DataPembayaran> tvDataPembayaran;

        @FXML
        private TableColumn<DataPembayaran, String> cIdPembayarn;
        @FXML
        private TableColumn<DataPembayaran, String> cIdPetugas;
        @FXML
        private TableColumn<DataPembayaran, String> CNISN;
        @FXML
        private TableColumn<DataPembayaran, String> CTanggalBayar;
        @FXML
        private TableColumn<DataPembayaran, String> CJumlahBayar;
        @FXML
        private TableColumn<DataPembayaran, String> cMetode;
        @FXML
        private TableColumn<DataPembayaran, String> cStatus;



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

        // method inisiasi ketika membuka view data Pembayaran
        @FXML
        private void initialize() {
            cIdPembayarn.setCellValueFactory(new PropertyValueFactory<>("idPembayaran"));
            cIdPetugas.setCellValueFactory(new PropertyValueFactory<>("idPetugas"));
            CNISN.setCellValueFactory(new PropertyValueFactory<>("nisn"));
            CTanggalBayar.setCellValueFactory(new PropertyValueFactory<>("tanggalBayar"));
            CJumlahBayar.setCellValueFactory(new PropertyValueFactory<>("jumlahBayar"));
            cMetode.setCellValueFactory(new PropertyValueFactory<>("metode"));
            cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            loadDataFromDatabase(); // mengambil data dari database
        }

        // method untuk mengambil data Pembayaran
        private void loadDataFromDatabase() {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
                String query = "SELECT id_pembayaran, id_petugas, nisn, tgl_bayar, jumlah_bayar, metode, status FROM pembayaran"; // query mengambil data Siswa
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        ObservableList<HistoryPembayaranAdminController.DataPembayaran> data = FXCollections.observableArrayList(); // menyiapkan array untuk menampung data
                        while (resultSet.next()) { // perulanagan untuk mengambil seluruh baris hasil query
                            String id_pembayaran = String.valueOf(resultSet.getInt("id_pembayaran"));
                            String id_petugas = String.valueOf(resultSet.getInt("id_petugas"));
                            String nisn = String.valueOf(resultSet.getInt("nisn"));

                            Date date = resultSet.getDate("tgl_bayar");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String tgl_bayar = (date != null) ? dateFormat.format(date) : "";

                            String jumlah_bayar =  String.valueOf(resultSet.getInt("jumlah_bayar"));
                            String metode = resultSet.getString("metode");
                            String status = resultSet.getString("status");

                            data.add(new HistoryPembayaranAdminController.DataPembayaran(id_pembayaran, id_petugas, nisn, tgl_bayar, jumlah_bayar, metode, status)); // menambahkan data kedalam array data
                        }
                        tvDataPembayaran.setItems(data); // menambahhkan data kedalam table view
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void searchPembayaran() {
            String keyword = tPembayaranSearch.getText().toLowerCase();
            if (keyword.length() > 0){
                loadDataFromDatabase();
                // Get the original data from the TableView
                ObservableList<DataPembayaran> originalData = tvDataPembayaran.getItems();

                // Create a filtered list to store the search results
                ObservableList<DataPembayaran> filteredData = FXCollections.observableArrayList();

                // Perform the search
                for (DataPembayaran pembayaran : originalData) {
                    if (pembayaran.getIdPembayaran().toLowerCase().contains(keyword) || pembayaran.getIdPetugas().toLowerCase().contains(keyword) || pembayaran.getNisn().toLowerCase().contains(keyword) ||
                        pembayaran.getTanggalBayar().toLowerCase().contains(keyword) || pembayaran.getJumlahBayar().toLowerCase().contains(keyword) || pembayaran.getMetode().toLowerCase().contains(keyword) ||
                        pembayaran.getStatus().toLowerCase().contains(keyword)) {
                        filteredData.add(pembayaran);
                    }
                }

                // Update the TableView with the search results
                tvDataPembayaran.setItems(filteredData);
            }else{
                loadDataFromDatabase();
            }
        }



        //    data class untuk menyimpan data Pembayaran
        public static class DataPembayaran {
            private String idPembayaran;
            private String idPetugas;
            private String nisn;
            private String tanggalBayar;
            private String jumlahBayar;
            private String metode;
            private String status;

            public DataPembayaran(String idPembayaran, String idPetugas, String nisn, String tanggalBayar, String jumlahBayar, String metode, String status) {
                this.idPembayaran = idPembayaran;
                this.idPetugas = idPetugas;
                this.nisn = nisn;
                this.tanggalBayar = tanggalBayar;
                this.jumlahBayar = jumlahBayar;
                this.metode = metode;
                this.status = status;
            }
            // Getter methods
            public String getIdPembayaran() {
                return idPembayaran;
            }

            public String getIdPetugas() {
                return idPetugas;
            }

            public String getNisn() {
                return nisn;
            }

            public String getTanggalBayar() {
                return tanggalBayar;
            }

            public String getJumlahBayar() {
                return jumlahBayar;
            }

            public String getMetode() {
                return metode;
            }

            public String getStatus() {
                return status;
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

        // method untuk membuka data petugas
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
