package com.texteditor.app.texteditor;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class textareafontsetting {

    static int fontsize = 14;
    static String fontfamily = "Consolas";
    static FontPosture defaultposture = FontPosture.REGULAR;
    static FontWeight defaultWeight = FontWeight.NORMAL;

    public void change(int font,String family,FontWeight weight,FontPosture posture)
    {
        textareafontsetting.defaultposture = posture;
        textareafontsetting.fontsize = font;
        textareafontsetting.fontfamily = family;
        textareafontsetting.defaultWeight = weight;
    }
}




public class basic extends Application {

    String storefiledatacheck; //after file has been saved or new file has been opened
    String tempfamily  = "Consolas";
    String tempfiledata;
    FontPosture tempPosture = FontPosture.REGULAR;
    FontWeight tempWeight =  FontWeight.NORMAL;
    int tempsize=14;
    File filelocation=null; //global

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("TextEditor+");
        Image iconImage = new Image(getClass().getResourceAsStream("texteditoricon.png"));
        primaryStage.getIcons().add(iconImage);
        TextArea txtarea = new TextArea();

        //Load Images for menu items
        Image b1img = new Image(getClass().getResourceAsStream("b1img.png"));
        ImageView showimg1 = new ImageView(b1img);
        Image b2img = new Image(getClass().getResourceAsStream("b2img.png"));
        ImageView showimg2 = new ImageView(b2img);


        //Creating menu items for File
        MenuItem newItem = new MenuItem("New								");
        MenuItem opeItem = new MenuItem("Open File...					");
        MenuItem exItem =  new MenuItem("Exit							");
        MenuItem saveItem = new MenuItem("Save						 	");
        MenuItem saveasItem = new MenuItem("SaveAs						");

