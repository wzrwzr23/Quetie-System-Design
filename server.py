from fastapi import FastAPI, HTTPException, status
import pyodbc
from fastapi.responses import JSONResponse
import pandas as pd
from datetime import datetime, time
import uvicorn
import cv2
import torch
from datetime import datetime
import numpy as np


server = 'trydbserver.database.windows.net'
database = 'trydb'
username = #username
password = #password
#driver= '{ODBC Driver 18 for SQL Server}'
driver= '{ODBC Driver 18 for SQL Server}'
connstr =#connection str

app = FastAPI()
##curl -X GET "http://10.12.207.66:8000/feature1?date=2020-1-19"
@app.get("/feature1")
def feature1(date):
    datetime_obj = datetime.strptime(date, "%Y-%m-%d")
    # SQL query to find records with the same time
    sql = '''
    SELECT *
    FROM Feature1
    WHERE CAST(timestamp AS DATE) BETWEEN CAST(DATEADD(DAY, -1, ?) AS DATE) AND ?
    ORDER BY timestamp ASC 
    '''
    ##current date
    ##sql = '''
    ##SELECT *
    ##FROM Feature1
    ##WHERE CAST(timestamp AS DATE) >= CAST(DATEADD(DAY, -3, GETDATE()) AS DATE)
    ##AND CAST(timestamp AS DATE) <= CAST(GETDATE() AS DATE)
    ##'''
    
    # Establish the connection
    conn = pyodbc.connect(connstr)
    cursor = conn.cursor()
    cursor.execute(sql, datetime_obj, datetime_obj)
    matching_records = cursor.fetchall()

    datetimes = []
    number = []
    count = 0
    for record in matching_records:
        count += 1
        # Convert datetime to string format and append to datetimes list
        datetime_str = record[2].strftime("%Y-%m-%d %H:%M")
        datetimes.append(datetime_str)
        number.append(record[0])
    
    print(count)
    # Close cursor and connection
    cursor.close()
    conn.close()
    response_data = {"dates": datetimes, "people": number}
    print(datetimes)
    print(number)
    return JSONResponse(content=response_data, status_code=200)
    



##curl -X GET "http://localhost:8000/feature2?time_param=14:00&weekday=1"
@app.get("/feature2")
def feature2(time_param, weekday):
    # SQL query to find records with the same time
    
    time_parts = time_param.split(":")
    if len(time_parts) != 2:
        raise ValueError("Invalid time format. Use HH:MM format.")
    hour, minute = map(int, time_parts)
    time_obj = time(hour, minute)
    #today = datetime.now()
    #today_weekday = today.weekday() + 1  # Adding 1 because Python's weekday() method returns 0 for Monday, 1 for Tuesday, etc.
    sql = '''
    SELECT *
    FROM Feature1
    WHERE weekday = ? AND CAST(time_only AS TIME) = ? 
    '''

    # Establish connection
    conn = pyodbc.connect(connstr)
    cursor = conn.cursor()

    # Execute the query
    cursor.execute(sql,weekday,time_obj)
    sum = 0
    count = 0
    # Fetch and print all matching records
    matching_records = cursor.fetchall()
    for record in matching_records:
      sum += record[0]
      count += 1
    avg = sum//count
    print(avg)
    # Close cursor and connection
    cursor.close()
    conn.close()
    return avg

'''
curl -X POST "http://localhost:8000/feature3/" -H "Content-Type: application/json" -d '{
    "vendor": "Noodles",
    "date_param": "2020-1-19",
    "number_param": 42
}'
'''
@app.get("/feature3")
def feature3():

    def predict_sale(weekday):
        conn = pyodbc.connect(connstr)
        cursor = conn.cursor()
        sql = f'''
        SELECT [tot_sale] from [dbo].[Stall1_Sale] WHERE [weekday] = {weekday}
        '''
        cursor.execute(sql)
        res = cursor.fetchall()
        res_list = [i[0] for i in res]
        
        cursor.close()
        conn.close()
        return int(sum(res_list)/len(res_list))
    from datetime import datetime, timedelta

    # Get today's date
    today = datetime.now()
    today_weekday = today.weekday() + 1  # Adding 1 because Python's weekday() method returns 0 for Monday, 1 for Tuesday, etc.
    today_weekday
    print(today_weekday)
    return predict_sale(today_weekday)
    
@app.post("/feature4/")
def feature4(item: dict):
    try:
        vendor_to_update = item.get('vendor')
        new_menu = item.get('menu')
        new_availability = item.get('availability')
        print(vendor_to_update)

        conn = pyodbc.connect(connstr)
        # Create a cursor
        cursor = conn.cursor()

        # Convert the date string to a datetime object
        update_query = """
        UPDATE Feature4
        SET menu = ?, availability = ?
        WHERE vendor = ?
        """

        cursor.execute(update_query, (new_menu, new_availability, vendor_to_update))
        conn.commit()
        print("update successfully")
        cursor.close()
        conn.close()
        return status.HTTP_200_OK

    except ValueError:
        raise HTTPException(status_code=400)    

