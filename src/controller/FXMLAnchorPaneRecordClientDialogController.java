package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.domain.Client;
import utils.Mask;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordClientDialogController implements Initializable {
    @FXML private TextField tfName;
    @FXML private TextField tfCpf;
    @FXML private TextField tfPhone;
    @FXML private TextField tfAddress;
    @FXML private DatePicker dpBirthDate;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;
    
    private Stage dialogStage;
    private boolean btConfirmClicked = false;
    private Client client;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Mask.maskCPF(tfCpf);
        Mask.maskDate(dpBirthDate);
        
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isBtConfirmClicked() {
        return btConfirmClicked;
    }

    public void setBtConfirmClicked(boolean btConfirmClicked) {
        this.btConfirmClicked = btConfirmClicked;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        this.tfName.setText(this.client.getName());
        this.tfCpf.setText(this.client.getCpf());
        this.tfPhone.setText(this.client.getPhone());
        this.tfAddress.setText(this.client.getAddress());
        this.dpBirthDate.setValue(this.client.getBirthDate());
    }   
    
    @FXML
    private void handleBtConfirm(ActionEvent event) {
        if(validateInputData()){
            client.setName(tfName.getText());
            client.setCpf(tfCpf.getText());
            client.setPhone(tfPhone.getText());
            client.setAddress(tfAddress.getText());
            client.setBirthDate(dpBirthDate.getValue());
            
            btConfirmClicked = true;
            dialogStage.close();
        }
    }
    
    @FXML
    private void handleBtCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    private boolean validateInputData(){
        String errorMessage = "";
        if(this.tfName.getText() == null || this.tfName.getText().length() < 2) errorMessage += "Nome inválido. \n";
        if(this.tfCpf.getText() == null || this.tfCpf.getText().length() < 11) errorMessage += "CPF inválido. \n";
        if(this.tfAddress.getText() == null || this.tfAddress.getText().length() == 0) errorMessage += "Endereço inválido. \n";
        if(this.tfPhone.getText() == null || this.tfPhone.getText().length() == 0) errorMessage += "Telefone inválido. \n";
        if(this.dpBirthDate.getValue() == null) errorMessage += "Data de nascimento inválida. \n";
        
        if(errorMessage.length() == 0) return true;
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