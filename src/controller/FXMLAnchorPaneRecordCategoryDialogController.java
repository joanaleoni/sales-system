package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.domain.Category;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordCategoryDialogController implements Initializable {
    @FXML private TextField tfDescription;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;
    
    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Category category;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.tfDescription.setText(category.getDescription());
    }

    @FXML
    private void handleBtConfirm(ActionEvent event) {
        if(validateInputData()) {
           category.setDescription(tfDescription.getText());
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
        if(this.tfDescription.getText() == null || this.tfDescription.getText().length() < 2) 
            errorMessage += "Descrição inválida. A descrição deve ter, pelo menos, 2 caracteres.\n";
                
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