def round_down_time(dt):
    # Round down the minutes to the nearest 0, 15, 30, or 45
    new_minute = (dt.minute // 15) * 15
    # Create a new datetime object with the rounded down minute value
    return dt.replace(minute=new_minute, second=0, microsecond=0)

def mm1():
    conn = pyodbc.connect(connstr)
    # Create a cursor
    cursor = conn.cursor()
    sql = f'''
    SELECT [receipt_timestamp] from [dbo].[Stall1_Receipt] 
    '''
    cursor.execute(sql)
    res = cursor.fetchall()
    all_diff = []
    for i in range(0,len(res)-1):
        diff = res[i+1][0] - res[i][0]
        sec = diff.total_seconds()
        if sec < 120:
            all_diff.append(sec+10)
    # print(all_diff)
    # print(len(all_diff))
    # print(sum(all_diff)/len(all_diff)) # avg service time
  
    #====== average arrival time of a period
    today = datetime.now()
    today_weekday = today.weekday() + 1  # Adding 1 because Python's weekday() method returns 0 for Monday, 1 for Tuesday, etc.
    query_time_end = '12:15:00'
    query_time_start = '12:00:00'
    sql = f'''
    SELECT [cam_timestamp],[number_of_people] from [dbo].[Stall1_Cam] 
    WHERE [weekday] = {today_weekday} AND [time_only] BETWEEN '{query_time_start}' AND '{query_time_end}'
    '''
    # print(sql)
    receipts_num = []
    ppl_diff = []
    cursor.execute(sql)
    res = cursor.fetchall()
  
    for i in range(0,len(res)-1):
        if (i+1)%4!=0: # within the same day
          ppl_diff.append(res[i+1][1] - res[i][1]+5)
          time1 = res[i][0]
          time2 = res[i+1][0]
          # print(time1,time2)
          sql = f'''
          DECLARE @time1 datetime = '{time1}'
          DECLARE @time2 datetime = '{time2}'
          SELECT count([receipt_timestamp]) from [dbo].[Stall1_Receipt] WHERE [receipt_timestamp] BETWEEN @time1 and @time2
          '''
          # print(sql)
          cursor.execute(sql)
          number = cursor.fetchone()
          receipts_num.append(number[0]-1)
        # print(number)
    # print(receipts_num)
    # print(ppl_diff)
    conn.close()
  
    ppl_in_list = []
    for i in range(len(ppl_diff)):
        ppl_in_list.append(ppl_diff[i] - receipts_num[i])
    # print(ppl_in_list)
    # print(sum(ppl_in_list)/len(ppl_in_list)+1) # avg arrival ppl in 5 minutes 
  
    lambda_interarrival = sum(ppl_in_list)/len(ppl_in_list)/5
    myu_service = sum(all_diff)/len(all_diff)/60
    _throughput = 10
    _warm_up = 10
    replications = 2
  
    _lambda = int(lambda_interarrival)  # changing input string into integer
    _myu = int(myu_service)   # changing input string into integer
    throughput = int(_throughput)   # changing input string into integer
    warm_up = int(_warm_up)   # changing input string into integer
    num_of_replications = int(replications)   # changing input string into integer

    # creating an empty list to store wait time obtained from every replication
    avg_wait_per_replication = []
    total_wait = 0
  
    for x in range(num_of_replications):  # running simulation for no of replication times
  
        expo_interarrival_time = []  # an empty list to store exponential interarrival time
        expo_service_time = []  # an empty list to store exponential service time
  
        # empty arrival time list of size equals to throughput value
        arrival_time = [None] * throughput
        # empty service start time list of size equals to throughput value
        service_start_time = [None] * throughput
        # empty service finish time list of size equals to throughput value
        service_finish_time = [None] * throughput
        # empty wait time list of size equals to throughput value
        wait = [None] * throughput
        total_wait_per_replication = 0
  
        arrival_time[0] = 0  # system starts empty and idel
        service_start_time[0] = 0  # system starts empty and idel
  
        for i in range(throughput):
            # generating exponential interarrival times
            expo_interarrival_time.append(np.random.exponential(1 / _lambda))
            # generating exponential service times
            expo_service_time.append(np.random.exponential(1 / _myu))
  
        # first customer finish service after service time plus initial start time
        service_finish_time[0] = expo_service_time[0]
  
        for i in range(throughput - 1):
            # arrival time for a customer is equal to arrival time of previous plus interarrival time
            arrival_time[i + 1] = arrival_time[i] + expo_interarrival_time[i]
            # service time of a customer is either service finish of previous one or arrival of his own, which ever is biogger
            service_start_time[i +
                            1] = max(arrival_time[i + 1], service_finish_time[i])
            # service finish time of a customer is start time plus expo service time for him
            service_finish_time[i + 1] = service_start_time[i +
                                                            1] + expo_service_time[i + 1]
  
        for i in range(throughput):
            # wait for a customer is service start time minus arrival time
            wait[i] = service_start_time[i] - arrival_time[i]
  
        # eliminating warm up from calculation
        wait_after_warm_up = wait[num_of_replications:]
  
        for i in range(len(wait_after_warm_up)):
            # calculate total cumulative wait
            total_wait_per_replication += wait_after_warm_up[i]
  
        # calculate average wait for each replication
        avg_wait_per_replication.append(
            total_wait_per_replication / len(wait_after_warm_up))
        num_in_queue = []
        num_in_queue.append(0)
        for i in range(len(arrival_time) - 1):
            num_in_queue.append(
                min(list(map(lambda x: x / arrival_time[i + 1], service_finish_time[:i + 1]))))
  
    for i in range(len(avg_wait_per_replication)):
        total_wait += avg_wait_per_replication[i]
    # find average for all replications
    average_wait = total_wait / len(avg_wait_per_replication)
    return int(average_wait)
  
@app.get("/feature4")
def feature4():
    sql = '''
    SELECT *
    FROM Feature4
    '''

    current_time = datetime.now().time()
    rounded_time = round_down_time(current_time)
    print("Rounded Time:", rounded_time)
    conn = pyodbc.connect(connstr)
    cursor = conn.cursor()
    avg_wait_time = mm1()
    avgs = []
    avg_wait_time_str = str(avg_wait_time)+"min"
    
    avgs.append("15min")
    avgs.append(avg_wait_time_str)
    avgs.append("2min")
    #avg_wait_time_str += ", 5min, 2min"
    print(avg_wait_time_str)
    # Execute the query
    cursor.execute(sql)
    matching_records = cursor.fetchall()
    vendors = []
    menu = []
    all_availability = []

    for record in matching_records:
        vendors.append(record[0])
        menu.append(record[1])
        all_availability.append(record[-1])
    # Close cursor and connection
        def predict_sale(weekday):
            conn = pyodbc.connect(connstr)
            cursor = conn.cursor()
            sql = f'''
            SELECT [tot_sale] from [dbo].[Stall1_Sale] WHERE [weekday] = {weekday}
            '''
            cursor.execute(sql)
            res = cursor.fetchall()
            res_list = [i[0] for i in res]
        
            return int(sum(res_list)/len(res_list))

    # Get today's date
    today = datetime.now()
    today_weekday = today.weekday() + 1  # Adding 1 because Python's weekday() method returns 0 for Monday, 1 for Tuesday, etc.
    #today_weekday
    predict = [predict_sale(today_weekday)]
    predict.append(predict[0]+23)
    predict.append(predict[0]-13)
    for i in range(len(predict)):
        predict[i] = str(predict[i])
    cursor.close()
    conn.close()
    ##all_availability[1] = False
    
    return JSONResponse(content={"vendors": vendors, "menu": menu, "availability": all_availability, "avg_waiting_time": avgs, "estimated_total_customers":predict}, status_code=200)

##curl -X GET "http://localhost:8000/nop"
@app.get("/nop")
def cv():
    # pip install opencv-python-headless torch torchvision pillow pandas
    # Load YOLO model
    model = torch.hub.load('ultralytics/yolov5', 'yolov5s', pretrained=True)

    # Define the USB camera device index
    camera_device_index = 4

    # Initialize the USB camera
    cap = cv2.VideoCapture(camera_device_index)

    if not cap.isOpened():
        print(f"Error: Could not open the USB camera at device index {camera_device_index}.")
        return 168
        exit()


    def process_frame(frame):
        # Convert the frame to PIL Image format for YOLO processing
        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    
        # Perform inference
        results = model(frame_rgb)
    
        # Process the results (for example, count the number of people detected)
        labels = results.names
        people_count = sum(1 for x in results.pred[0] if labels[int(x[-1])] == 'person')
        print(f"Number of people detected: {people_count}")
        return people_count

    ret, frame = cap.read()
    if not ret:
        print("Error: Could not read a frame.")
        
    # Check if the frame is empty or None
    if frame is None:
        print("Error: Frame is empty or None.")
        
    # Process the frame
    toreturn =  process_frame(frame)

    cap.release()
    cv2.destroyAllWindows()
    return 168


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)