        // Creating Menu bar for file and adding icons and setting style
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newItem,opeItem,saveItem,saveasItem,exItem);
        MenuBar fileMenuBar = new MenuBar();
        fileMenuBar.getMenus().add(fileMenu);
        fileMenu.setGraphic(showimg1);
        fileMenu.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");

        //Menu items for edit menu
        MenuItem cutMenu = new MenuItem("Cut	");
        MenuItem copyMenu = new MenuItem("Copy ");
        MenuItem pasteMenu = new MenuItem("Paste	");
        MenuItem appearanceItem = new MenuItem("Change Appearance 			");

        //Creating menu bar for edit menu and setting icon for edit menu
        Menu editMenu = new Menu();
        editMenu.setText("Edit");

        editMenu.setGraphic(showimg2);
        editMenu.setStyle("-fx-text-fill: red;");
        editMenu.getItems().addAll(cutMenu,copyMenu,pasteMenu,appearanceItem);
        MenuBar editBar = new MenuBar();
        editBar.getMenus().add(editMenu);
        editMenu.setStyle("-fx-font-family: 'Consolas'; " +"-fx-font-size: 12px; ");
        editMenu.setId("editmenu");

        txtarea.setOnKeyTyped(event->{
            tempfiledata = txtarea.getText(); // gets current text of text area
            tempfiledata = tempfiledata.replace("\b","");
        });

        //Performing actions for menu items
        //1. Save As Item
        saveasItem.setOnAction(eve->{
            savepopup(primaryStage);
        });
        //2. Save Item
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCodeCombination.CONTROL_DOWN));
        saveItem.setOnAction(eve->{

            if(filelocation!=null)
            {
                try {
                    savecurrenttfile(filelocation, tempfiledata);
                    storefiledatacheck=tempfiledata;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {
                savepopup(primaryStage);
            }

        });

        //3. Open Item
        opeItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));
        opeItem.setOnAction(eve->{
            if(Objects.equals(storefiledatacheck, tempfiledata))
            {
                try {
                    openfile(primaryStage);
                    txtarea.clear();
                    txtarea.setText(storefiledatacheck);//this will set string obtained from opening
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if(filelocation == null && tempfiledata.isEmpty()==true)
            {
                try {
                    openfile(primaryStage);
                    txtarea.clear();
                    txtarea.setText(storefiledatacheck);//this will set string obtained from opening
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {

                Alert warningAlert = new Alert(AlertType.WARNING);
                warningAlert.setTitle("File Not Saved !");
                warningAlert.setHeaderText("Warning: Unsaved Changes");
                ButtonType yes = new ButtonType("YES");
                ButtonType no = new ButtonType("NO");
                ButtonType cancel=new ButtonType("CANCEL");
                warningAlert.getButtonTypes().setAll(yes,no,cancel);
                warningAlert.setContentText("You have unsaved changes. Do you want to save them?");
                Stage alertStage = (Stage) warningAlert.getDialogPane().getScene().getWindow();
                alertStage.initStyle(StageStyle.UTILITY);
                warningAlert.showAndWait().ifPresent(response->{

                    if(response == yes)
                    {
                        if(filelocation!=null)
                        {
                            try {
                                System.out.println("File Saved !");
                                savecurrenttfile(filelocation, tempfiledata);
                                storefiledatacheck=tempfiledata;
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        else {
                            savepopup(primaryStage);
                        }

                    }
                    if(response == no)
                    {
                        try {
                            openfile(primaryStage);
                            if(filelocation!=null) {
                                txtarea.clear();
                                txtarea.setText(storefiledatacheck);//this will set string obtained from opening
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    if(response == cancel)
                    {
                        warningAlert.close();
                    }






                });




            }


        });
        //4.New Item
        newItem.setAccelerator(new KeyCodeCombination(KeyCode.N,KeyCodeCombination.CONTROL_DOWN));
        newItem.setOnAction(eve->{

            if(filelocation == null && tempfiledata=="")
            {
                txtarea.clear();
                storefiledatacheck="";
                tempfiledata="";
            }
            else if(Objects.equals(tempfiledata,storefiledatacheck))
            {
                txtarea.clear();
                filelocation = null; //for new file
                tempfiledata=storefiledatacheck="";
            }

            else {
                Alert warningAlert = new Alert(AlertType.WARNING);
                warningAlert.setTitle("File Not Saved !");
                warningAlert.setHeaderText("Warning: Unsaved Changes");
                ButtonType yes = new ButtonType("YES");
                ButtonType no = new ButtonType("NO");
                ButtonType cancel = new ButtonType("CANCEL");
                warningAlert.getButtonTypes().setAll(yes,no,cancel);
                warningAlert.setContentText("You have unsaved changes. Do you want to save them?");
                Stage alertStage = (Stage) warningAlert.getDialogPane().getScene().getWindow();
                alertStage.initStyle(StageStyle.UTILITY);

                warningAlert.showAndWait().ifPresent(response->{
                    if(response == yes)
                    {
                        if(filelocation!=null)
                        {
                            try {
                                System.out.println("File Saved !");
                                savecurrenttfile(filelocation, tempfiledata);
                                storefiledatacheck=tempfiledata;
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        else {
                            savepopup(primaryStage);
                        }

                    }
                    if(response == no)
                    {
                        txtarea.clear();
                        filelocation = null;
                        storefiledatacheck="";
                        tempfiledata="";
                    }
                    if(response == cancel)
                    {
                        warningAlert.close();
                    }

                });
            }
        });
        //5.Exit Item
//      	 exItem.setAccelerator(new KeyCodeCombination(KeyCode.F4,KeyCodeCombination.ALT_DOWN));
        exItem.setOnAction(eve->{
            if(Objects.equals(tempfiledata,storefiledatacheck))
            {
                primaryStage.close();
            }
            else {
                Alert warningAlert = new Alert(AlertType.WARNING);
                warningAlert.setTitle("File Not Saved !");
                warningAlert.setHeaderText("Warning: Unsaved Changes");
                ButtonType yes = new ButtonType("YES");
                ButtonType no = new ButtonType("NO");
                ButtonType cancel = new ButtonType("CANCEL");
                warningAlert.getButtonTypes().setAll(yes,no,cancel);
                warningAlert.setContentText("You have unsaved changes. Do you want to save them?");
                Stage alertStage = (Stage) warningAlert.getDialogPane().getScene().getWindow();
                alertStage.initStyle(StageStyle.UTILITY);
                warningAlert.showAndWait().ifPresent(response->{
                    if(response == yes)
                    {
                        if(filelocation!=null)
                        {
                            try {
                                System.out.println("File Saved !");
                                savecurrenttfile(filelocation, tempfiledata);
                                storefiledatacheck=tempfiledata;
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        else {
                            savepopup(primaryStage);
                        }
                        primaryStage.close();
                        storefiledatacheck="";
                        tempfiledata="";
                        filelocation=null;

                    }
                    if(response == no)
                    {
                        primaryStage.close();
                        storefiledatacheck="";
                        tempfiledata="";
                        filelocation=null;
                    }

                    if(response == cancel)
                    {
                        warningAlert.close();

                    }


                });

            }

        });

//      6.Close Button
        primaryStage.setOnCloseRequest(event->{
            exItem.fire();
            event.consume();
        });

        cutMenu.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCodeCombination.CONTROL_DOWN));
        cutMenu.setOnAction(e->{
            txtarea.cut();
        });
        copyMenu.setAccelerator(new KeyCodeCombination(KeyCode.C,KeyCodeCombination.CONTROL_DOWN));
        copyMenu.setOnAction(e->{
            txtarea.copy();
        });
        pasteMenu.setAccelerator(new KeyCodeCombination(KeyCode.V,KeyCodeCombination.CONTROL_DOWN));
        pasteMenu.setOnAction(e->{
            txtarea.paste();
        });
        appearanceItem.setOnAction(e->{
            changePopup(txtarea, primaryStage);
        });


        HBox horizontalarrangeBox = new HBox();
        horizontalarrangeBox.getChildren().addAll(fileMenuBar,editBar);
        BorderPane bdrPane = new BorderPane();
        txtarea.prefWidthProperty().bind(primaryStage.widthProperty());
        txtarea.prefHeightProperty().bind(primaryStage.widthProperty());



        txtarea.setFont(Font.font(textareafontsetting.fontfamily,textareafontsetting.defaultWeight,textareafontsetting.defaultposture,textareafontsetting.fontsize));


        txtarea.setStyle("-fx-border-color: gray; -fx-border-width: 10px; -fx-border-radius: 0px;");
        bdrPane.setCenter(txtarea);
        BorderPane brdPaneMain = new BorderPane();
        brdPaneMain.setTop(horizontalarrangeBox);
        brdPaneMain.setCenter(bdrPane);





        Scene scene = new Scene(brdPaneMain);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setHeight(600);
        primaryStage.setWidth(800);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void openfile(Stage obj) throws IOException {
        ExtensionFilter ex1 = new ExtensionFilter("Text Files", "*.txt");
        ExtensionFilter ex2 = new ExtensionFilter("All Files", "*.*");
        FileChooser openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open File");
        openFileChooser.setInitialDirectory(new File("C:/"));
        openFileChooser.getExtensionFilters().addAll(ex1, ex2);

        File openFile = openFileChooser.showOpenDialog(obj);

        if (openFile != null) {
            filelocation = openFile;
            storefiledatacheck = "";

            // Use InputStreamReader to handle BOM and FileReader
            try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(filelocation), StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String tempString;

                while ((tempString = bReader.readLine()) != null) {
                    content.append(tempString).append(System.lineSeparator());
                }

                storefiledatacheck = content.toString();  // Store content into storefiledatacheck
                tempfiledata = storefiledatacheck;        // Once loaded, it should be current text
            }
        }

    }



    private void savecurrenttfile(File file,String temp) throws IOException {
        FileWriter fWriter = new FileWriter(file);
        BufferedWriter bWriter = new BufferedWriter(fWriter);
        PrintWriter writeinfile = new PrintWriter(bWriter);
        writeinfile.print(temp);
        storefiledatacheck=tempfiledata;
        bWriter.close();
    }

    public void savepopup(Stage obj)
    {
        ExtensionFilter ex1 = new ExtensionFilter("Text Files","*.txt");
        ExtensionFilter ex2 = new ExtensionFilter("All Files","*.*");
        FileChooser saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("SaveAs File");
        saveFileChooser.setInitialDirectory(new File("C:/"));
        saveFileChooser.getExtensionFilters().addAll(ex1,ex2);
        File saveFile = null;
        saveFile = saveFileChooser.showSaveDialog(obj);
        if(saveFile != null)
        {
            filelocation=saveFile;
            try {
                savecurrenttfile(filelocation,tempfiledata);
                storefiledatacheck = tempfiledata; // after file saved tell memory
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
























    public void changePopup(TextArea myTextAreaobj,Stage mainwindowStage) {

        mainwindowStage.getScene().getRoot().setDisable(true);
        // Create a new Stage (pop-up window)
        Stage popupStage = new Stage();

        // Set the stage style to make it a utility window (no taskbar icon)
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Change Appearance");
        AnchorPane popuAnchorPane = new AnchorPane();
        ListView<String> listitem1 = new ListView<>();
        ListView<String> listitem2 = new ListView<>();
        ListView<String> listitem3 = new ListView<>();

        String fontString[] = {"Calibri", "Arial", "Times New Roman", "Verdana", "Tahoma", "Calibri Light", "Cambria", "Georgia", "Segoe UI", "Courier New", "Trebuchet MS", "Comic Sans MS", "Arial Narrow", "Garamond", "Helvetica", "Frank Ruhl Libre", "Century Gothic", "Impact", "Lucida Sans", "Arial Black", "Book Antiqua", "Palatino Linotype", "Consolas", "Century Schoolbook", "Baskerville", "Mongolian Baiti", "Microsoft Sans Serif", "Brush Script MT", "Rockwell", "Rockwell Extra Bold", "Algerian", "Courier", "Lucida Console", "Tahoma Bold", "Monotype Corsiva", "Edwardian Script ITC", "Brush Script Std", "Script MT Bold", "Vivaldi", "French Script MT"};
        String fontstyle[] = {"Regular","Italic","Bold","Bold Italic","Extra Bold"};
        String sizeString[] = new String[43];
        int num=8;
        for(int i=0; i < sizeString.length;i++) {
            sizeString[i] = String.format("\t\t\t%d", num);
            num++;
        }

        for(String tempString : fontString) {
            listitem1.getItems().add(tempString);
        }
        for(String tempString : fontstyle) {
            listitem2.getItems().add(tempString);
        }

        TextField familyRectangle = new TextField();
        familyRectangle.setPrefHeight(20.0);
        familyRectangle.setPrefWidth(250);


        TextField fontstyleRectangle = new TextField();
        fontstyleRectangle.setPrefHeight(20.0);
        fontstyleRectangle.setPrefWidth(200.0);

        TextField fontsizeArea = new TextField();
        fontsizeArea.setPrefHeight(5.0);
        fontsizeArea.setPrefWidth(200.0);


        TextField sampletextareaField = new TextField();
        sampletextareaField.setPrefHeight(100.0);
        sampletextareaField.setPrefWidth(345.0);
        sampletextareaField.setText("ABCDabcd");
        sampletextareaField.setEditable(false);
        sampletextareaField.setAlignment(Pos.CENTER);
        sampletextareaField.setFont(Font.font(tempfamily,tempWeight,tempPosture,tempsize));
        listitem1.setPrefWidth(250);
        listitem1.setPrefHeight(253);


        listitem2.setPrefHeight(100);
        listitem2.setPrefWidth(200);
        listitem3.setPrefHeight(100);
        listitem3.setPrefWidth(200);

        for(String tempString : sizeString) {

            listitem3.getItems().add(tempString);
        }
        // Anchor the ListView to the right and top of the AnchorPane
        Label myLabel = new Label("Font:");
        Label myLabel2 = new Label("Size:");
        Label myLabel3  = new Label("Font Style:");
        Label myLabel4 = new Label("Sample Text:");
        AnchorPane.setLeftAnchor(myLabel, 10.0);
        AnchorPane.setTopAnchor(myLabel, 17.0);
        AnchorPane.setTopAnchor(myLabel2, 170.0);
        AnchorPane.setRightAnchor(myLabel2,188.0);
        AnchorPane.setTopAnchor(myLabel3, 17.0);
        AnchorPane.setRightAnchor(myLabel3, 157.0);




        AnchorPane.setTopAnchor(familyRectangle,40.0);
        AnchorPane.setLeftAnchor(familyRectangle,10.0);
        AnchorPane.setLeftAnchor(listitem1, 10.0);
        AnchorPane.setTopAnchor(listitem1, 65.0);

        AnchorPane.setRightAnchor(listitem2, 10.0);
        AnchorPane.setTopAnchor(listitem2,65.0);
        AnchorPane.setRightAnchor(fontstyleRectangle,10.0);
        AnchorPane.setTopAnchor(fontstyleRectangle, 40.0);
        AnchorPane.setRightAnchor(listitem3, 10.0);
        AnchorPane.setTopAnchor(listitem3,220.0);
        AnchorPane.setTopAnchor(fontsizeArea, 192.0);
        AnchorPane.setRightAnchor(fontsizeArea, 10.0);


        AnchorPane.setBottomAnchor(sampletextareaField, 30.0);
        AnchorPane.setLeftAnchor(sampletextareaField,10.0);
        AnchorPane.setBottomAnchor(myLabel4,130.0);
        AnchorPane.setLeftAnchor(myLabel4, 10.0);

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");
        AnchorPane.setBottomAnchor(cancelButton,10.0);
        AnchorPane.setRightAnchor(cancelButton, 10.0);
        AnchorPane.setBottomAnchor(saveButton,10.0);
        AnchorPane.setRightAnchor(saveButton, 80.0);
        popuAnchorPane.getChildren().addAll(fontsizeArea,fontstyleRectangle,familyRectangle,listitem1,listitem2,listitem3,myLabel,myLabel2,myLabel3,myLabel4,sampletextareaField,cancelButton,saveButton);

        listitem1.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setStyle("");  // Clear any custom styles if the cell is empty
                } else {
                    setText(item);
                    if (item != null) {
                        // Set a custom font for specific items

                        for(String tempString : fontString) {
                            if (item.equals(tempString))
                            {

                                setFont(Font.font(tempString, 14));
                            }

                        }
                    }
                }
            }
        });

        listitem2.setCellFactory(lv-> new ListCell<String>()
        {
            @Override
            protected void updateItem(String item,boolean empty)
            {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setStyle("");  // Clear any custom styles if the cell is empty
                }
                else {
                    setText(item);
                    if(item != null) {
                        if(item.equals("Regular")) {
                            setFont(Font.font("Arial",14));
                        }
                        if(item.equals("Italic"))
                        {
                            setFont(Font.font("Arial", FontPosture.ITALIC, 14));
                        }
                        if(item.equals("Bold"))
                        {
                            setFont(Font.font("Arial", FontWeight.BOLD, 14));
                        }
                        if(item.equals("Bold Italic")) {
                            setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 14));
                        }
                        if(item.equals("Extra Bold")) {
                            setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
                        }


                    }
                }
            }
        });


        fontsizeArea.setText("\t\t\t14");
        fontstyleRectangle.setText("Regular");
        familyRectangle.setText("Consolas");




        listitem1.setOnMouseClicked(e->
        {
            tempfamily = listitem1.getSelectionModel().getSelectedItem();
            sampletextareaField.setFont(Font.font(tempfamily,tempWeight,tempPosture,tempsize));
            familyRectangle.setText(tempfamily);

        });
        listitem3.setOnMouseClicked(e->
        {
            tempsize = Integer.parseInt((listitem3.getSelectionModel().getSelectedItem()).trim());
            sampletextareaField.setFont(Font.font(tempfamily,tempWeight,tempPosture,tempsize));
            fontsizeArea.setText("\t\t\t"+tempsize);
        });

        listitem2.setOnMouseClicked(e->
        {

            String item = listitem2.getSelectionModel().getSelectedItem();
            if(item.equals("Regular")) {
                tempPosture = FontPosture.REGULAR;
                tempWeight = FontWeight.NORMAL;
            }
            if(item.equals("Italic"))
            {
                tempPosture = FontPosture.ITALIC;
                tempWeight = FontWeight.NORMAL;

            }
            if(item.equals("Bold"))
            {
                tempWeight = FontWeight.BOLD;
                tempPosture = FontPosture.REGULAR;
            }
            if(item.equals("Bold Italic")) {
                tempWeight = FontWeight.BOLD;
                tempPosture = FontPosture.ITALIC;

            }
            if(item.equals("Extra Bold")) {
                tempWeight = FontWeight.EXTRA_BOLD;
                tempPosture = FontPosture.REGULAR;
            }


            fontstyleRectangle.setText(item);
            sampletextareaField.setFont(Font.font(tempfamily,tempWeight,tempPosture,tempsize));

        });




        fontsizeArea.setOnKeyTyped(e->
        {
            for(int i = 0 ; i < listitem3.getItems().size() ; i++)
            {
                if((fontsizeArea.getText()).trim().equals(listitem3.getItems().get(i).trim()))
                {
                    listitem3.getSelectionModel().select(i);
                    listitem3.scrollTo(i);
                }
            }
        });

        fontstyleRectangle.setOnKeyTyped(e -> {
            String inputText="";
            inputText = fontstyleRectangle.getText().trim().toLowerCase();
            for(int i = 0 ; i < listitem2.getItems().size() ; i++) {
                if(listitem2.getItems().get(i).toLowerCase().startsWith(inputText))
                {
                    listitem2.getSelectionModel().select(i);
                    listitem2.scrollTo(i);
                }
            }
        });

        familyRectangle.setOnKeyTyped(e -> {
            String inputText="";
            inputText = familyRectangle.getText().trim().toLowerCase();
            for(int i = 0 ; i < listitem1.getItems().size() ; i++) {
                if(listitem1.getItems().get(i).toLowerCase().startsWith(inputText))
                {
                    listitem1.getSelectionModel().select(i);
                    listitem1.scrollTo(i);
                }
            }
        });






        saveButton.setOnMouseClicked(e->{
                    textareafontsetting obj = new textareafontsetting();
                    obj.change(tempsize, tempfamily, tempWeight, tempPosture);
                    myTextAreaobj.setFont(Font.font(textareafontsetting.fontfamily,textareafontsetting.defaultWeight,textareafontsetting.defaultposture,textareafontsetting.fontsize));
                    popupStage.close();
                }
        );

        cancelButton.setOnMouseClicked(e->
        {
            popupStage.close();

        });
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(mainwindowStage);
        mainwindowStage.getScene().getRoot().setDisable(false);

        Scene myScene = new Scene(popuAnchorPane);
        popupStage.setScene(myScene);
        popupStage.setWidth(493);
        popupStage.setHeight(518);
        popupStage.setResizable(false);
        popupStage.show();
    }



}
