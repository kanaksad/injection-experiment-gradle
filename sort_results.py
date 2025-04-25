import re

input_log = "errors.log"   # Input log file
output_file = "sorted_errors.txt"  # Output file

# Regex to extract the filename and also separate the numeric suffix
# It will match something like "Basic33" into:
#   group(1): "Basic"
#   group(2): "33"
filename_pattern = re.compile(r'(?P<path>/\S+/(?P<filename>(?P<base>[A-Za-z_]+)(?P<num>\d*))\.java):')

lines_with_info = []

with open(input_log, 'r', encoding='utf-8') as f:
    for line in f:
        match = filename_pattern.search(line)
        if match:
            filename = match.group('filename')
            base = match.group('base')
            num_str = match.group('num')
            
            # If no numeric suffix, treat as 0
            num_val = int(num_str) if num_str.isdigit() else 0
            
            # Store tuple: (base, num_val, full_line)
            lines_with_info.append((base, num_val, line.rstrip('\n')))

# Sort by base name and then by numeric suffix
lines_with_info.sort(key=lambda x: (x[0], x[1]))

total_count = len(lines_with_info)

# Write the sorted lines and total count to the output file
with open(output_file, 'w', encoding='utf-8') as out:
    for _, _, l in lines_with_info:
        print(l + "\n")
        out.write(l + "\n")
    out.write(f"\nTotal: {total_count}\n")
