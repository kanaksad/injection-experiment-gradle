import re

def parse_log1_line(line):
    """
    Parse a line from log1.
    Example line:
    TA (attribute) com/lbp/SessionListenerClasses/RemoveSession.java:25: [Injection: ...] ... "executeQuery"

    We extract:
      - The prefix (TA, TA2, FA) if present at the start of the line.
      - file path
      - line number
      - method name
    """
    # Extract prefix if it exists as first token
    parts = line.split(None, 1)
    prefix_candidate = parts[0] if len(parts) > 0 else ""
    known_prefixes = ["TA", "TA2", "FA"]
    prefix = prefix_candidate if prefix_candidate in known_prefixes else None

    # Regex to extract file path, line number, and method name
    # Format: ... com/lbp/jspclasses/Chance.java:52: ... method "executeQuery"
    pattern = r'(?P<path>[\w/]+\.java):(?P<line>\d+):.*method\s+"(?P<method>[^"]+)"'
    match = re.search(pattern, line)
    if match:
        file_path = match.group('path')
        line_num = int(match.group('line'))
        method_name = match.group('method')
        return file_path, line_num, method_name, prefix, line
    else:
        # If we can't parse properly, return None for path/line/method but still keep prefix and raw line
        return None, None, None, prefix, line

def parse_log2_line(line):
    """
    Parse a line from log2.
    Example line:
    com/lbp/jspclasses/Chance.java:53: warning: [argument] incompatible argument for parameter arg0 of Statement.executeQuery.

    We'll extract:
      - file path
      - line number
      - method name (normalized to the part after the last dot)
    """
    # Regex to get file path and line
    # After line number, we have something like: warning: [argument] ... parameter ... of Statement.executeQuery.
    pattern = r'(?P<path>[\w/]+\.java):(?P<line>\d+):.*of\s+([\w\.]+)'
    match = re.search(pattern, line)
    if match:
        file_path = match.group('path')
        line_num = int(match.group('line'))
        # Extract method part from the full line
        # Everything after "of " until the end.
        method_full = line.strip().split("of")[-1].strip('. \n')
        # Normalize method name (take last part after dot)
        if '.' in method_full:
            method_name = method_full.split('.')[-1]
        else:
            method_name = method_full
        return file_path, line_num, method_name
    else:
        return None, None, None

def normalize_method_name(method_name):
    # Split by dot and take the last part, then strip whitespace
    return method_name.split('.')[-1].strip()

def read_log(filename, parser_func):
    entries = []
    with open(filename, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            parsed = parser_func(line)
            # parsed may return different tuples depending on log type
            # For log1: returns (fpath, lnum, mname, prefix, raw)
            # For log2: we need to ensure consistent tuple length
            if len(parsed) == 5:
                fpath, lnum, mname, prefix, raw = parsed
            else:
                # This is for log2 which returns 3 values, we add prefix=None, raw=line for consistency
                fpath, lnum, mname = parsed
                prefix = None
                raw = line

            if fpath and lnum and mname:
                mname = normalize_method_name(mname)
                entries.append((fpath, lnum, mname, prefix, raw))
    return entries

def find_matches(log1_entries, log2_entries):
    matched_log1 = set()
    matched_log2 = set()

    for i, (f1, l1, m1, prefix1, raw1) in enumerate(log1_entries):
        for j, (f2, l2, m2, prefix2, raw2) in enumerate(log2_entries):
            if j in matched_log2:
                continue
            # Match criteria: same file, method name, and line number within ±1
            if f1 == f2 and m1 == m2 and abs(l1 - l2) <= 1:
                matched_log1.add(i)
                matched_log2.add(j)
                break
    return matched_log1, matched_log2

def main():
    # Modify these file names as needed
    log1_file = 'Colossus/base.txt'
    log2_file = 'Colossus/rtainting.txt'

    log1_entries = read_log(log1_file, parse_log1_line)
    log2_entries = read_log(log2_file, parse_log2_line)

    matched_log1, matched_log2 = find_matches(log1_entries, log2_entries)

    # Stats
    total_log1 = len(log1_entries)
    total_log2 = len(log2_entries)
    matched_count = len(matched_log1)
    unmatched_log1 = [e for i, e in enumerate(log1_entries) if i not in matched_log1]
    unmatched_log2 = [e for i, e in enumerate(log2_entries) if i not in matched_log2]

    # Count how many matched entries from log1 are TA, TA2, FA
    ta_count = 0
    ta2_count = 0
    fa_count = 0
    for i in matched_log1:
        entry = log1_entries[i]
        _, _, _, prefix, _ = entry
        if prefix == "TA":
            ta_count += 1
        elif prefix == "TA2":
            ta2_count += 1
        elif prefix == "FA":
            fa_count += 1

    output_file = 'Colossus/comparison_output.txt'
    with open(output_file, 'w') as outfile:
        def outprint(*args, **kwargs):
            # Print to console
            print(*args, **kwargs)
            # Print to file
            print(*args, file=outfile, **{k: v for k, v in kwargs.items() if k != 'file'})
        
        outprint("=== Summary ===")
        outprint(f"Total in Log1: {total_log1}")
        outprint(f"Total in Log2: {total_log2}")
        outprint(f"Matches found: {matched_count}")
        outprint(f"Unmatched in Log1: {len(unmatched_log1)}")
        outprint(f"Unmatched in Log2: {len(unmatched_log2)}")
        outprint(f"Matched TA from Log1: {ta_count}")
        outprint(f"Matched TA2 from Log1: {ta2_count}")
        outprint(f"Matched FA from Log1: {fa_count}")

        if unmatched_log1:
            outprint("\nUnmatched entries from Log1:")
            for fpath, lnum, mname, prefix, raw in unmatched_log1:
                outprint(raw)

        if unmatched_log2:
            outprint("\nUnmatched entries from Log2:")
            for fpath, lnum, mname, prefix, raw in unmatched_log2:
                outprint(raw)

if __name__ == '__main__':
    main()
