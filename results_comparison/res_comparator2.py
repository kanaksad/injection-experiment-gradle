import re
from collections import defaultdict

################################################################################
# PARSERS
################################################################################

def parse_log1_line(line):
    """
    Parse a line from Log1 (e.g., base.txt).
    We ignore method/parameter details and only extract:
      - an optional prefix (TA, TA2, or FA) if it's the first token
      - the file path
      - the line number
    """
    # Identify prefix if it matches known tokens
    parts = line.split(None, 1)
    prefix_candidate = parts[0] if len(parts) > 0 else ""
    known_prefixes = ["TA", "TA2", "FA"]
    prefix = prefix_candidate if prefix_candidate in known_prefixes else None

    # Regex to extract just file path and line number
    # Example: com/lbp/SessionListenerClasses/RemoveSession.java:25:
    pattern = r'(?P<path>[\w/]+\.java):(?P<line>\d+):'
    match = re.search(pattern, line)
    if match:
        file_path = match.group('path')
        line_num = int(match.group('line'))
        return file_path, line_num, prefix, line
    else:
        return None, None, prefix, line


def parse_log2_line(line):
    """
    Parse a line from Log2 (e.g., rtainting.txt).
    We only extract:
      - the file path
      - the line number
    """
    # Regex to get file path and line number
    # Example: com/lbp/jspclasses/Chance.java:53: warning: [argument] ...
    pattern = r'(?P<path>[\w/]+\.java):(?P<line>\d+):'
    match = re.search(pattern, line)
    if match:
        file_path = match.group('path')
        line_num = int(match.group('line'))
        return file_path, line_num
    else:
        return None, None

################################################################################
# READING LOGS
################################################################################

def read_log1(filename):
    """
    Reads Log1 file (like base.txt) using parse_log1_line.
    Returns:
      entries: [(file_path, line_num, prefix, raw_line), ...]
      unparsed: [line, ...]   # lines that didn't parse
    """
    entries = []
    unparsed = []
    with open(filename, 'r', encoding='utf-8') as f:
        for line in f:
            raw = line.strip()
            if not raw:
                continue
            file_path, line_num, prefix, raw_line = parse_log1_line(raw)
            if file_path and line_num:
                entries.append((file_path, line_num, prefix, raw_line))
            else:
                unparsed.append(raw)
    return entries, unparsed

def read_log2(filename):
    """
    Reads Log2 file (like rtainting.txt) using parse_log2_line.
    Returns:
      entries: [(file_path, line_num, raw_line), ...]
      unparsed: [line, ...]   # lines that didn't parse
    """
    entries = []
    unparsed = []
    with open(filename, 'r', encoding='utf-8') as f:
        for line in f:
            raw = line.strip()
            if not raw:
                continue
            file_path, line_num = parse_log2_line(raw)
            if file_path and line_num:
                entries.append((file_path, line_num, raw))
            else:
                unparsed.append(raw)
    return entries, unparsed

################################################################################
# MATCHING LOG ENTRIES
################################################################################

def find_matches(log1_entries, log2_entries):
    """
    Attempt to match each Log1 entry with a Log2 entry if:
      - same file path
      - line number within +/- 1
    Return:
      matched_log1: set of matched indices in log1_entries
      matched_log2: set of matched indices in log2_entries
    """
    matched_log1 = set()
    matched_log2 = set()

    for i, (f1, l1, prefix, raw1) in enumerate(log1_entries):
        for j, (f2, l2, raw2) in enumerate(log2_entries):
            if j in matched_log2:
                continue
            # Match criteria
            if f1 == f2 and abs(l1 - l2) <= 1:
                matched_log1.add(i)
                matched_log2.add(j)
                break
    return matched_log1, matched_log2

################################################################################
# MAIN
################################################################################

def main():
    log1_file = 'Colossus/base.txt'
    log2_file = 'Colossus/rtainting.txt'

    # Read logs
    log1_entries, log1_unparsed = read_log1(log1_file)
    log2_entries, log2_unparsed = read_log2(log2_file)

    # Perform matching
    matched_log1, matched_log2 = find_matches(log1_entries, log2_entries)

    # Basic stats for lines
    total_log1 = len(log1_entries)
    total_log2 = len(log2_entries)
    matched_count = len(matched_log1)

    unmatched_log1 = [e for i, e in enumerate(log1_entries) if i not in matched_log1]
    unmatched_log2 = [e for i, e in enumerate(log2_entries) if i not in matched_log2]

    # Prefix counts (TA, TA2, FA) among matched entries
    ta_count = 0
    ta2_count = 0
    fa_count = 0
    for i in matched_log1:
        _, _, prefix, _ = log1_entries[i]
        if prefix == "TA":
            ta_count += 1
        elif prefix == "TA2":
            ta2_count += 1
        elif prefix == "FA":
            fa_count += 1

    # FILE-LEVEL STATS
    # 1. Distinct file sets in each log
    log1_files = {f for (f, _, _, _) in log1_entries}
    log2_files = {f for (f, _, _) in log2_entries}

    log1_file_count = len(log1_files)
    log2_file_count = len(log2_files)

    # 2. Files that appear in both logs
    common_files = log1_files.intersection(log2_files)
    common_file_count = len(common_files)

    # 3. Files present only in Log1 or only in Log2
    log1_only_files = log1_files - log2_files
    log2_only_files = log2_files - log1_files

    # Write results to output
    output_file = 'Colossus/comparison_output.txt'
    with open(output_file, 'w', encoding='utf-8') as outfile:
        def outprint(*args, **kwargs):
            # Print to console
            print(*args, **kwargs)
            # Print to file
            print(*args, file=outfile, **{k: v for k, v in kwargs.items() if k != 'file'})

        outprint("=== Summary ===")
        outprint(f"Total lines in Log1: {total_log1}")
        outprint(f"Total lines in Log2: {total_log2}")
        outprint(f"Matches found: {matched_count}")
        outprint(f"Unmatched lines in Log1: {len(unmatched_log1)}")
        outprint(f"Unmatched lines in Log2: {len(unmatched_log2)}")
        outprint(f"Matched TA from Log1: {ta_count}")
        outprint(f"Matched TA2 from Log1: {ta2_count}")
        outprint(f"Matched FA from Log1: {fa_count}")

        outprint("\n=== File-Level Stats ===")
        outprint(f"Distinct files in Log1: {log1_file_count}")
        outprint(f"Distinct files in Log2: {log2_file_count}")
        outprint(f"Files present in both logs: {common_file_count}")

        if log1_only_files:
            outprint("\nFiles only in Log1:")
            for fname in sorted(log1_only_files):
                outprint(f"  {fname}")

        if log2_only_files:
            outprint("\nFiles only in Log2:")
            for fname in sorted(log2_only_files):
                outprint(f"  {fname}")

        # Unmatched details
        if unmatched_log1:
            outprint("\n--- Unmatched lines from Log1 ---")
            for (fpath, lnum, prefix, raw_line) in unmatched_log1:
                outprint(raw_line)

        if unmatched_log2:
            outprint("\n--- Unmatched lines from Log2 ---")
            for (fpath, lnum, raw_line) in unmatched_log2:
                outprint(raw_line)

        # Unparsed lines
        if log1_unparsed:
            outprint("\n--- Unparsed lines from Log1 ---")
            for line in log1_unparsed:
                outprint(line)

        if log2_unparsed:
            outprint("\n--- Unparsed lines from Log2 ---")
            for line in log2_unparsed:
                outprint(line)

if __name__ == '__main__':
    main()
