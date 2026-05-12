import os
import re

model_dir = "src/main/java/com/logistica/model"
java_files = []
for root, dirs, files in os.walk(model_dir):
    for f in files:
        if f.endswith(".java"):
            java_files.append(os.path.join(root, f))

for filepath in java_files:
    with open(filepath, 'r', encoding='utf-8') as file:
        content = file.read()
        
        # package
        pkg = re.search(r'package\s+([a-zA-Z0-9_.]+);', content)
        pkg_name = pkg.group(1) if pkg else ""
        
        # class/interface/enum
        # match public (abstract) (class|interface|enum) Name
        
        lines = content.split('\n')
        
        class_signature = ""
        fields = []
        methods = []
        
        in_class = False
        brace_count = 0
        
        for line in lines:
            line = line.strip()
            if not line or line.startswith("//") or line.startswith("*") or line.startswith("/*"):
                continue
            
            if not in_class:
                if "class " in line or "interface " in line or "enum " in line:
                    if "public" in line or "abstract" in line or line.startswith("class") or line.startswith("interface") or line.startswith("enum"):
                        # remove {
                        sig = line.replace("{", "").strip()
                        print(f"[{pkg_name}] {sig}")
                        in_class = True
                        brace_count = line.count("{") - line.count("}")
                        continue
            else:
                brace_count += line.count("{") - line.count("}")
                if brace_count == 1:
                    # field or method
                    if re.match(r'^(private|protected|public)\s+[\w<>]+\s+\w+.*(?:;|)$', line) and "(" not in line:
                        print("  " + line)
                    elif re.match(r'^(private|protected|public)\s+[\w<>]+\s+\w+\s*\(.*\).*$', line):
                         print("  " + line)
                if brace_count <= 0:
                    in_class = False
