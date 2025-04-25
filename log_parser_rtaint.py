import re
from collections import defaultdict

# Log file path
logfile = "formatter_logs.txt"

# Regex pattern to capture:
#   - Full file path with line number: (/Users/.../something.java:\d+)
#   - Warning message
#   - Parameter name
#   - Fully qualified method name (e.g. PrintStream.println or Statement.executeQuery)
pattern = re.compile(
    r'^(?P<file>/\S+\.java:\d+): warning: \[argument\] incompatible argument for parameter (?P<param>\S+) of (?P<methodFull>\S+\.\S+)\.'
)

# Dictionary keyed by (methodFull, paramName) -> set of files
method_param_map = defaultdict(set)

with open(logfile, 'r', encoding='utf-8') as f:
    for line in f:
        match = pattern.search(line.strip())
        if match:
            file_info = match.group('file')
            param_name = match.group('param')
            method_full = match.group('methodFull')

            # If you only want the actual method name (e.g. "println" from "PrintStream.println"), 
            # you can do the following:
            # method_name = method_full.split('.')[-1]
            # For this example, we will keep the fully qualified method name as is.
            
            method_param_map[(method_full, param_name)].add(file_info)

# Print the results
for (method_full, param_name), files in sorted(method_param_map.items()):
    print(f"Method: {method_full}, Parameter: {param_name}")
    for f in sorted(files):
        print(f"  {f}")
    print()
