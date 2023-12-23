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
import java.time.LocalDate;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PembayaranSiswaController{
        
    @FXML
    private Label lWelcome;
    @FXML
    private Label lInfo;
    @FXML
    private Button bLogout;
    @FXML
    private Button bHistoryPembayaran;
    
    @FXML
    private TextField fNisn;
    @FXML
    private TextField fIdPetugas;
    @FXML
    private TextField fJumlahBayar;
    @FXML
    private DatePicker fTanggal;
    
    @FXML
    private ComboBox cMetode;
    
//    data siswa
    private int nisn;
    private int nis;
    private String nama;
    private int id_kelas;
    private String alamat;
    private String no_telp;
    private int id_spp;
    
    private boolean continueProcess;
    
    void setUserData(Integer Nisn, Integer Nis, String Nama, Integer Id_kelas, String Alamat, String No_telp, Integer Id_spp) {
        nisn = Nisn;
        nis = Nis;
        nama = Nama;
        id_kelas = Id_kelas;
        alamat = Alamat;
        no_telp = No_telp;
        id_spp = Id_spp;
        
        lWelcome.setText("Hallo, " + nama);
    }
    
    @FXML
    private void initialize() {
        cMetode.getItems().removeAll(cMetode.getItems());
        cMetode.getItems().addAll("Bank BRI", "Bank BCA", "Bank Mandiri");
        cMetode.getSelectionModel().select("Bank BCA");
    }
    
    //Method untuk melakukan pembayaran
    @FXML
    private void AddPembayaran(){
        int NISN;
        try{
            NISN = Integer.parseInt(fNisn.getText()); //memanggil TextField NISN
        } catch (NumberFormatException e) {
            NISN = 0;
        }
        String nisnText = fNisn.getText();
        
        int IDPetugas;
        try{
            IDPetugas = Integer.parseInt(fIdPetugas.getText()); //memanggil TextField NISN
        } catch (NumberFormatException e) {
            IDPetugas = 0;
        }
        String idPetugasText = fIdPetugas.getText();
        
        int JumlahBayar;
        try{
            JumlahBayar = Integer.parseInt(fJumlahBayar.getText()); //memanggil TextField NISN
        } catch (NumberFormatException e) {
            JumlahBayar = 0;
        }
        String jumlahBayarText = fJumlahBayar.getText();
        
        LocalDate tanggal = fTanggal.getValue();
        
        Object combo = cMetode.getValue();
        String bank = String.valueOf(combo);
        
        if (nisnText.length() != 0 && idPetugasText.length() != 0 && jumlahBayarText.length() != 0 && tanggal != null && combo != null){ //seluruh field terisi
            showAlertWarning("Warning", "Apakah data pembayaran sudah benar?"); //alert validation ketika pembayaran
            if (continueProcess) { // jika menekan tomobol ok di alert
                boolean succesStore = storePembayaran(NISN,IDPetugas,JumlahBayar,tanggal,bank); // menambahkan data pembayaran
                if (succesStore) {//peminjaman berhasil
                    showAlert("Pembayaran Sucess", "Peminjaman berhasil disimpan.");
                    
//                    mengosongkan input field
                    fNisn.setText(null);
                    fIdPetugas.setText(null);
                    fJumlahBayar.setText(null);
                    fTanggal.setValue(null);
                    lInfo.setText("Virtual Account " + bank + " : 0005876288234");
                } else { // pembayaran gagal
                    showAlert("Pembayaran Failed", "NISN atau ID Petugas tidak terdaftar.");
                }
            }
        }else{ // terdapat input field yang kosong
            showAlert("Pembayaran Failed", "Silahkan isi seluruh inputan.");
        }
        
        System.out.println(bank);
    }
    
    // menyimpan data ke table pembayarn
    private boolean storePembayaran(Integer NISN, Integer IDPetugas, Integer JumlahBayar, LocalDate Tanggal, String bank){
        boolean isStored = false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sekolah", "root", "")) {
            String query = "INSERT INTO pembayaran (id_petugas , nisn , tgl_bayar, jumlah_bayar, metode) VALUES (?, ?, ?, ?, ?)"; // query insert data
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, IDPetugas);
                preparedStatement.setInt(2, NISN);
                java.sql.Date tanggal_bayar = java.sql.Date.valueOf(Tanggal);
                preparedStatement.setDate(3, tanggal_bayar);
                preparedStatement.setInt(4, JumlahBayar);
                preparedStatement.setString(5, bank);
                
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
    
    @FXML
    private void Logout() {
        showAlertWarning("Warning", "Apakah yakin untuk logout?");
        if (continueProcess) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginSiswa.fxml"));
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
    
    @FXML
    private void showHistoryPembayaran(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistoryPembayaranSiswa.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            HistoryPembayaranSiswaController history = loader.getController();

            // mengirimkan user data ke PembayaranSiswaController
            history.setUserData(nisn, nis, nama, id_kelas, alamat, no_telp, id_spp);

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
