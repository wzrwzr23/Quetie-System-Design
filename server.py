from fastapi import FastAPI, HTTPException
import pyodbc
from fastapi.responses import JSONResponse
import pandas as pd
from datetime import datetime, time
import uvicorn


server = 'trydbserver.database.windows.net'
database = 'trydb'
username = 'sqladmin@trydbserver.com'
password = 'Capstone1'
#driver= '{ODBC Driver 18 for SQL Server}'
driver= '{ODBC Driver 18 for SQL Server}'
connstr = 'Driver={ODBC Driver 18 for SQL Server};Server=tcp:trydbserver.database.windows.net,1433;Database=trydb;Uid=sqladmin;Pwd=Capstone1;Encrypt=yes;TrustServerCertificate=no;Connection Timeout=30;'\

app = FastAPI()
##curl -X GET "http://10.12.207.66:8000/feature1?date=2020-1-19"
@app.get("/feature1")
def feature1(date):
    datetime_obj = datetime.strptime(date, "%Y-%m-%d")
    # SQL query to find records with the same time
    sql = '''
    SELECT *
    FROM Feature1
    WHERE CAST(timestamp AS DATE) BETWEEN CAST(DATEADD(DAY, -3, ?) AS DATE) AND ?
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
    return JSONResponse(content=response_data, status_code=200)


##curl -X GET "http://localhost:8000/feature2?time_param=14:00"
@app.get("/feature2")
def feature2(time_param):
    # SQL query to find records with the same time

    time_parts = time_param.split(":")
    if len(time_parts) != 2:
        raise ValueError("Invalid time format. Use HH:MM format.")
    hour, minute = map(int, time_parts)
    time_obj = time(hour, minute)

    sql = '''
    SELECT *
    FROM Feature1
    WHERE CAST(time_only AS TIME) = ?
    '''

    # Establish connection
    conn = pyodbc.connect(connstr)
    cursor = conn.cursor()

    # Execute the query
    cursor.execute(sql,time_obj)
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
    return JSONResponse(content={"average": avg}, status_code=200)

'''
curl -X POST "http://localhost:8000/feature3/" -H "Content-Type: application/json" -d '{
    "vendor": "Noodles",
    "date_param": "2023-11-26",
    "number_param": 42
}'
'''
@app.post("/feature3/")
def feature3(item: dict):
    try:
        vendor = item.get('vendor')
        date_param = item.get('date_param')
        number_param = item.get('number_param')
        # Convert the date string to a datetime object
        datetime_obj = datetime.strptime(date_param, "%Y-%m-%d")
        numberOfOrder = int(number_param)

        conn = pyodbc.connect(connstr)
        
        # Create a cursor
        cursor = conn.cursor()

        # Define SQL query for inserting a record
        insert_query = "INSERT INTO Feature3 (vendor, order_date, number_of_total_order) VALUES (?, ?, ?)"
        # Define values for the record
        values = (vendor, datetime_obj, numberOfOrder)

        try:
            # Execute the insert query
            cursor.execute(insert_query, values)
            conn.commit()
            print("Record inserted successfully.")
        except Exception as e:
            # Handle any exceptions
            print(f"Error: {str(e)}")
            conn.rollback()
        finally:
            sql = '''
            SELECT *
            FROM Feature3
            WHERE vendor = ?
            '''
            cursor.execute(sql, vendor)
            sum = 0
            count = 0
            # Fetch and print all matching records
            matching_records = cursor.fetchall()
            for record in matching_records:
                sum += record[1]
                count += 1
            # Close the cursor and connection
            avg = sum//count
            cursor.close()
            conn.close()
            return JSONResponse(content={"average": avg}, status_code=200)
    

    except ValueError:
        raise HTTPException(status_code=400)
    
@app.post("/feature4/")
def feature4(item: dict):
    try:
        vendor_to_update = item.get('vendor')
        new_menu = item.get('menu')
        new_availability = item.get('availability')
        print(vendor_to_update)
        print(new_availability)
        print(new_menu)

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

    except ValueError:
        raise HTTPException(status_code=400)    



@app.get("/feature4")
def feature4():
    sql = '''
    SELECT *
    FROM Feature4
    '''

    conn = pyodbc.connect(connstr)
    cursor = conn.cursor()

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
    cursor.close()
    conn.close()
    return JSONResponse(content={"vendors": vendors, "menu": menu, "availability": all_availability}, status_code=200)


    

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)