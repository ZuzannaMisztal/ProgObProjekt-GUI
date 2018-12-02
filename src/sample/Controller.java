package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import misztal.DataFrame;
import misztal.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {
    public Button filechooser;
    public CheckBox areColumnNamesIncluded;
    public VBox vboxForColumnNames;
    public Tab load;
    public Tab view;
    public Button fileLoader;
    public TableView<ArrayList<? extends Value>> dataFrameTable;

    private File file;
    private DataFrame df;
    private ArrayList<String> columnNames;
    private Stage stage = new Stage();
    private ObservableList<ArrayList<? extends Value>> dataForDataFrameTable = FXCollections.observableArrayList();


    public void chooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(stage);
        displayVboxForColumnNames();
        setDisableofLoadButton();
    }

    public void displayVboxForColumnNames() {
        if (file != null) {
            vboxForColumnNames.getChildren().clear();
            if (areColumnNamesIncluded.isSelected()) {
                FileInputStream fstream = null;
                try {
                    fstream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                try {
                    columnNames = new ArrayList<>(Arrays.asList(br.readLine().split(",")));
                    br.close();
                    for (String columnName : columnNames) {
                        Label label = new Label(columnName);
                        vboxForColumnNames.getChildren().add(label);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileLoader.setDisable(false);
            } else {
                FileInputStream fstream = null;
                try {
                    fstream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                try {
                    int numberOfColumns = br.readLine().split(",").length;
                    br.close();
                    columnNames = new ArrayList<>(numberOfColumns);
                    for (int i = 0; i < numberOfColumns; i++) {
                        columnNames.add(i, "");
                        TextField getColumnNameTextField = new TextField();
                        getColumnNameTextField.setPromptText("column No." + (i + 1));
                        int finalI = i;
                        getColumnNameTextField.setOnAction((event) -> {
                            columnNames.set(finalI, String.valueOf(getColumnNameTextField.getCharacters()));
                            setDisableofLoadButton();
                        });
                        vboxForColumnNames.getChildren().add(getColumnNameTextField);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadFile() {
        if (areColumnNamesIncluded.isSelected()){
            try {
                df=new DataFrame(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                df=new DataFrame(file,columnNames);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        view.setDisable(false);
        loadDataFrameToTable();
    }

    private void setDisableofLoadButton(){
        for (String name:columnNames) {
            if (name.equals("")){
                fileLoader.setDisable(true);
                return;
            }
        }
        fileLoader.setDisable(false);
    }

    private void loadDataFrameToTable(){
        int i=0;
        for (int j = 0; j < df.numberOfRows(); j++) {
            dataForDataFrameTable.add(df.getRow(j));
        }
        dataFrameTable.setItems(dataForDataFrameTable);
        for (String colName:columnNames) {
            TableColumn<ArrayList<? extends Value>, String> tableColumn=new TableColumn<>(colName);
            int finalI = i;
            tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ArrayList<? extends Value>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ArrayList<? extends Value>, String> arrayListStringCellDataFeatures) {
                    return new SimpleStringProperty(arrayListStringCellDataFeatures.getValue().get(finalI).toString());
                }
            });
            ++i;
            dataFrameTable.getColumns().add(tableColumn);
        }
    }
}
