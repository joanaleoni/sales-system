package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.domain.Situation;
import model.domain.Stock;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneProcessStockUpdateController implements Initializable {
    @FXML private TextField tfName;
    @FXML private TextField tfMinQuantity;
    @FXML private TextField tfMaxQuantity;
    @FXML private ChoiceBox<Situation> cbSituation;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;
    
    private Stage dialogStage;
    private boolean buttonConfirmClicked = false;
    private Stock stock;  

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadChoiceBoxSituation();
        setFocusLostHandle();
    }    

    private void setFocusLostHandle() {
        tfName.focusedProperty().addListener((ov, oldV, newV) -> {
        if (!newV) {
                if (tfName.getText() == null || tfName.getText().isEmpty()) {
                    tfName.requestFocus();
                }
            }
        });
    }
    
    public void loadChoiceBoxSituation(){
        cbSituation.setItems(FXCollections.observableArrayList(Situation.values()));
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isButtonConfirmClicked() {
        return buttonConfirmClicked;
    }

    public void setButtonConfirmClicked(boolean buttonConfirmClicked) {
        this.buttonConfirmClicked = buttonConfirmClicked;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
        tfName.setText(stock.getProduct().getName());
        tfMinQuantity.setText(Integer.toString(stock.getMinQuantity()));
        tfMaxQuantity.setText(Integer.toString(stock.getMaxQuantity()));
        cbSituation.getSelectionModel().select(stock.getSituation());
    }
    
    @FXML
    private void handleBtConfirm(ActionEvent event) {
        if(validateInputData()){
            stock.setMinQuantity(Integer.parseInt(tfMinQuantity.getText()));
            stock.setMaxQuantity(Integer.parseInt(tfMaxQuantity.getText()));
            stock.setSituation(cbSituation.getSelectionModel().getSelectedItem());
            buttonConfirmClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleBtCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    private boolean validateInputData(){
        String errorMessage = "";
        int maxQuantity = 0, minQuantity = 0;
        
        try {
            maxQuantity = Integer.parseInt(tfMaxQuantity.getText());
            minQuantity = Integer.parseInt(tfMinQuantity.getText());
            if(minQuantity >= maxQuantity)
                errorMessage += "A quantidade máxima informada deve ser maior que a quantidade mínima.\n";
        } catch(NumberFormatException ex){
            errorMessage += "Certifique-se que a quantidade mínima ou máxima foram inseridas corretamente.\n";
        }
        
        if(cbSituation.getSelectionModel().getSelectedItem() == null) errorMessage += "Você deve selecionar uma situação para o estoque.\n";
        
        if(errorMessage.length() == 0) return true;
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro na atualização do estoque");
            alert.setHeaderText("Por favor, corrija os campos inválidos.");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }    
    }    
}