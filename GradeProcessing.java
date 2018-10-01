/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFX_DatabaseProcessing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author chengzhongito
 */
public class GradeProcessing extends Application{
    private boolean btnPressed;
    
    public void start(Stage primaryStage){  
        BorderPane backPane = new BorderPane();
        
       //Create pane for all text fields layouts
        GridPane textPane = new GridPane();
        textPane.setHgap(5);
        textPane.setVgap(5);
        textPane.setPadding(new Insets(10,10,10,10));
        TextField stu_id = new TextField();
        stu_id.setAlignment(Pos.BASELINE_RIGHT);
        TextField stu_name = new TextField();
        stu_name.setAlignment(Pos.BASELINE_RIGHT);
        TextField quiz = new TextField();
        quiz.setAlignment(Pos.BASELINE_RIGHT);
        TextField a1 = new TextField();
        a1.setAlignment(Pos.BASELINE_RIGHT);
        TextField a2 = new TextField();
        a2.setAlignment(Pos.BASELINE_RIGHT);
        TextField a3 = new TextField();
        a3.setAlignment(Pos.BASELINE_RIGHT);
        TextField exam = new TextField();
        exam.setAlignment(Pos.BASELINE_RIGHT);
      //Add labels in the text pane
        textPane.add(new Label("Student ID"),0,0);
        textPane.add(new Label("Student name"),0,1);
        textPane.add(new Label("Quiz"),0,2);
        textPane.add(new Label("A1"),0,3);
        textPane.add(new Label("A2"),0,4);
        textPane.add(new Label("A3"),0,5);
        textPane.add(new Label("Exam"),0,6);
        textPane.add(new Label("Results"),0,7);
        textPane.add(new Label("Grade"),0,8);
        
        Label results = new Label("0.00");
        results.setAlignment(Pos.CENTER);
        Label grade = new Label("N/A");
        grade.setAlignment(Pos.BASELINE_RIGHT);
      //Add all text fields in the pane  
        textPane.add(stu_id,1,0);
        textPane.add(stu_name,1,1);
        textPane.add(quiz,1,2);
        textPane.add(a1,1,3);
        textPane.add(a2,1,4);
        textPane.add(a3,1,5);
        textPane.add(exam,1,6);
        textPane.add(results,1,7);
        textPane.add(grade,1,8);
        
        Button calBtn = new Button("Calculate");
        calBtn.setPrefWidth(100);
        Button insertBtn = new Button("Insert");
        insertBtn.setPrefWidth(100);
        Button updateBtn = new Button("Update");
        updateBtn.setPrefWidth(100);
        Button clearBtn = new Button("Clear");
        clearBtn.setPrefWidth(100);
       //Add buttons in the layoutss 
        textPane.add(calBtn,0,9);
        textPane.add(insertBtn,1,9);
        textPane.add(updateBtn,1,10);
        textPane.add(clearBtn,0,10);
       
        //Create and add search functoin and display areas
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(10,10,10,10));
        Button searchBtn = new Button("Search");
        Button clearResult = new Button("Clear");
      //Create content for combobox  
        ObservableList<String> options = FXCollections.observableArrayList(
        "ID","Name","Quiz","A1","A2","A3","Exam","Grade");
        ComboBox searchList = new ComboBox(options);
        searchList.setItems(options);
      //Set default status for combobox  
        searchList.setVisibleRowCount(3);
        searchList.getSelectionModel().selectFirst();
        TextField searchText = new TextField();
        searchText.setPrefWidth(10);
        searchText.setAlignment(Pos.BASELINE_RIGHT);
      //Create scroll pane for displaying results  
        ScrollPane sp = new ScrollPane();
        sp.setPrefViewportHeight(200);
        sp.setPrefViewportWidth(400);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        searchBox.getChildren().addAll(new Label("Search records by "),searchList,searchText,searchBtn,sp,clearResult);
        
