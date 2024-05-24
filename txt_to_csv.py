import json
import csv

# Open the text file for reading
with open('./alert.txt', 'r') as file:
    lines = file.readlines()

# Define CSV file header
header = ['pm10', 'pm25', 'genTime', 'recTime', 'transTime']

# Open a CSV file for writing
with open('alert.csv', 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(header)

    # Iterate through each line in the text file
    for line in lines:
        # Extract the Properties part
        properties_str = line.split("Properties: ")[1]

        # Parse the JSON string
        properties = json.loads(properties_str)

        # Write the properties to the CSV file
        writer.writerow([properties['pm10'], properties['pm25'], properties['genTime'], properties['recTime'], properties['transTime']])
