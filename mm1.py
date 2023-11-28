def mm1():
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
  query_time_end = '12:15:00'
  query_time_start = '12:00:00'
  sql = f'''
  SELECT [cam_timestamp],[number_of_people] from [dbo].[Stall1_Cam] 
  WHERE [time_only] BETWEEN '{query_time_start}' AND '{query_time_end}'
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
  '''



  '''

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

  print('\nAverage wait:', average_wait, 'time unit')
  # return average_wait
mm1()