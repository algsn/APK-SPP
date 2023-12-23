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


    public class LoginSiswaController{

        @FXML
        private TextField fNisn;

        @FXML
        private Button bLogin;

        @FXML
        private Button bAdministrator;

        // Connect Database
        private static final String DB_URL = "jdbc:mysql://localhost:3306/sekolah";
        private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "";

    //    data siswa
        private int nisn;
        private int nis;
        private String nama;
        private int id_kelas;
        private String alamat;
        private String no_telp;
        private int id_spp;

    //
        private int nomorIndukSiswaNasional;


    //    Method login sebagai action dari tombol login siswa
        @FXML
        private void Login() {
            try{
                nomorIndukSiswaNasional = Integer.parseInt(fNisn.getText()); //memanggil TextField NISN
            } catch (NumberFormatException e) {
                nomorIndukSiswaNasional = 0;
            }

            if (nomorIndukSiswaNasional != 0){ //inputan terisi semua
                // Authenticate siswa and get siswa data
                Boolean siswaData = authenticateUser(nomorIndukSiswaNasional); //memeriksa Nomor Induk Siswa Nasional benar atau tidak

                if (siswaData == true) { //siswa ditemukan
                    System.out.println("Login Success");
                    System.out.println(nisn + nis + nama + id_kelas + alamat + no_telp + id_spp);
                    openSiswaHome(nisn, nis, nama, id_kelas, alamat, no_telp, id_spp);
                } else {//user tidak ditemukan
                    showAlert("Login Failed", "Nomor Induk Siswa Nasional Tidak Terdaftar, silahkan hubungi admin.");
                }
            } else { // ada inputan yang kosong atau bukan angka
                showAlert("Login Failed", "Silahkan coba kembali.");
            }
            System.out.println("Login Admin");
        }

        //    Auth method untuk mencari usernam password yang sama
        private Boolean authenticateUser(Integer nomorIndukSiswaNasional) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
                String query = "SELECT * FROM siswa WHERE nisn = ?"; // query untuk cari username dan password yang sama
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, nomorIndukSiswaNasional);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) { //menemukan siswa

                            nisn = resultSet.getInt("nisn");
                            nis = resultSet.getInt("nis");
                            nama = resultSet.getString("nama");
                            id_kelas = resultSet.getInt("id_kelas");
                            alamat = resultSet.getString("alamat");
                            no_telp = resultSet.getString("no_telp");
                            id_spp = resultSet.getInt("id_spp");

                            return true; //mengembalikan nilai true, menandakan menemukan siswa
                        }
                        resultSet.close();
                    }
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false; //mengembalikan false, menandakan tidak menemukan siswa
        }

        private void openSiswaHome(Integer nisn, Integer nis, String nama, Integer id_kelas, String alamat, String no_telp, Integer id_spp) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PembayaranSiswa.fxml"));
                Parent root = loader.load();

                // Get the controller associated with the loaded FXML
                PembayaranSiswaController pembayaran = loader.getController();

                // mengirimkan user data ke PembayaranSiswaController
                pembayaran.setUserData(nisn, nis, nama, id_kelas, alamat, no_telp, id_spp);

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
        private void Administrator() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginAdmin.fxml"));
                Parent root = loader.load();

                // Create a new stage and set the scene
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                // Close the login window
                Stage loginStage = (Stage) bAdministrator.getScene().getWindow();
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
