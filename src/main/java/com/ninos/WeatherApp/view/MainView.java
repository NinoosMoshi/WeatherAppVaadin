package com.ninos.WeatherApp.view;

import com.ninos.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringUI(path="")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button showWeatherButoon;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label sunRiseLabel;
    private Label sunSetLabel;
    private ExternalResource img;
    private Image iconImage;
    private HorizontalLayout dashBoardMain;
    private VerticalLayout pressureLayout;
    private HorizontalLayout mainDescriptionLayout;
    private VerticalLayout descriptionLayout;


    @Override
    protected void init(VaadinRequest request) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashBoardTitle();
        dashBoardDescription();


        showWeatherButoon.addClickListener(event -> {
           if (! cityTextField.getValue().equals("")){
               try {
                   updateUI();

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }else Notification.show("Please enter a city");


        });


    }




    private void setUpLayout(){
        iconImage = new Image();
        weatherDescription = new Label("Description: Clear Skies");
        weatherMin = new Label("Min: 56F");
        weatherMax = new Label("Max: 89F");
        pressureLabel = new Label("Pressure: 123pa");
        humidityLabel = new Label("Humidity: 34");
        windSpeedLabel = new Label("Wind Speed: 123/hr");
        sunRiseLabel = new Label("SunRise");
        sunSetLabel = new Label("Sunset: ");




       mainLayout = new VerticalLayout();
       mainLayout.setWidth("100%");
       mainLayout.setMargin(true);
       mainLayout.setSpacing(true);

       mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
       setContent(mainLayout);
   }
    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Weather!");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        headerLayout.addComponents(title);

        mainLayout.addComponents(headerLayout);

    }
    private void setLogo() {
       HorizontalLayout logoLayout = new HorizontalLayout();
       logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

       Image icon = new Image(null , new ClassResource("/weather.png"));
       icon.setWidth("125px");
       icon.setHeight("125px");

       logoLayout.addComponents(icon);
       mainLayout.addComponents(logoLayout);

    }
    private void setUpForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setMargin(true);
        formLayout.setSpacing(true);

        //create the selection component
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("40px");
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(1));    // 0 index for "C" and 1 index for "F"
        formLayout.addComponents(unitSelect);

        //Add TextField
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponents(cityTextField);


        //Add Button
        showWeatherButoon = new Button();
        showWeatherButoon.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(showWeatherButoon);


        mainLayout.addComponents(formLayout);



    }
    private void dashBoardTitle() {

      dashBoardMain = new HorizontalLayout();
      dashBoardMain.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);



      currentLocationTitle = new Label("Currently in Spokane");
      currentLocationTitle.setStyleName(ValoTheme.LABEL_H2);
      currentLocationTitle.setStyleName(ValoTheme.LABEL_LIGHT);

     // current Temp Label
        currentTemp = new Label("19F");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.setStyleName(ValoTheme.LABEL_H1);
        currentTemp.setStyleName(ValoTheme.LABEL_LIGHT);




    }
    private void dashBoardDescription() {

        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        // Description Vertical Layout
        descriptionLayout = new VerticalLayout();
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        descriptionLayout.addComponents(weatherDescription);


        descriptionLayout.addComponents(weatherMin);


        descriptionLayout.addComponents(weatherMax);





        //Pressure, humidity etc...
        pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        pressureLayout.addComponents(pressureLabel);


        pressureLayout.addComponents(humidityLabel);


        pressureLayout.addComponents(windSpeedLabel);



        pressureLayout.addComponents(sunRiseLabel);


        pressureLayout.addComponents(sunSetLabel);







    }

    private void updateUI() throws JSONException {
        String city = cityTextField.getValue();
        String defaultUnit;
        String unit;

        if(unitSelect.getValue().equals("F")){
             defaultUnit = "imperial";
             unitSelect.setValue("F");
             unit = "\u00b0"+"F";     // degree sign
        }else{
            defaultUnit = "metric";
            unitSelect.setValue("C");
            unit = "\u00b0"+"C";     // degree sign
        }


        weatherService.setCityName(city);
        weatherService.setUnit(defaultUnit);





        currentLocationTitle.setValue("Currently in "+city);
        JSONObject myObject = weatherService.returnMainObject();
        double temp = myObject.getDouble("temp");  // temp inside bracket is from openweathermap
        currentTemp.setValue(temp + unit);

        //Get min,max,pressure,humidity
        JSONObject mainObject = weatherService.returnMainObject();
        double minTemp = mainObject.getDouble("temp_min");
        double maxTemp = mainObject.getDouble("temp_max");
        int pressure = mainObject.getInt("pressure");
        int humidity = mainObject.getInt("humidity");

        //Get Wind Speed
        JSONObject windObject = weatherService.returnWindObject();
        double wind = windObject.getDouble("speed");

        //Get sunrise and sunset
        JSONObject systemObject = weatherService.returnSunSet();
        Long sunRise = systemObject.getLong("sunrise")*1000 ;
        Long sunSet  = systemObject.getLong("sunset")*1000 ;




        //setup icon Image
        String iconCode = null;
        String description=null;
        JSONArray jsonArray = weatherService.returnWeatherArray();


        for (int i=0; i < jsonArray.length(); i++){
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            description = weatherObject.getString("description");  // description inside bracket from restfull
            iconCode = weatherObject.getString("icon");   // icon inside bracket from restfull

        }

        iconImage.setSource( new ExternalResource("http://openweathermap.org/img/w/"+iconCode+".png"));
        dashBoardMain.addComponents(currentLocationTitle, iconImage ,currentTemp);
        mainLayout.addComponents(dashBoardMain);


        //update Description UI
        weatherDescription.setValue("Cloudiness: "+description);
        weatherMin.setValue("Min: "+String.valueOf(minTemp)+unit);
        weatherMax.setValue("Max: "+String.valueOf(maxTemp)+unit);
        pressureLabel.setValue("Pressure: "+String.valueOf(pressure)+"hpa");
        humidityLabel.setValue("Humidity: "+String.valueOf(humidity)+"%");

        windSpeedLabel.setValue("Wind: "+String.valueOf(wind)+"m/s");

        sunRiseLabel.setValue("Sunrise: "+convertTime(sunRise));
        sunSetLabel.setValue("Sunset: "+convertTime(sunSet));


        mainDescriptionLayout.addComponents(descriptionLayout,pressureLayout);
        mainLayout.addComponents(mainDescriptionLayout);



    }


   private String convertTime(Long time){
       SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy  hh:mm aa");

       return  dateFormat.format(new Date(time));
   }




}


















/*
* try {
           // System.out.println("Data : "+weatherService.getWeather("chicago").getString("coord").toString() );
            JSONArray jsonArray = weatherService.returnWeatherArray("chicago");
            JSONObject myObject = weatherService.returnMainObject("chicago");
            System.out.println( "Pressure: "+myObject.getLong("pressure") );

            for (int i=0; i < jsonArray.length(); i++){
                JSONObject weatherObject = jsonArray.getJSONObject(i);

                System.out.println("Id: "+weatherObject.getInt("id") +
                                   ", main: "+weatherObject.getString("main") +
                                   ", description: "+weatherObject.getString("description"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

* */










