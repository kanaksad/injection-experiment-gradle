import re
from collections import defaultdict

# Specify the log file
logfile = "snake_and_ladder.txt"

# Regex pattern to capture:
# - Filename and line number: ([\w\/]+\.java:\d+)
# - Argument index: "arg (\d+)"
# - Method name: method "([^"]+)"
pattern = re.compile(r'(?P<file>[\w\/]+\.java:\d+).*"arg (\d+)" of method "([^"]+)"')

# Dictionary keyed by (method_name, arg_num) -> set of files
method_args_map = defaultdict(set)

# Read from the file line by line
with open(logfile, 'r', encoding='utf-8') as f:
    for line in f:
        match = pattern.search(line)
        if match:
            file_info = match.group('file')
            arg_num = match.group(2)
            method_name = match.group(3)
            method_args_map[(method_name, arg_num)].add(file_info)

# Print the results
for (method_name, arg_num), files in sorted(method_args_map.items()):
    print(f"Method: {method_name}, Arg: {arg_num}")
    for f in sorted(files):
        print(f"  {f}")
    print()
