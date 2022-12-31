package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.domain.InternationalSupplier;
import model.domain.NationalSupplier;
import model.domain.Supplier;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordSupplierDialogController implements Initializable {
    @FXML private TextField tfName;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;
    @FXML private TextField tfCountry;
    @FXML private Group gbType;
    @FXML private RadioButton rbNational;
    @FXML private RadioButton rbInternational;
    @FXML private TextField tfCNPJNIF;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Supplier supplier;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isBtConfirmarClicked() {
        return btConfirmarClicked;
    }

    public void setBtConfirmarClicked(boolean btConfirmarClicked) {
        this.btConfirmarClicked = btConfirmarClicked;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        this.tfName.setText(this.supplier.getName());
        this.tfEmail.setText(this.supplier.getEmail());
        this.tfPhone.setText(this.supplier.getPhone());
        this.gbType.setDisable(true);

        if (supplier instanceof NationalSupplier) {
            rbNational.setSelected(true);
            tfCNPJNIF.setText(((NationalSupplier) this.supplier).getCnpj());
            tfCountry.setText("Brasil");
            tfCountry.setDisable(true);
        } else {
            rbInternational.setSelected(true);
            tfCNPJNIF.setText(((InternationalSupplier) this.supplier).getNif());
            tfCountry.setText(((InternationalSupplier) this.supplier).getCountry());
            tfCountry.setDisable(false);
        }

        this.tfName.requestFocus();
    }

    @FXML
    private void handleRbNacional(ActionEvent event) {
        this.tfCountry.setText("BRASIL");
        this.tfCountry.setDisable(true);
    }

    @FXML
    private void handleRbInternacional(ActionEvent event) {
        this.tfCountry.setText("");
        this.tfCountry.setDisable(false);
    }

    @FXML
    private void handleBtConfirm(ActionEvent event) {
        if(validateInputData()) {
            supplier.setName(tfName.getText());
            supplier.setEmail(tfEmail.getText());
            supplier.setPhone(tfPhone.getText());
            if(rbNational.isSelected()) {
                ((NationalSupplier)supplier).setCnpj(tfCNPJNIF.getText());
            } else {
                ((InternationalSupplier)supplier).setNif(tfCNPJNIF.getText());
                ((InternationalSupplier)supplier).setCountry(tfCountry.getText());
            }
            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleBtCancel(ActionEvent event) {
        dialogStage.close();
    }

    private boolean validateInputData() {
        String errorMessage = "";
        if(this.tfName.getText() == null || this.tfName.getText().length() == 2) errorMessage += "Nome inválido.\n";
        if(this.tfPhone.getText() == null || this.tfPhone.getText().length() == 0) errorMessage += "Telefone inválido.\n";
        if (this.tfEmail.getText() == null || this.tfEmail.getText().length() == 0 || !this.tfEmail.getText().contains("@")) errorMessage += "Email inválido.\n";

        if(rbNational.isSelected()) {
            if(this.tfCNPJNIF.getText() == null || this.tfCNPJNIF.getText().length() == 0) errorMessage += "CNPJ inválido.\n";
        } else {
            if(this.tfCNPJNIF.getText() == null || this.tfCNPJNIF.getText().length() == 0) errorMessage += "NIF inválido.\n";
            if(this.tfCountry.getText() == null || this.tfCountry.getText().length() == 0) errorMessage += "Informe o nome do país.\n";
        }

        if (errorMessage.length() == 0) return true;
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Corrija os campos inválidos!");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }

}
