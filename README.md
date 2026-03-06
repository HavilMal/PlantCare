# PlantCare

## Overview
PlantCare is a mobile application written with Jetpack compose that allows users to monitor their plants health and keep track of the watering schedule.
Core functionalities are:
- Managing list of your plants
- Listing plants needing watering in current day
- Keeping track of users watering streak
- Connecting to a sensor for real time plant health data
- Providing users with tips on plant care

## Running
To work properly PlantCare is using data from following APIs:
- https://perenual.com/
- https://openweathermap.org/
 
Import this project in Android Studio, sync it and then add following to `local.properties`:
```properties
PLANT_API_KEY="your_perenual_api_key"
WEATHER_API_KEY="your_openweathermap_api_key"
```
With proper keys to those services.

Then build and run the app.

## Screenshots
### Home Screen
![home_screen](https://github.com/user-attachments/assets/2d1af8cc-f89a-425b-8d85-58bf7054d7ac)

### Plant List
 ![plant_list](https://github.com/user-attachments/assets/4bcae116-89d3-4c2a-a77f-bf4d6563520f)

### Plant Screen
![plant_screen](https://github.com/user-attachments/assets/208bd87e-6d3f-4171-a060-f7fd586727b6)
![plant_screen_2](https://github.com/user-attachments/assets/cc6c1e83-8d39-4e47-9dd8-f3a2fbabd3ac)

### Plant Edition
![plant_edit](https://github.com/user-attachments/assets/029ccf2b-6ff4-46dd-bcde-7bd0fac76e8c)

### Calendar Screen
![calendar_screen](https://github.com/user-attachments/assets/959aff0f-6865-4893-b9cd-51022172a810)