      //Calculation button function  
        calBtn.setOnAction(e -> {
          //Get result and grade from calculation method and set texts  
            double stu_result = calculation(quiz.getText(),a1.getText(),a2.getText(),a3.getText(),exam.getText());
            String stu_grade = getGrade(stu_result);
            results.setText(String.valueOf(stu_result));
            grade.setText(stu_grade);
        });
      //insert button funciton  
        insertBtn.setOnAction(e ->{
          //Check if input scores are in the required range  
            boolean scoreRange = getInputcorrect(quiz,a1,a2,a3,exam);
            if (scoreRange){
          //Check whether the input ID has already existed in database
          //If not, insert record and clear text fields
            btnPressed = checkID(stu_id,stu_name,quiz,a1,a2,a3,exam);
            if (btnPressed){
                clear(stu_id,stu_name,quiz,a1,a2,a3,exam,results,grade);
                AlertMessage("Record inserted!",false);
            }
            } 
            else{
              //Show alert message is input scores are incorrect  
                AlertMessage("Scores must be between 0 and 100.", true);
            }
        });
     //Update button event handler   
        updateBtn.setOnAction(e ->{
          //First check if input is valid  
            boolean scoreRange = getInputcorrect(quiz,a1,a2,a3,exam);
            if (scoreRange){
              //Check whether ID exist in database  
                boolean searchID = idExist(stu_id.getText());
                if (searchID){
                 //Update record in database with DBupdate method 
                    boolean updated = DBupdate(stu_id,stu_name,quiz,a1,a2,a3,exam,results,grade);
                  //Clear all textFields and pop message to inform user  
                    if (updated){
                        clear(stu_id,stu_name,quiz,a1,a2,a3,exam,results,grade);
                        AlertMessage("Record updated.",false);
                    }
                  //Inform user if update fails  
                    else{
                        AlertMessage("Query has issue.",false);
                    }
                }
                else{
                    AlertMessage("This ID does not exisit!", false);
                }
            } 
            else{
                AlertMessage("Scores must be between 0 and 100.", true);
            }
        });
      //Search button event handler  
        searchBtn.setOnAction(e ->{
            ArrayList<String> list = new ArrayList();
          //Retrive value from combobox   
            String selected = searchList.getValue().toString();
          //Retrive records from database based on selected item in combobox  
            switch (selected){
                case("ID"):{
                    list = DBsearch(1,searchText.getText());
                    break;
                }
                case("Name"):{
                    list = DBsearch(2,searchText.getText());
                    break;
                }
                case("Quiz"):{
                    list = DBsearch(3,searchText.getText());
                    break;
                }
                case("A1"):{
                    list = DBsearch(4,searchText.getText());
                    break;
                }
                case("A2"):{
                    list = DBsearch(5,searchText.getText());
                    break;
                }
                case("A3"):{
                    list = DBsearch(6,searchText.getText());
                    break;
                }
                case("Exam"):{
                    list = DBsearch(7,searchText.getText());
                    break;
                }
                case("Grade"):{
                    list = DBsearch(9,searchText.getText());
                    break;
                }
            }
            if (list.size()<=1){
                AlertMessage("Record does not found in database!",true);
            }
         //Display records in the pane if records found in database   
            else{
                GridPane display = new GridPane();
                display.setHgap(10);
                String[] dataList = {};
                int row = 0;
          // Transer data into grid pane     
            for (String content : list){
                dataList = content.split(" ");
                display.add(new Label(dataList[0]),0,row);
                display.add(new Label(dataList[1]),1,row);
                display.add(new Label(dataList[2]),2,row);
                display.add(new Label(dataList[3]),3,row);
                display.add(new Label(dataList[4]),4,row);
                display.add(new Label(dataList[5]),5,row);
                display.add(new Label(dataList[6]),6,row);
                display.add(new Label(dataList[7]),7,row);
                display.add(new Label(dataList[8]),8,row);
                row++;
            }
          //Set scroll pane content by the gridpane node 
            sp.setContent(display);
            }
        });
      //Clear button on search side to clear all search textFields  
        clearResult.setOnAction(e ->{
            searchText.clear();
            sp.setContent(new Label(""));
        });
      //Clear button on input side to clear all textFields  
        clearBtn.setOnAction(e->{
            clear(stu_id,stu_name,quiz,a1,a2,a3,exam,results,grade);
        });
     //Set all panes into main boarder pane and scene and stage  
        backPane.setLeft(textPane);
        backPane.setRight(searchBox);
        Scene scene = new Scene(backPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
  //Method to check if input in valid
    public boolean checkID(TextField id,TextField name,TextField quiz,TextField a1, TextField a2,TextField a3,TextField exam){
        boolean insertCheck = false;
       //Check if all textFields are entered  
        if (id.getText().length() == 0 || name.getText().length() ==0 || quiz.getText().length()==0 ||
                a1.getText().length()==0 || a2.getText().length()== 0 || a3.getText().length()==0 || exam.getText().length()==0){
                AlertMessage("All columns must be entered!",true);
            }
        else {
            //Check if ID contain 8 digital number
                if (id.getText().length() != 8){
                    AlertMessage("ID must be 8 digit numbers",true);
                }
                else{
                  //Check if ID exist in databse  
                    boolean checkId = idExist(id.getText().toString());
                    if (checkId){
                      AlertMessage("ID exist",false);
                    }
                  //If not exist, insert record into databse with DBinsert method.  
                    else{
                        insertCheck = DBinsert(id,name,quiz,a1,a2,a3,exam);
                        }
                }
            }
        return insertCheck;  //Return the value whether record is successfully inserted
    }
    
   
    public static void main(String[] args) {
        launch(args);
    }
  // DBinsert method  
    protected boolean DBinsert(TextField id,TextField name,TextField quiz,TextField a1,TextField a2,TextField a3, TextField exam){
        boolean inserted = false;
        try{  
           //Open connection to database 
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/GradeProcessing","root","sam11001");
           //Calculate results and grade 
            double results = calculation( quiz.getText().toString(), a1.getText().toString(), a2.getText().toString(), a3.getText().toString(), exam.getText().toString());
            String grade = getGrade(results);
          //Create query statement  
            Statement stmt=con.createStatement();
            String query = "INSERT INTO Java2 (stu_id,stu_name, score_quiz, score_a1, score_a2 , "
                    + "score_a3 , score_exam , result , stu_grade) VALUE ("
                    + "'"+id.getText().toString()+ "','"+name.getText().toString()+ "',"+Integer.parseInt(quiz.getText().toString())
                    + ","+Integer.parseInt(a1.getText().toString())+ ","+Integer.parseInt(a2.getText().toString())
                    + ","+Integer.parseInt(a3.getText().toString())+ ","+Integer.parseInt(exam.getText().toString())
                    +","+String.format("%3.2f",results)+",'"+grade+"')";
           //Get value whether insert is successful 
            int rs = stmt.executeUpdate(query); 
            if (rs>0){
                inserted = true;
            }
            else{
                AlertMessage("Something's wrong!",false);
            }
            con.close(); 
        }catch(Exception e)
        { System.out.println(e);} 
        finally{
            return inserted;
        }
    }
//DBupdate method
    protected boolean DBupdate(TextField id,TextField name,TextField quiz,TextField a1,TextField a2,TextField a3, TextField exam,Label results,Label grade){
        boolean dbUpdate = false;
        try{  
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/GradeProcessing","root","sam11001");  
            Statement stmt=con.createStatement();  
          //Get resultSet from database  
            ResultSet rs = stmt.executeQuery("SELECT * FROM Java2 WHERE stu_id = '"+id.getText().toString()+"';");
            rs.next();
            String[] attribute = new String[10];
          //Store attributes into String array  
            for(int i=0;i<9;i++){
                attribute[i] = rs.getString(i+1);
            }
            
           
            if (!name.getText().equals("")){
                attribute[1] = name.getText();
                //calculation( quiz, a1, a2, a3,  exam);
            }
            if (!quiz.getText().equals("")){
                attribute[2] = quiz.getText();
             //   String query = "UPDATE Java2 SET score_quiz = '"+attribute[2]+"' WHERE stu_id = '"+ id.getText().toString()+"';";
            //int urss = stmt.executeUpdate(query);
            //System.out.println(urss);
                //calculation( quiz, a1, a2, a3,  exam);
            }
            if (!a1.getText().equals("")){
                attribute[3] = a1.getText();
               // calculation( quiz, a1, a2, a3,  exam);
            }
            if (!a2.getText().equals("")){
                attribute[4] = a2.getText();
               // calculation( quiz, a1, a2, a3,  exam);
            }
            if (!a3.getText().equals("")){
                attribute[5] = a3.getText();
                //calculation( quiz, a1, a2, a3,  exam);
            }
            if (!exam.getText().equals("")){
                attribute[6] = exam.getText();
            }
            double resultUpdated = calculation(attribute[2],attribute[3],attribute[4],attribute[5],attribute[6]);
            String gradeUpdated = getGrade(resultUpdated);
            
            String query = "UPDATE Java2 SET stu_name = '"+attribute[1]+"', score_quiz = "+Integer.parseInt(attribute[2])+", score_a1 = "+Integer.parseInt(attribute[3])+", score_a2 = "
                    + Integer.parseInt(attribute[4])+", score_a3 = "+Integer.parseInt(attribute[5])+", score_exam = "+Integer.parseInt(attribute[6])+", result = '"+resultUpdated+"', stu_grade = '"+gradeUpdated
                    +"' WHERE stu_id = '"+ id.getText().toString()+"';";
            int urs = stmt.executeUpdate(query);
          //System.out.println(urs);
            con.close();  
            if (urs>0){
                dbUpdate = true;
            }
            
        }
        catch(Exception e){ System.out.println(e);}  
        return dbUpdate;
    } 
     
    protected ArrayList DBsearch(int column, String value){
            ArrayList<String> searchResult = new ArrayList();
            
            try{  
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/GradeProcessing","root","sam11001"); 
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.getResultSet();
            
            switch(column){
                case(1):{
                    String query = "SELECT * FROM Java2 WHERE stu_id = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(2):{
                    String query = "SELECT * FROM Java2 WHERE stu_name = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(3):{
                    String query = "SELECT * FROM Java2 WHERE score_quiz = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(4):{
                    String query = "SELECT * FROM Java2 WHERE score_a1 = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(5):{
                    String query = "SELECT * FROM Java2 WHERE score_a2 = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(6):{
                    String query = "SELECT * FROM Java2 WHERE score_a3 = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(7):{
                    String query = "SELECT * FROM Java2 WHERE score_exam = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
                case(9):{
                    String query = "SELECT * FROM Java2 WHERE stu_grade = '"+value+"';";
                     rs =stmt.executeQuery(query);
                    break;
                }
            }
            searchResult.add(String.format("%s %s %s %s %s %s %s %s %s","ID","Name","Quiz",
                    "A1","A2","A3","Exam","Result","Grade"));
            while(rs.next()){
                searchResult.add(String.format("%s %s %s %s %s %s %s %s %s", rs.getString(1),
                        rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),
                        rs.getString(7),rs.getDouble(8),rs.getString(9)));
            }
            con.close();  
        }
        catch(Exception e){ System.out.println(e);}  
        finally{
            return searchResult;
        }
    }
    
    protected boolean idExist(String id){
        boolean inputIDexist = false;
        if (!id.equals("")){
        try{  
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/GradeProcessing","root","sam11001");
          
            Statement stmt=con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Java2 WHERE stu_id = '"+id+"';");
            
            rs.next();
            if(rs.getString(1).contains(id)){
                inputIDexist = true;
            }
            else{
                inputIDexist = false;
                
            }
            con.close();
            
            
        }catch(Exception e)
        { System.out.println(e);} 
        finally{
           return inputIDexist; 
        }
        }
        else
            return inputIDexist;
    }
    
    
    protected void clear(TextField id,TextField name,TextField quiz,TextField a1,TextField a2,TextField a3, TextField exam, Label result,Label grade){
        id.clear();
        name.clear();
        quiz.clear();;
        a1.clear();
        a2.clear();
        a3.clear();
        exam.clear();
        result.setText("0.00");
        grade.setText("N/A");
    }
        
    protected void AlertMessage(String msg,boolean errorType){
        
        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
        if (errorType){
            errorAlert.setTitle("Input error");
            
        }
        else{
            errorAlert.setTitle("Databse update error");
        }
        
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(msg);
        errorAlert.showAndWait();
        
    }
    
    protected double calculation(String quiz,String a1,String a2,String a3, String exam){
        int inputQuiz,inputA1,inputA2,inputA3,inputExam;
                
        if (quiz.equals("")){
            inputQuiz = 0;
        }
        else{
            inputQuiz = Integer.parseInt(quiz);
        }
        if (a1.equals("")){
            inputA1 = 0;
        }
        else{
            inputA1=Integer.parseInt(a1);
        }
        if (a2.equals("")){
            inputA2 = 0;
        }
        else{
            inputA2=Integer.parseInt(a2);
        }
        if (a3.equals("")){
            inputA3 = 0;
        }
        else{
            inputA3=Integer.parseInt(a3);
        }
        if (exam.equals("")){
            inputExam = 0;
        }
        else{
            inputExam=Integer.parseInt(exam);
        }
        
        double results = (inputQuiz*0.05)+(inputA1*0.15)+(inputA2*0.2)+(inputA3*0.1)+(inputExam*0.5);
        //double results = (Integer.parseInt(quiz.getText())*0.05)+ (Integer.parseInt(a1.getText())*0.15)
             //   + (Integer.parseInt(a2.getText())*0.2) + (Integer.parseInt(a3.getText())*0.1)
             //       +(Integer.parseInt(a1.getText().toString())*0.5);
        return results;
    }
    
    protected String getGrade(double results){
        
        String grade;
            if (results >= 85){
                grade = "HD";
            }
            else if (results <85 && results >= 75){
                grade = "DI";
            }
            else if (results <75 && results >= 65){
                grade = "CR";
            }
            else if (results <65 && results >= 50){
                grade = "PS";
            }
            else  {
                grade = "FL";
            }
            
            return(grade);
        
    }
    
    protected boolean getInputcorrect(TextField quiz,TextField a1, TextField a2,TextField a3,TextField exam){
        int inputCorrect =0;
        int quizC = 0;
        int a1C =0;
        int a2C = 0;
        int a3C = 0;
        int examC = 0;
        
        if(quiz.getText().matches("[0-9]+")){
            quizC = Integer.parseInt(quiz.getText());
            if(quizC>=0 && quizC<=100)
                inputCorrect +=1;
        }
        else
            inputCorrect +=1;
            
        
        if(quiz.getText().matches("[0-9]+")){
            a1C = Integer.parseInt(quiz.getText());
            if(a1C>=0 && a1C <=100 )
                inputCorrect +=1;
        }
        else
            inputCorrect +=1;
        
        if(quiz.getText().matches("[0-9]+")){
            a2C = Integer.parseInt(quiz.getText().toString());
            if(a2C>=0 && a2C <=100 )
                inputCorrect +=1;
        }
        else
            inputCorrect +=1;
        
        if(quiz.getText().matches("[0-9]+")){
            a3C = Integer.parseInt(quiz.getText().toString());
            if(a3C>=0 && a3C <=100 )
                inputCorrect +=1;
        }
        else
            inputCorrect +=1;
        
        if(quiz.getText().matches("[0-9]+")){
            examC = Integer.parseInt(quiz.getText().toString());
            if(examC>=0 && examC <=100 )
                inputCorrect +=1;
        }
        else
            inputCorrect +=1;
        
        if (inputCorrect == 5)
            return true;
        else
            return false;
    }
        
    
}
