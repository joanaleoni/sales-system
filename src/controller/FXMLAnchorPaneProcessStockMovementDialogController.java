package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.domain.Stock;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneProcessStockMovementDialogController implements Initializable {
    @FXML private TextField tfQuantity;
    @FXML private Label lbCurrentQuantity;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;
    
    private Stage dialogStage;
    private boolean btConfirmClicked = false;
    private Stock stock;    
    private String movementType;

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
        btConfirm.setText(movementType);
    }

    public boolean isBtConfirmClicked() {
        return btConfirmClicked;
    }

    public void setBtConfirmClicked(boolean btConfirmClicked) {
        this.btConfirmClicked = btConfirmClicked;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
        this.lbCurrentQuantity.setText(Integer.toString(stock.getQuantity()));
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }
    
    @FXML
    private void handleBtConfirm(ActionEvent event) {
        try {
            if(movementType.equalsIgnoreCase("Repôr")) stock.replace(Integer.parseInt(tfQuantity.getText()));
            else stock.remove(Integer.parseInt(tfQuantity.getText()));
            
            btConfirmClicked = true;
            dialogStage.close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro na movimentação");
            alert.setHeaderText("Corrija a quantidade ou cancele a operação!");
            alert.setContentText(ex.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleBtCancel(ActionEvent event) {
        dialogStage.close();
    }    
}