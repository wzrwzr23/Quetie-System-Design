# Architecture Design
![Alt text](/image.png "Architecture Design")
The system comprises an Android app and a Telegram bot as frontends, both connected to a Python-based backend server. The server offers APIs for seamless access to the MySQL database from the Android app. The IoT device, equipped with a camera for image capture, serves as the primary data source.
The IoT device communicates with the Python server, where image data is processed by a Computer Vision (CV) model. Processed data is periodically populated into the MySQL database. This structured approach enables efficient data retrieval through the Android app, forming the foundation of our IoT-Enhanced Campus Dining Solutions. This architecture supports real-time monitoring, analysis, and historical data availability for predictive analytics, contributing to effective crowd management.
# App Features
## Feature 1: Real-Time Monitoring
Feature 1 provides real-time monitoring data of the number of people in the canteen to users. The data recorded by the IoT device will be populated into our SQL database, which will eventually be reflected on the traffic history graph.
By assuming that the number of people in a region is in proportion to the area of the region, we can estimate the total number of people in the canteen by counting the number of people in this region via CV model.
## Feature 2: Crowd Prediction
Feature 2 is designed to enhance the dining experience by providing predictive insights into the crowd dynamics within the canteen. Through an intuitive user interface (UI), individuals can select their preferred date and time for a visit to the canteen, and the system generates an estimate of the expected number of people at that specific moment.
## Feature 3: Dining Insights
Feature 3 provides real-time information on the availability of different food stalls and estimated waiting times, enhancing the dining experience by reducing unnecessary waiting. 
Vendors are able to update their menu and availability any time via a Telegram bot. It will also show the predicted number of total customers, minimising waste and ensuring availability during peak hours.
The Telegram bot handler is on the same server where the Python backend is configured so that it can notify the server to return different results when a modification is made.
