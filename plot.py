import matplotlib.pyplot as plt
from datetime import datetime
import re
import json

# File path
file_path = "./alert_1_t.txt"

# Variables to store data for plotting
batch_timestamps = []
time_differences = []

# Counters
line_count = 0
sum = 0
batch = 100
e = 0

# Open the file and process each line
with open(file_path, 'r') as file:
    for line in file:
        # Increment line count
        line_count += 1
        e += 1
        pattern = r'Properties: (\{.*\})'
        match = re.search(pattern, line)

        if match:
            properties_json_string = match.group(1)
            properties_dict = json.loads(properties_json_string)
            received_time = properties_dict.get('receivedTime')
            transformed_time = properties_dict.get('transformedTime')

        # Convert to datetime object
        time_diff = transformed_time - received_time
        sum += time_diff


        # Check if 1000 lines have been processed
        if line_count == batch:
            time_differences.append(sum/batch)

            # Store the timestamp of the batch
            batch_timestamps.append(e)

            # Reset counters
            sum = 0
            line_count = 0

# Plotting
plt.figure(figsize=(10, 6))
plt.plot(batch_timestamps, time_differences, marker='o', linestyle='-')
plt.title('avg transformation time taken for every n events transformed')
plt.xlabel('--------->')
plt.ylabel('avg transformation time (n events) (milliseconds)')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()
