import os
import javalang
from collections import defaultdict

def traverse_java_files(root_dir):
    java_files = []
    for subdir, _, files in os.walk(root_dir):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(subdir, file))
    return java_files

def extract_method_calls(java_code):
    sources = []
    sinks = []
    try:
        tree = javalang.parse.parse(java_code)
    except javalang.parser.JavaSyntaxError as e:
        print(f"Syntax error while parsing: {e}")
        return sources, sinks
    except Exception as e:
        print(f"Error while parsing: {e}")
        return sources, sinks

    for path, node in tree.filter(javalang.tree.MethodInvocation):
        qualifier = node.qualifier if node.qualifier else "Unqualified"
        method_name = node.member

        # Determine if source or sink based on arguments
        if len(node.arguments) == 0:
            # No arguments: considered a source
            sources.append((qualifier, method_name))
        else:
            # One or more arguments: considered a sink
            sinks.append((qualifier, method_name))

    return sources, sinks

def main():
    # Define the root directory containing Java files
    root_dir = "./"  # Adjust this path as needed

    java_files = traverse_java_files(root_dir)
    print(f"Found {len(java_files)} Java files.")

    # Dictionaries to hold method calls per file
    file_sources_dict = defaultdict(list)
    file_sinks_dict = defaultdict(list)

    # Sets to store all unique sources and sinks
    all_sources_set = set()
    all_sinks_set = set()

    # Process each Java file
    for java_file in java_files:
        with open(java_file, 'r', encoding='utf-8') as f:
            java_code = f.read()
        sources, sinks = extract_method_calls(java_code)

        file_sources_dict[java_file] = sources
        file_sinks_dict[java_file] = sinks

        # Add to the global sets of sources and sinks
        for qualifier, method in sources:
            all_sources_set.add((qualifier, method))
        for qualifier, method in sinks:
            all_sinks_set.add((qualifier, method))

    # Output the results per file
    for file, sources in file_sources_dict.items():
        print(f"\nSource methods in {file}:")
        for qualifier, method in sources:
            print(f"  {qualifier}.{method}()")

    for file, sinks in file_sinks_dict.items():
        print(f"\nSink methods in {file}:")
        for qualifier, method in sinks:
            print(f"  {qualifier}.{method}()")

    # Output the consolidated list of all unique sources and sinks
    print("\nAll unique source methods encountered:")
    for qualifier, method in sorted(all_sources_set):
        print(f"{qualifier}.{method}()")

    print("\nAll unique sink methods encountered:")
    for qualifier, method in sorted(all_sinks_set):
        print(f"{qualifier}.{method}()")

    # Optionally, write the results to files
    with open("source_methods_report.txt", "w", encoding='utf-8') as report:
        report.write("All unique source methods encountered:\n")
        for qualifier, method in sorted(all_sources_set):
            report.write(f"{qualifier}.{method}()\n")

    with open("sink_methods_report.txt", "w", encoding='utf-8') as report:
        report.write("All unique sink methods encountered:\n")
        for qualifier, method in sorted(all_sinks_set):
            report.write(f"{qualifier}.{method}()\n")

    print("\nA consolidated list of all source methods has been written to source_methods_report.txt")
    print("A consolidated list of all sink methods has been written to sink_methods_report.txt")

if __name__ == "__main__":
    main()
