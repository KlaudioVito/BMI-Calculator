/*
 * @author Klaudio VITO
 * CSC221 - Assignment 7
 * JavaFX BMI Calculator
 * 12/02/2015
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class hw7Controller implements Initializable {

    @FXML private DatePicker datePicker; //used to connect to DatePicker in fxml file
    @FXML private Label heightLabel, weightLabel, bmiLabel, ageLabel,
            maxHeartRateLabel, lowTargetLabel,highTargetLabel,
            underweight, normal, overweight, obese; //these are all labels used to connect to fxml file
    @FXML private Slider heightSlider, weightSlider, ageSlider; //Sliders used
    @FXML private double height = 65.0, weight = 100.0, bmi = 16.6, heartRate,
            lowTarget,highTarget ; //these are the variables that are used during the program
    @FXML final List<String> dateList = new ArrayList<>(); //an array list of strings to hold the dates
    @FXML final ArrayList<Double> bmiList  = new ArrayList<>(); //an array list of doubles to hold the bmis
    @FXML private LineChart<String,Double> lineChart; //a lineChart to plot the data
    @FXML XYChart.Series series = new XYChart.Series(); //the series that will be ploted
    @FXML Map <String, Double> map = new TreeMap<>(); //a map which will be used to sort the inputs
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        underweight.setTextFill(Color.CHOCOLATE); //set text color to Chocolate for underweight
        normal.setTextFill(Color.DARKGREEN); //set text color fro normal
        overweight.setTextFill(Color.FUCHSIA); // set text color for overweight
        obese.setTextFill(Color.RED);// set text color for obese
        bmiLabel.setTextFill(Color.CHOCOLATE);// set initial text color for bmiLabel
        lineChart.setLegendVisible(false); //remove legend since we only have one series
        
        //action listener for the height slider
        heightSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, 
                    Number oldValue, Number newValue) {
                heightLabel.setText(String.format("%.0f",newValue)); //set the text of the label to the new value
                height = (double) newValue; //get the new value
                bmi = (weight * 703)/(height*height); //calculate bmi
                //if statements to change the color of bmi text according to bmi
                if (bmi <= 18.5)
                    bmiLabel.setTextFill(Color.CHOCOLATE);
                else if(bmi > 18.5 && bmi < 24.9)
                    bmiLabel.setTextFill(Color.DARKGREEN);
                else if(bmi >= 25 && bmi < 29.9)
                    bmiLabel.setTextFill(Color.FUCHSIA);
                else if(bmi >= 30)
                    bmiLabel.setTextFill(Color.RED);
                bmiLabel.setText(String.format("%.1f", bmi)); //set bmiLabel accordingly
            }
        });
        //action listener for the weight slider
        weightSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, 
                    Number oldValue, Number newValue) {
                weightLabel.setText(String.format("%.0f",newValue)); //assign new value to text
                weight = (double) newValue; //get weight value
                bmi = (weight * 703)/(height*height); //calculate bmi
                //if statements to change bmi color according to bmi
               if (bmi <= 18.5)
                    bmiLabel.setTextFill(Color.CHOCOLATE);
                else if(bmi > 18.5 && bmi < 24.9)
                    bmiLabel.setTextFill(Color.DARKGREEN);
                else if(bmi >= 25 && bmi < 29.9)
                    bmiLabel.setTextFill(Color.FUCHSIA);
                else if(bmi >= 30)
                    bmiLabel.setTextFill(Color.RED);
                bmiLabel.setText(String.format("%.1f", bmi)); //display bmi in its label   
            }
        });
        //action listener for the age slider
        ageSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, 
                    Number oldValue, Number newValue) {
                ageLabel.setText(String.format("%.0f",newValue)); //assign new value as text
                heartRate = (220 -(double) newValue); //get maximum heart rate
                maxHeartRateLabel.setText(String.format("%.0f", heartRate));//display max heart rate
                lowTarget = 0.5*heartRate; //calculate low target heart rate
                lowTargetLabel.setText(String.format("%.0f", lowTarget));//display low target heart rate in label
                highTarget = 0.85*heartRate;//calculate high target heart rate
                highTargetLabel.setText(String.format("%.0f", highTarget));//display high target heart rate in label
            }
        });
    }
    
    //method to exit the program
    @FXML
    private void quitPressed(ActionEvent event) {
        System.exit(0);
    }
    
    //method to append data
    @FXML
    private void appendData(ActionEvent event) {
            LocalDate date = datePicker.getValue(); //get value from date picker
            dateList.add(date.toString()); //append this to the date List
            bmi = (double)Math.round(bmi * 10d) / 10d; //round bmi to 1 significant digit
            bmiList.add(bmi); //append bmi to bmi list
            map.put(date.toString(), bmi); //insert data-bmi pair to map
            
            //for loop to construct data series
            for(Map.Entry<String, Double> entry : map.entrySet()){ 
                series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
            }
            //set data to the chart through the chartData() method explained later
            lineChart.setData(chartData());
    }
    
    //method to last data
    @FXML
    private void removeData(ActionEvent event){
        try{
            series.getData().remove(series.getData().size()-1); //remove last element from series
            dateList.remove(dateList.size()-1); //remove last element from dateList
            bmiList.remove(bmiList.size()-1);//remove last element from bmi List
            map.clear(); //clear the map
            Iterator i = dateList.iterator(); //declare list iterator for date List
            Iterator j = bmiList.iterator(); //declare list iterator for bmi list
               
            //assign new data to map through two while statements
            while(i.hasNext()){
                while(j.hasNext())
                    map.put(i.next().toString(), Double.parseDouble(j.next().toString()));
            }
            //construct the series with the new data
            for(Map.Entry<String, Double> entry : map.entrySet()){ 
                series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
            }
            lineChart.setData(chartData());//plot new data 
        }catch(RuntimeException e){
            System.out.println("Exception occured: " + e);
        }  
    }
    //method to save file
    @FXML
    private void saveFile(){
        FileChooser fileChooser = new FileChooser(); //initialize file chooser
        fileChooser.setTitle("Save as"); //set the title of the window
        fileChooser.setInitialFileName("myBMI's.txt"); //set default file name
        File file = fileChooser.showSaveDialog(null); //show the dialog box
        
        if(file != null){
            try{
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
                FileWriter writer = new FileWriter(file);
                
                writer.write(String.format("%-16s%s%n%s%n", 
                        "Date","BMI",
                        "------------------------" ));
                for(Map.Entry<String, Double> entry : map.entrySet()){ 
                
                    writer.write(String.format("%-16s%s%n",entry.getKey(), entry.getValue()));
                }
                writer.close();
            }catch(IOException e){
                System.out.println("Exception occured: " + e);
            }
        }
    }
    
    @FXML
    private void openFile() {
        dateList.clear();
        bmiList.clear();
        series.getData().clear();
        map.clear();
        
	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
	fileChooser.getExtensionFilters().addAll( new ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);                 
        if(selectedFile != null){           
            try{
                
                FileReader fr = new FileReader(selectedFile);
                BufferedReader o = new BufferedReader(fr);
                for(int i=1;i < 3;i++) {
                        o.readLine();
                }
                String s;
                while((s = o.readLine())!= null){
                    
                    String values[] = s.split("\\s+");
                    for(int i = 0; i < values.length; i++){
                        if(i%2 == 0)
                            dateList.add(values[i]);
                        else
                            bmiList.add(Double.parseDouble(values[i]));
                    }
                }
            }catch(FileNotFoundException e){
                System.out.println("Exception occured: " + e);
            }catch(IOException e){
                System.out.println("Exception occured: " + e);                    
            }
            Iterator i = dateList.iterator();
                Iterator j = bmiList.iterator();
            while(i.hasNext()){
                    while(j.hasNext())
                        map.put(i.next().toString(), Double.parseDouble(j.next().toString()));
                }
            for(Map.Entry<String, Double> entry : map.entrySet()){ 
                    series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
                    
                }
            lineChart.setData(chartData());
        }
    }
    
    private ObservableList<XYChart.Series<String, Double>> chartData(){
        ObservableList<XYChart.Series<String, Double>> output = FXCollections.observableArrayList();
        Series<String, Double> chart = new Series<>();
        for(Map.Entry<String, Double> entry : map.entrySet()){
            chart.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }
        output.addAll(chart);
        return output;
    }
}

