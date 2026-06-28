# Script to clear version specific build folders
# Sometimes fletching table plugin starts being dumb and not actually registering mixins, especially when switching
# branches or disabling subprojects, this seems to fix it, run before buildAndCollect

import shutil
import os
import glob

# Get the versions directory
versions_dir = os.path.dirname(os.path.abspath(__file__))

# Find all version subdirectories
version_folders = [d for d in glob.glob(
    os.path.join(versions_dir, "*")) if os.path.isdir(d)]

# Clear builds folder in each version
for folder in version_folders:
    builds_path = os.path.join(folder, "build")
    if os.path.exists(builds_path):
        print(f"Clearing {builds_path}")
        shutil.rmtree(builds_path)
        print(f"Cleared {builds_path}")

print("Done clearing all version builds")
